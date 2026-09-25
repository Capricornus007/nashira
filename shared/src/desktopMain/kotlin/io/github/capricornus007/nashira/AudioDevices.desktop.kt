package io.github.capricornus007.nashira

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.DataLine
import javax.sound.sampled.Mixer
import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine

/**
 * 桌面音訊裝置枚舉（javax.sound.sampled Mixer）。
 *
 * 只列「真的支援我們用的線型」的 mixer：輸入用錄音規格
 * （16kHz/16bit/單聲道，VoiceRecorder 的格式），輸出用 Clip
 * （AudioPlayer 的 WAV 直解路徑）——避免把 MIDI 之類的埠全塞進列表。
 *
 * 顯示名是另一回事：javax.sound 的 Mixer 名其實是 ALSA 路徑
 * （`alsa_playback.java [default]`、`Generic_1 [plughw:1,0]`），人看不出那是
 * 喇叭還是麥克風。所以再問一次 PipeWire/PulseAudio（`pactl`）拿節點描述
 * （「Ryzen HD Audio Controller Speaker」），按 `api.alsa.path` 對回去。
 * 問不到（沒裝 pactl、純 ALSA、解析失敗）就原樣顯示 id——
 * 寧可難看也不編一個可能錯的名字。
 */
actual object AudioDevices {
    private val recordFormat = AudioFormat(16_000f, 16, 1, true, false)
    private val playFormat = AudioFormat(44_100f, 16, 2, true, false)

    actual fun inputs(): List<AudioDevice> = list(TargetDataLine::class.java, recordFormat, sinks = false)

    actual fun outputs(): List<AudioDevice> = list(SourceDataLine::class.java, playFormat, sinks = true)

    actual fun defaultInputLabel(): String? = NativeAudioNodes.defaultSource()?.description

    actual fun defaultOutputLabel(): String? = NativeAudioNodes.defaultSink()?.description

    private fun list(
        lineType: Class<out javax.sound.sampled.Line>,
        format: AudioFormat,
        sinks: Boolean,
    ): List<AudioDevice> = runCatching {
        val nodes = if (sinks) NativeAudioNodes.sinks() else NativeAudioNodes.sources()
        val def = if (sinks) NativeAudioNodes.defaultSink() else NativeAudioNodes.defaultSource()
        AudioSystem.getMixerInfo()
            .filter { info ->
                runCatching {
                    val mixer = AudioSystem.getMixer(info)
                    mixer.isLineSupported(DataLine.Info(lineType, format)) ||
                        (sinks && mixer.isLineSupported(DataLine.Info(Clip::class.java, format)))
                }.getOrDefault(false)
            }
            .map { info ->
                AudioDevice(id = info.name, label = NativeAudioNodes.labelFor(info.name, info.description, nodes, def))
            }
    }.getOrDefault(emptyList())
}

/** 依名字找 mixer info；null／找不到回 null（呼叫端走系統預設）。 */
internal fun mixerInfoFor(name: String?): Mixer.Info? {
    if (name.isNullOrBlank()) return null
    return runCatching {
        AudioSystem.getMixerInfo().firstOrNull { it.name == name }
    }.getOrNull()
}

/** 依名字找 mixer；null／找不到回系統預設 mixer。 */
internal fun mixerFor(name: String?): Mixer =
    mixerInfoFor(name)?.let { runCatching { AudioSystem.getMixer(it) }.getOrNull() }
        ?: AudioSystem.getMixer(null)

/**
 * 音訊服務（PipeWire 的 pulse 相容層或純 PulseAudio）那邊的節點描述。
 * 整個類別只在桌面用，且一律 runCatching：沒有 pactl 時當作查不到。
 */
internal object NativeAudioNodes {
    /**
     * 一個播放／錄製節點：名字（`alsa_output.pci-…__Speaker__sink`）＋人話描述
     * ＋ALSA 路徑（`api.alsa.path`，如 `hw:Generic_1`、`hw:0,3`）。
     *
     * 為什麼不用看起來更直觀的 `alsa.card`／`alsa.device`：UCM 把同一張卡的
     * 好幾個裝置（內建喇叭、Mic1 數位麥、Mic2 類比麥）全回報成同一組數字
     * 卡號／裝置號。實測這台機器上兩個麥克風節點都是 `alsa.card=1`＋
     * `alsa.device=0`，只有 `api.alsa.path` 分得開（`hw:Generic_1` 與 `hw:acp`）。
     * 拿數字那組去對，兩列 mixer 會各自誤配上同一個節點——用戶截圖裡的
     * 「重複列＋亂碼名」就是這麼來的。
     */
    data class Node(val name: String, val description: String, val path: String?)

