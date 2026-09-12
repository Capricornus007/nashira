package io.github.capricornus007.nashira.matrix

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

/**
 * Android 播放：MediaPlayer。位元組先在後台執行緒寫進 cacheDir 臨時檔，
 * 再 prepareAsync()——同步 prepare() 會把主執行緒凍住（真機實證）。
 * release 時刪檔。m4a、WAV、OGG/Opus 系統都有解碼器。
 */
actual class AudioPlayer actual constructor() {
    private val main = Handler(Looper.getMainLooper())
    private var player: MediaPlayer? = null
    private var tempFile: File? = null
    private val generation = AtomicInteger(0)

    actual fun prepare(bytes: ByteArray, mimeType: String?, onReady: (Boolean) -> Unit) {
        release()
        val myGen = generation.incrementAndGet()
        Thread {
            val ok = runCatching {
                val ctx = TokenStorage.context ?: error("TokenStorage.context 未注入")
                val dir = File(ctx.cacheDir, "voice").apply { mkdirs() }
                val f = File(dir, "play_${System.currentTimeMillis()}.bin")
                f.writeBytes(bytes)
                if (generation.get() != myGen) {
                    f.delete()
                    return@runCatching false
                }
                val p = MediaPlayer()
                p.setDataSource(f.absolutePath)
                p.setOnPreparedListener { mp ->
                    if (generation.get() == myGen) {
                        tempFile = f
                        player = p
                        main.post { onReady(true) }
                    } else {
                        runCatching { mp.release() }
                        f.delete()
                    }
                }
                p.setOnErrorListener { mp, _, _ ->
                    runCatching { mp.release() }
                    f.delete()
                    if (generation.get() == myGen) main.post { onReady(false) }
                    true
                }
                p.prepareAsync()
                true
            }.getOrDefault(false)
            // 寫檔/建 MediaPlayer 階段就失敗（context 缺、IO 錯）
            if (!ok && generation.get() == myGen) main.post { onReady(false) }
        }.apply { isDaemon = true }.start()
    }

    actual fun play() {
        player?.start()
    }

    actual fun pause() {
        player?.takeIf { it.isPlaying }?.pause()
    }

    actual fun release() {
        generation.incrementAndGet()
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
