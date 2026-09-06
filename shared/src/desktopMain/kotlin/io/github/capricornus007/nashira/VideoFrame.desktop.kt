package io.github.capricornus007.nashira

import androidx.compose.ui.graphics.ImageBitmap

/**
 * 桌面沒有內建影片解碼器；系統有 ffmpeg 就借它抽一格，沒有就回 null
 * （UI 退回帶標籤的佔位，而不是假裝載入中）。
 */
actual fun decodeVideoFrame(bytes: ByteArray, maxDimension: Int): ImageBitmap? = runCatching {
    if (!ffmpegAvailable) return@runCatching null
    val process = ProcessBuilder(
        "ffmpeg", "-v", "error", "-i", "pipe:0",
        "-frames:v", "1",
        "-vf", "scale='min($maxDimension,iw)':-1",
        "-f", "image2pipe", "-vcodec", "png", "pipe:1",
    ).redirectErrorStream(false).start()
    process.outputStream.use { it.write(bytes) }
    val png = process.inputStream.use { it.readBytes() }
    process.waitFor()
    if (png.isEmpty()) null else decodeImageBitmap(png, maxDimension)
}.getOrNull()

private val ffmpegAvailable: Boolean by lazy {
    runCatching {
        ProcessBuilder("ffmpeg", "-version").redirectErrorStream(true).start().waitFor() == 0
    }.getOrDefault(false)
}