    private val sinkLine = Regex("""^\s*Name:\s*(\S+)\s*$""")
    private val sinkDesc = Regex("""^\s*Description:\s*(.+?)\s*$""")
    private val propPath = Regex("""^\s*api\.alsa\.path\s*=\s*"([^"]*)"""")
    private val mixerPath = Regex("""\[(?:plug)?hw:([^\]]+)\]\s*$""")

    private val sinkCache: List<Node> by lazy { query("sinks") }
    private val sourceCache: List<Node> by lazy {
        // 輸出節點的 monitor source 不是麥克風，別混進輸入清單
        query("sources").filterNot { it.name.endsWith(".monitor") || it.description.startsWith("Monitor of") }
    }
    private val defaultSinkNode: Node? by lazy { defaultOf("sink", sinkCache) }
    private val defaultSourceNode: Node? by lazy { defaultOf("source", sourceCache) }

    fun sinks(): List<Node> = sinkCache
    fun sources(): List<Node> = sourceCache
    fun defaultSink(): Node? = defaultSinkNode
    fun defaultSource(): Node? = defaultSourceNode

    /**
     * 把 javax.sound 的 mixer 名換成人話，依序試：
     * 1. `[default]`（ALSA 預設 pcm）→ 音訊服務的預設節點描述
     * 2. `[plughw:C,D]` / `[hw:C,D]` → ALSA 路徑對得上的節點描述；同一條路徑
     *    常掛好幾個節點（UCM 的 Mic1/Mic2 都算同一張卡），這時取「目前預設
     *    的那個」，再不然取第一個——最不容易說錯的取捨。
     * 3. 音訊服務那邊查無此裝置（例：HDMI 口沒接、PipeWire 就不會建節點）→
     *    用 mixer 自己的描述（見 [alsaHint]），至少是「HD-Audio Generic · HDMI 0」
     * 4. 連描述都沒有才原樣回 id。
     */
    fun labelFor(mixerName: String, mixerDescription: String, nodes: List<Node>, fallbackDefault: Node?): String {
        if (mixerName.endsWith("[default]")) {
            return fallbackDefault?.description ?: alsaHint(mixerDescription) ?: mixerName
        }
        val m = mixerPath.find(mixerName)?.groupValues?.getOrNull(1) ?: return mixerName
        val key = pathKey(m) ?: return mixerName
        val hits = nodes.filter { pathKey(it.path) == key }
        val node = hits.firstOrNull { fallbackDefault != null && it.name == fallbackDefault.name }
            ?: hits.firstOrNull()
        return node?.description ?: alsaHint(mixerDescription) ?: mixerName
    }

    /**
     * 把一條 ALSA 路徑拆成可比較的 (卡, 裝置)：去掉 `hw:`／`plug` 前綴、缺省
     * 裝置號補 0，並把**數字卡號換成卡名**（`/proc/asound/card1/id`）。
     * 兩邊都過這道手：mixer 一定給數字（`plughw:1,0`），PipeWire 節點多半給
     * 卡名（`hw:Generic_1`）、但純 ALSA 或老版本也可能是數字——不比掉這個
     * 差異就會全都落到 fallback。
     */
    private fun pathKey(raw: String?): Pair<String, String>? {
        val body = raw?.trim()?.removePrefix("plug")?.removePrefix("hw:")?.takeIf { it.isNotEmpty() }
            ?: return null
        val parts = body.split(',')
        return cardName(parts[0].trim()) to (parts.getOrNull(1)?.trim()?.ifBlank { null } ?: "0")
    }

    private fun cardName(token: String): String {
        val index = token.toIntOrNull() ?: return token
        return runCatching { java.io.File("/proc/asound/card$index/id").readText().trim() }
            .getOrDefault(token)
            .takeIf { it.isNotEmpty() } ?: token
    }

    /**
     * javax.sound 的 Mixer 描述長這樣：
     * `Direct Audio Device: HD-Audio Generic, HDMI 0, HDMI 0`
     * 去掉前綴、重複欄位與空欄，湊成「HD-Audio Generic · HDMI 0」。
     */
    private fun alsaHint(description: String): String? {
        val parts = description.removePrefix("Direct Audio Device:")
            .split(',')
            .map { it.trim() }
            // 「capture」／「playback」是重複資訊：那一欄標題已經寫明是輸入裝置（麥克風）
            // 還是輸出裝置（喇叭），留在名字裡只會讓人以為它是型號的一部分。
            .map { it.replace(Regex("""\s+\b(?:capture|playback)\b""", RegexOption.IGNORE_CASE), "").trim() }
            .filter { it.isNotEmpty() }
            .distinct()
        return parts.joinToString(" · ").takeIf { it.isNotBlank() }
    }

    private fun defaultOf(kind: String, nodes: List<Node>): Node? = runCatching {
        val name = runProcess("pactl", "get-default-$kind").trim().takeIf { it.isNotEmpty() } ?: return null
        nodes.firstOrNull { it.name == name }
    }.getOrNull()

    private fun query(what: String): List<Node> = runCatching {
        val text = runProcess("pactl", "list", what)
        val out = ArrayList<Node>()
        var name: String? = null
        var desc: String? = null
        var path: String? = null
        fun flush() {
            val n = name
            val d = desc
            if (n != null && !d.isNullOrBlank()) out += Node(n, d, path)
            name = null; desc = null; path = null
        }
        for (line in text.lineSequence()) {
            when {
                line.contains("Sink #") || line.contains("Source #") -> flush()
                else -> {
                    sinkLine.find(line)?.let { name = it.groupValues[1] }
                    sinkDesc.find(line)?.let { desc = it.groupValues[1] }
                    propPath.find(line)?.let { path = it.groupValues[1] }
                }
            }
        }
        flush()
        out
    }.getOrDefault(emptyList())

    private fun runProcess(vararg cmd: String): String = runCatching {
        val p = ProcessBuilder(*cmd).redirectErrorStream(true).start()
        val text = p.inputStream.bufferedReader().readText()
        if (!p.waitFor(3, java.util.concurrent.TimeUnit.SECONDS)) {
            p.destroy()
            return ""
        }
        if (p.exitValue() != 0) "" else text
    }.getOrDefault("")
}
