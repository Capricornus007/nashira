package io.github.capricornus007.nashira.matrix

import android.media.MediaPlayer
import java.io.File

/**
 * Android 播放：MediaPlayer。它只吃檔案/fd，把位元組寫進 cacheDir 臨時檔再播；
 * release 時刪檔。m4a、WAV、OGG/Opus 系統都有解碼器。
 */
actual class AudioPlayer actual constructor() {
    private var player: MediaPlayer? = null
    private var tempFile: File? = null

    actual fun prepare(bytes: ByteArray, mimeType: String?): Boolean {
        release()
        val ctx = TokenStorage.context ?: return false
        return try {
            val dir = File(ctx.cacheDir, "voice").apply { mkdirs() }
            val f = File(dir, "play_${System.currentTimeMillis()}.bin")
            f.writeBytes(bytes)
            val p = MediaPlayer()
            p.setDataSource(f.absolutePath)
            p.prepare()
            player = p
            tempFile = f
            true
        } catch (_: Throwable) {
            release()
            false
        }
    }

    actual fun play() {
        player?.start()
    }

    actual fun pause() {
        player?.takeIf { it.isPlaying }?.pause()
    }

    actual fun release() {
        player?.run {
            runCatching { stop() }
            release()
        }
        player = null
        tempFile?.delete()
        tempFile = null
    }

    actual fun positionMs(): Long = player?.currentPosition?.toLong() ?: -1L

    actual fun durationMs(): Long = player?.duration?.toLong() ?: -1L

    actual fun isPlaying(): Boolean = player?.isPlaying == true
}
