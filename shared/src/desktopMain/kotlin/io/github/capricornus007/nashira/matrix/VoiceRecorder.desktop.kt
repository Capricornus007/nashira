package io.github.capricornus007.nashira.matrix

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

    actual fun start() {
        val format = AudioFormat(16_000f, 16, 1, true, false)
        val info = DataLine.Info(TargetDataLine::class.java, format)
        val l = AudioSystem.getLine(info) as TargetDataLine
        l.open(format)
        l.start()
        line = l
        startedAt = System.currentTimeMillis()
        synchronized(pcm) { pcm.reset() }
        reader = thread(isDaemon = true, name = "voice-recorder") {
            val chunk = ByteArray(4 * 1024)
            while (l.isOpen) {
                val n = l.read(chunk, 0, chunk.size)
                if (n > 0) synchronized(pcm) { pcm.write(chunk, 0, n) }
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
        return RecordedVoice(pcmToWav(bytes), duration, "audio/wav")
    }

    actual fun cancel() {
        val l = line ?: return
        line = null
        reader = null
        l.stop()
        l.close()
        synchronized(pcm) { pcm.reset() }
    }

    /** javax.sound 沒有麥克風電平 API；回 0 讓 UI 畫靜態圓點。 */
    actual fun amplitude(): Float = 0f
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
