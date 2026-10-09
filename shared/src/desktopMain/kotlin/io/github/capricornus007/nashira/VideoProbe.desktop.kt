package io.github.capricornus007.nashira

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

private const val ProbeTimeoutSec = 15L

internal actual suspend fun probeVideoForSending(bytes: ByteArray): VideoSendMeta? =
    withContext(Dispatchers.IO) {
        runCatching {
            val file = File.createTempFile("nashira-probe-", ".bin").apply {
                writeBytes(bytes)
                deleteOnExit()
            }
            val text = runCaptureText(
                listOf(
                    "ffprobe", "-v", "error", "-select_streams", "v:0",
                    "-show_entries", "stream=width,height:format=duration",
                    "-of", "default=noprint_wrappers=1",
                    file.absolutePath,
                ),
            )
            var width: Int? = null
            var height: Int? = null
            var durationMs: Long? = null
            text?.lineSequence()?.map { it.trim() }?.forEach { line ->
                when {
                    line.startsWith("width=") -> width = line.removePrefix("width=").toIntOrNull()
                    line.startsWith("height=") -> height = line.removePrefix("height=").toIntOrNull()
                    line.startsWith("duration=") ->
                        durationMs = line.removePrefix("duration=").toDoubleOrNull()?.let { (it * 1000).toLong() }
                }
            }
            // 縮圖：寬度封頂 320（Matrix 建議縮圖不要比本體還大），只取第一格。
            val jpeg = runCapture(
                listOf(
                    "ffmpeg", "-v", "error", "-y", "-i", file.absolutePath,
                    "-vf", "scale='min(320,iw)':-2", "-frames:v", "1",
                    "-f", "image2", "-c:v", "mjpeg", "-q:v", "6", "pipe:1",
                ),
            )
            runCatching { file.delete() }
            VideoSendMeta(
                widthPx = width?.takeIf { it > 0 },
                heightPx = height?.takeIf { it > 0 },
                durationMs = durationMs,
                thumbnailJpeg = jpeg?.takeIf { it.size > 100 },
            )
        }.onFailure { mediaProbe("探測影片失敗：${it::class.simpleName} ${it.message}") }.getOrNull()
    }

/**
 * 跑一條命令把 stdout 收下來（二进制也照原樣）。
 *
 * ⚠️ 兩個一定要記得的坑（都是這輪實測換來的）：
 * - `redirectInput(/dev/null)`：ffmpeg 有任何需要人回話的提示（例如輸出檔已存在問
 *   `Overwrite? [y/N]`）時，會立刻拿到 EOF 變成看得見的錯誤，而不是無聲地永遠卡住。
 * - 收 stdout 要在另一條執行緒：在主執行緒上 `readBytes()` 會堵到行程結束，
 *   後面的 `waitFor(timeout)` 根本輪不到跑。
 */
private fun runCapture(args: List<String>): ByteArray? = runCatching {
    val process = ProcessBuilder(args)
        .redirectError(ProcessBuilder.Redirect.DISCARD)
        .redirectInput(ProcessBuilder.Redirect.from(File("/dev/null")))
        .withoutLauncherEnv()
        .start()
    val collected = CompletableFuture.supplyAsync {
        runCatching { process.inputStream.use { it.readBytes() } }.getOrNull()
    }
    if (!process.waitFor(ProbeTimeoutSec, TimeUnit.SECONDS)) {
        process.destroyForcibly()
        null
    } else if (process.exitValue() != 0) {
        null
    } else {
        collected.get(3, TimeUnit.SECONDS)
    }
}.getOrNull()

private fun runCaptureText(args: List<String>): String? = runCapture(args)?.decodeToString()
