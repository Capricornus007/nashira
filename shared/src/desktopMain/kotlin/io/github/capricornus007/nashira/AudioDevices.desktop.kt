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
 * （「Ryzen HD Audio Controller Speaker」），按 `alsa.card`/`alsa.device`
 * 對回去。問不到（沒裝 pactl、純 ALSA、解析失敗）就原樣顯示 id——
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
    /** 一個播放／錄製節點：名字（`alsa_output.pci-…__Speaker__sink`）＋人話描述＋ALSA 卡/裝置號。 */
    data class Node(val name: String, val description: String, val card: String?, val device: String?)

    private val sinkLine = Regex("""^\s*Name:\s*(\S+)\s*$""")
    private val sinkDesc = Regex("""^\s*Description:\s*(.+?)\s*$""")
    private val propCard = Regex("""^\s*alsa\.card\s*=\s*"([^"]*)"""")
    private val propDevice = Regex("""^\s*alsa\.device\s*=\s*"([^"]*)"""")
    private val hwPath = Regex("""\[(?:plug)?hw:(\d+),(\d+)\]\s*$""")

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
     * 2. `[plughw:C,D]` / `[hw:C,D]` → 卡/裝置號對得上的節點描述；同一個 (C,D)
     *    常掛好幾個節點（UCM 的 Mic1/Mic2 都算 card1,dev0），這時取「目前預設
     *    的那個」，再不然取第一個——最不容易說錯的取捨。
     * 3. 音訊服務那邊查無此裝置（例：HDMI 口沒接、PipeWire 就不會建節點）→
     *    用 mixer 自己的描述（見 [alsaHint]），至少是「HD-Audio Generic · HDMI 0」
     * 4. 連描述都沒有才原樣回 id。
     */
    fun labelFor(mixerName: String, mixerDescription: String, nodes: List<Node>, fallbackDefault: Node?): String {
        if (mixerName.endsWith("[default]")) {
            return fallbackDefault?.description ?: alsaHint(mixerDescription) ?: mixerName
        }
        val m = hwPath.find(mixerName) ?: return mixerName
        val (card, device) = m.destructured
        val hits = nodes.filter { it.card == card && it.device == device }
        val node = hits.firstOrNull { fallbackDefault != null && it.name == fallbackDefault.name }
            ?: hits.firstOrNull()
        return node?.description ?: alsaHint(mixerDescription) ?: mixerName
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
        var card: String? = null
        var device: String? = null
        fun flush() {
            val n = name
            val d = desc
            if (n != null && !d.isNullOrBlank()) out += Node(n, d, card, device)
            name = null; desc = null; card = null; device = null
        }
        for (line in text.lineSequence()) {
            when {
                line.contains("Sink #") || line.contains("Source #") -> flush()
                else -> {
                    sinkLine.find(line)?.let { name = it.groupValues[1] }
                    sinkDesc.find(line)?.let { desc = it.groupValues[1] }
                    propCard.find(line)?.let { card = it.groupValues[1] }
                    propDevice.find(line)?.let { device = it.groupValues[1] }
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
