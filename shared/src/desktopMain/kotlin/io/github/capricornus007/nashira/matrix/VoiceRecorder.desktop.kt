package io.github.capricornus007.nashira.matrix

import io.github.capricornus007.nashira.AudioSelection
import io.github.capricornus007.nashira.mixerFor
import java.io.ByteArrayOutputStream
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine
import kotlin.concurrent.thread

/**
 * 桌面錄音：TargetDataLine 抓 16kHz/16bit/單聲道 PCM，stop 時封成 WAV。
 * javax.sound 不含 AAC/Opus 編碼器，WAV 是零依賴又人人能播的選擇
 * （16kHz 單聲道一分鐘約 1.9MB，語音可接受）。
 */
actual class VoiceRecorder actual constructor() {
    private var line: TargetDataLine? = null
    private var reader: Thread? = null
    private val pcm = ByteArrayOutputStream()
    private var startedAt = 0L
    // 最近一個讀取块的峰值電平（0..1）：javax.sound 沒有電平 API，但 PCM 本来就
    // 從我們手上過，自己算 peak 就有與 Android maxAmplitude 同標度的值。
    @Volatile private var lastAmp = 0f

    actual fun start() {
        if (AudioSelection.micMuted) return  // 底欄麥克風靜音（Discord 語義）
        val format = AudioFormat(16_000f, 16, 1, true, false)
        val info = DataLine.Info(TargetDataLine::class.java, format)
        // 設置頁選了輸入裝置就走那個 mixer（AudioSelection 由 UiState 同步）；
        // 沒選／找不到回系統預設。
        val l = runCatching { mixerFor(AudioSelection.input).getLine(info) as TargetDataLine }
            .getOrElse { AudioSystem.getLine(info) as TargetDataLine }
        l.open(format)
        l.start()
        line = l
        startedAt = System.currentTimeMillis()
        lastAmp = 0f
        synchronized(pcm) { pcm.reset() }
        reader = thread(isDaemon = true, name = "voice-recorder") {
            val chunk = ByteArray(4 * 1024)
            while (l.isOpen) {
                val n = l.read(chunk, 0, chunk.size)
                if (n > 0) {
                    synchronized(pcm) { pcm.write(chunk, 0, n) }
                    lastAmp = peakLevel(chunk, n)
                }
            }
        }
    }

    actual fun stop(): RecordedVoice? {
        val l = line ?: return null
        val duration = System.currentTimeMillis() - startedAt
        line = null
        val bytes = synchronized(pcm) { pcm.toByteArray() }
        l.stop()
        l.close()
        reader?.join(500)
        reader = null
        if (duration < MinDurationMs || bytes.isEmpty()) return null
        return RecordedVoice(pcmToWav(applyGain(bytes)), duration, "audio/wav")
    }

    actual fun cancel() {
        val l = line ?: return
        line = null
        reader = null
        l.stop()
        l.close()
        synchronized(pcm) { pcm.reset() }
    }

    /** 最近一塊 PCM 的峰值（16bit 小端），換算 0..1——與 Android maxAmplitude 同標度。 */
    private fun peakLevel(buf: ByteArray, len: Int): Float {
        var peak = 0
        var i = 0
        while (i + 1 < len) {
            val sample = ((buf[i].toInt() and 0xFF) or (buf[i + 1].toInt() shl 8)).toShort()
            val abs = kotlin.math.abs(sample.toInt())
            if (abs > peak) peak = abs
            i += 2
        }
        return (peak / 32767f).coerceIn(0f, 1f)
    }

    actual fun amplitude(): Float = lastAmp
}

private const val MinDurationMs = 500L

/** 裸 PCM 封 RIFF/WAV 標頭（16bit 小端單聲道）。 */
private fun pcmToWav(pcm: ByteArray): ByteArray {
    val sampleRate = 16_000
    val byteRate = sampleRate * 2
    val out = ByteArrayOutputStream(44 + pcm.size)
    fun le32(v: Int) { out.write(v and 0xFF); out.write((v shr 8) and 0xFF); out.write((v shr 16) and 0xFF); out.write((v shr 24) and 0xFF) }
    fun le16(v: Int) { out.write(v and 0xFF); out.write((v shr 8) and 0xFF) }
    out.write("RIFF".toByteArray()); le32(36 + pcm.size)
    out.write("WAVE".toByteArray())
    out.write("fmt ".toByteArray()); le32(16); le16(1); le16(1)
    le32(sampleRate); le32(byteRate); le16(2); le16(16)
    out.write("data".toByteArray()); le32(pcm.size)
    out.write(pcm)
    return out.toByteArray()
}

/**
 * 輸入增益（AudioSelection.inputGain，0-100）：對 16bit 小端 PCM 樣本線性
 * 縮放並夾到 Short 範圍。100＝原樣。javax.sound 沒有硬體輸入增益 API，
 * 軟體縮放是唯一跨裝置一致的方案（Discord 的輸入音量同理）。
 */
private fun applyGain(pcm: ByteArray): ByteArray {
    val gain = AudioSelection.inputGain
    if (gain >= 100 || pcm.size < 2) return pcm
    val factor = gain / 100.0
    val out = pcm.copyOf()
    var i = 0
    while (i + 1 < out.size) {
        val sample = ((out[i].toInt() and 0xFF) or (out[i + 1].toInt() shl 8)).toShort()
        val scaled = (sample * factor).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
        out[i] = (scaled and 0xFF).toByte()
        out[i + 1] = ((scaled shr 8) and 0xFF).toByte()
        i += 2
    }
    return out
}
