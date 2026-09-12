package io.github.capricornus007.nashira.matrix

import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

/**
 * 桌面播放：先試 javax.sound Clip（WAV 直解、有進度）；不是 WAV 就找 ffplay
 * （有 ffmpeg 的機器全格式能播，但拿不到進度，UI 只顯示播放中）。
 */
actual class AudioPlayer actual constructor() {
    private var clip: Clip? = null
    private var ffplay: Process? = null
    private var ffplayFile: File? = null
    private var ffplayCommand: String? = null

    actual fun prepare(bytes: ByteArray, mimeType: String?): Boolean {
        release()
        // 1) javax.sound：RIFF/WAV 可以直接開
        try {
            val input = AudioSystem.getAudioInputStream(ByteArrayInputStream(bytes))
            val c = AudioSystem.getClip()
            c.open(input)
            clip = c
            return true
        } catch (_: Throwable) {
            // 不是 WAV（或音訊系統不可用）→ 走 ffplay
        }
        // 2) ffplay：ffmpeg 自帶的極簡播放器，全格式
        val ffplayPath = sequenceOf("ffplay", "/usr/bin/ffplay")
            .firstOrNull { File(it).canExecute() || which(it) }
            ?: return false
        return try {
            val f = File.createTempFile("nashira-voice-", ".bin")
            f.writeBytes(bytes)
            ffplayFile = f
            ffplayCommand = ffplayPath
            true // 真正的啟動在 play()（-autoexit 一次到底）
        } catch (_: Throwable) {
            false
        }
    }

    actual fun play() {
        clip?.let { it.start(); return }
        val f = ffplayFile ?: return
        val cmd = ffplayCommand ?: return
        if (ffplay?.isAlive == true) return
        ffplay = runCatching {
            ProcessBuilder(cmd, "-nodisp", "-autoexit", "-loglevel", "quiet", f.absolutePath).start()
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
