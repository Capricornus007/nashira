package io.github.capricornus007.nashira.matrix

import io.github.capricornus007.nashira.AudioSelection
import io.github.capricornus007.nashira.mixerInfoFor
import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

/**
 * 桌面播放：先試 javax.sound Clip（WAV 直解）；不是 WAV 就找 ffplay
 * （有 ffmpeg 的機器全格式能播，但拿不到進度）。
 * Clip 開檔在小檔上很快，同步完成後直接回呼。
 */
actual class AudioPlayer actual constructor() {
    private var clip: Clip? = null
    private var ffplay: Process? = null
    private var ffplayFile: File? = null
    private var ffplayCommand: String? = null

    actual fun prepare(bytes: ByteArray, mimeType: String?, onReady: (Boolean) -> Unit) {
        release()
        // 1) javax.sound：RIFF/WAV 可以直接開
        try {
            val input = AudioSystem.getAudioInputStream(ByteArrayInputStream(bytes))
            // 設置頁選了輸出裝置就走那個 mixer；沒選/找不到回系統預設
            val c = mixerInfoFor(AudioSelection.output)?.let { info ->
                runCatching { AudioSystem.getClip(info) }.getOrNull()
            } ?: AudioSystem.getClip()
            c.open(input)
            // 輸出音量（AudioSelection.outputVolume 0-100）：Clip 沒有 setVolume，
            // 走 MASTER_GAIN FloatControl（dB，-80..6）。線性→dB 近似：0% 靜音。
            runCatching {
                val v = AudioSelection.outputVolume / 100f
                val db = when {
                    v >= 1f -> 0f
                    v <= 0f -> -80f
                    else -> (kotlin.math.ln(v) / kotlin.math.ln(2f) * 20f)
                }
                val gainControl = c.getControl(javax.sound.sampled.FloatControl.Type.MASTER_GAIN) as javax.sound.sampled.FloatControl
                gainControl.setValue(db)
            }
            clip = c
            onReady(true)
            return
        } catch (_: Throwable) {
            // 不是 WAV（或音訊系統不可用）→ 走 ffplay
        }
        // 2) ffplay：ffmpeg 自帶的極簡播放器，全格式
        val ffplayPath = sequenceOf("ffplay", "/usr/bin/ffplay")
            .firstOrNull { File(it).canExecute() || which(it) }
            ?: return onReady(false)
        try {
            val f = File.createTempFile("nashira-voice-", ".bin")
            f.writeBytes(bytes)
            ffplayFile = f
            ffplayCommand = ffplayPath
            onReady(true)
        } catch (_: Throwable) {
            onReady(false)
        }
    }

    actual fun play() {
        clip?.let { it.start(); return }
        val f = ffplayFile ?: return
        val cmd = ffplayCommand ?: return
        if (ffplay?.isAlive == true) return
        ffplay = runCatching {
            ProcessBuilder(cmd, "-nodisp", "-autoexit", "-loglevel", "quiet", "-volume", AudioSelection.outputVolume.toString(), f.absolutePath).start()
        }.getOrNull()
    }

    actual fun pause() {
        // Clip.stop() 之後 start() 會從暫停點續播；ffplay 沒有暫停管道，暫停即停
        clip?.let { c -> if (c.isRunning) c.stop() }
        ffplay?.destroy()
        ffplay = null
    }

    actual fun release() {
        clip?.run { runCatching { close() } }
        clip = null
        ffplay?.destroy()
        ffplay = null
        ffplayFile?.delete()
        ffplayFile = null
        ffplayCommand = null
    }

    actual fun positionMs(): Long =
        clip?.let { if (it.microsecondPosition >= 0) it.microsecondPosition / 1000 else -1L } ?: -1L

    actual fun durationMs(): Long =
        clip?.let { if (it.microsecondLength > 0) it.microsecondLength / 1000 else -1L } ?: -1L

    actual fun isPlaying(): Boolean =
        clip?.isRunning == true || ffplay?.isAlive == true

    private fun which(bin: String): Boolean =
        System.getenv("PATH").orEmpty().split(File.pathSeparator).any { dir ->
            File(dir, bin).canExecute()
        }
}
