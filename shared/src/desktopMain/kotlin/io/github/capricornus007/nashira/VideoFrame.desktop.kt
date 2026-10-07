package io.github.capricornus007.nashira

import androidx.compose.ui.graphics.ImageBitmap

/**
 * 桌面沒有內建影片解碼器；系統有 ffmpeg 就借它抽一格，沒有就回 null
 * （UI 退回帶標籤的佔位，而不是假裝載入中）。
 */
actual fun decodeVideoFrame(bytes: ByteArray, maxDimension: Int): ImageBitmap? = runCatching {
    if (!ffmpegAvailable) return@runCatching null
    // ⚠️ 一定要落地成**檔案**再交給 ffmpeg，不能用 pipe:0：mp4 的索引（moov）常常放在檔尾，
    // 不可 seek 的管線讀到一半就報「moov atom not found」。
    // 實測那條 9.69MiB 的 t2bot.io 影片走管線永遠解不出封面（用戶 2026-10-07 #129
    //「十七秒的視頻電報秒开 nashira 就不行」），而同一個檔用 curl 抓下來直接播得動。
    val file = java.io.File.createTempFile("nashira-frame-", ".bin")
    val png = try {
        file.writeBytes(bytes)
        val process = ProcessBuilder(
            "ffmpeg", "-v", "error", "-i", file.absolutePath,
            "-frames:v", "1",
            "-vf", "scale='min($maxDimension,iw)':-1",
            "-f", "image2pipe", "-vcodec", "png", "pipe:1",
        ).redirectErrorStream(false).start()
        process.outputStream.use { it.flush() }
        process.inputStream.use { it.readBytes() }.also { process.waitFor() }
    } finally {
        file.delete()
    }
    if (png.isEmpty()) null else decodeImageBitmap(png, maxDimension)
}.getOrNull()

private val ffmpegAvailable: Boolean by lazy {
    runCatching {
        ProcessBuilder("ffmpeg", "-version").redirectErrorStream(true).start().waitFor() == 0
    }.getOrDefault(false)
}
