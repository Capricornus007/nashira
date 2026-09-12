package io.github.capricornus007.nashira.matrix

import android.media.MediaRecorder
import android.os.Build
import android.os.SystemClock
import java.io.File

/**
 * Android 錄音：MediaRecorder，AAC 64kbps 單聲道 m4a。VoiceMessage 用 audio/mp4，
 * Element/各家客戶端都直接播。輸出檔放 cacheDir，stop() 讀完即刪，不佔空間。
 */
actual class VoiceRecorder actual constructor() {
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var startedAt = 0L

    actual fun start() {
        val ctx = TokenStorage.context
            ?: error("TokenStorage.context 未注入（MainActivity 應先設置）")
        val dir = File(ctx.cacheDir, "voice").apply { mkdirs() }
        val out = File(dir, "rec_${System.currentTimeMillis()}.m4a")
        @Suppress("DEPRECATION")
        val r = if (Build.VERSION.SDK_INT >= 31) MediaRecorder(ctx) else MediaRecorder()
        try {
            r.setAudioSource(MediaRecorder.AudioSource.MIC)
            r.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            r.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            r.setAudioEncodingBitRate(64_000)
            r.setAudioSamplingRate(44_100)
            r.setAudioChannels(1)
            r.setOutputFile(out.absolutePath)
            r.prepare()
            r.start()
        } catch (t: Throwable) {
            r.release()
            out.delete()
            throw t
        }
        recorder = r
        outputFile = out
        startedAt = SystemClock.elapsedRealtime()
    }

    actual fun stop(): RecordedVoice? {
        val r = recorder ?: return null
        val out = outputFile
        val duration = SystemClock.elapsedRealtime() - startedAt
        recorder = null
        outputFile = null
        return try {
            r.stop()
            val bytes = out?.readBytes() ?: ByteArray(0)
            if (duration < MinDurationMs || bytes.isEmpty()) null
            else RecordedVoice(bytes, duration, "audio/mp4")
        } catch (t: Throwable) {
            null // stop 在來不及錄到資料時會丟 RuntimeException——當作取消
        } finally {
            r.release()
            out?.delete()
        }
    }

    actual fun cancel() {
        val r = recorder ?: return
        recorder = null
        val out = outputFile
        outputFile = null
        try {
            r.stop()
        } catch (_: RuntimeException) {
            // 尚未錄到任何資料就取消：stop 會丟，忽略
        }
        r.release()
        out?.delete()
    }

    actual fun amplitude(): Float =
        recorder?.let { r -> (r.maxAmplitude / 32767f).coerceIn(0f, 1f) } ?: 0f
}

private const val MinDurationMs = 500L
