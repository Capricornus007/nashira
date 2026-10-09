package io.github.capricornus007.nashira

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect
import org.jetbrains.skia.SamplingMode
import org.jetbrains.skia.Surface

actual fun decodeImageBitmap(bytes: ByteArray, maxDimension: Int): ImageBitmap? = runCatching {
    val image = Image.makeFromEncoded(bytes)
    if (maxDimension <= 0 || (image.width <= maxDimension && image.height <= maxDimension)) {
        return@runCatching image.toComposeImageBitmap()
    }
    // Skia 沒有解碼期降採樣，解完再縮一次；記憶體峰值仍在，但快取住的是小圖
    val scale = maxDimension.toFloat() / maxOf(image.width, image.height)
    val width = (image.width * scale).toInt().coerceAtLeast(1)
    val height = (image.height * scale).toInt().coerceAtLeast(1)
    val surface = Surface.makeRasterN32Premul(width, height)
    // 必須明給取樣模式：不給參數的那個多載走的是「無濾鏡＝最近鄰」，
    // 一張 2560 的長圖硬縮到 1024 會直接出現鋸齒與細節丟失（用戶 2026-09-29：
    // 「點開之後也沒多清楚」就是這一步糊掉的）。縮小用 Mitchell 是高品質的標準選擇。
    surface.canvas.drawImageRect(
        image,
        Rect.makeWH(image.width.toFloat(), image.height.toFloat()),
        Rect.makeWH(width.toFloat(), height.toFloat()),
        SamplingMode.MITCHELL,
        null,
        false,
    )
    surface.makeImageSnapshot().toComposeImageBitmap()
}.getOrNull()

/**
 * 逐格解動畫。用 `Codec` 而不是 `Image.makeFromEncoded`：後者只有第一格。
 *
 * ⚠️ 所有格都要解進**同一個 Bitmap**：GIF／WebP 的後續格常常只存差異（delta），
 * SkCodec 靠緩衝區裡上一格的內容把這一格補完整；每格新開一個 Bitmap 會解出殘缺畫面。
 */
actual fun decodeAnimatedFrames(bytes: ByteArray, maxDimension: Int): List<DecodedFrame> = runCatching {
    val codec = Codec.makeFromData(Data.makeFromBytes(bytes)) ?: return emptyList()
    try {
        val count = codec.frameCount
        if (count <= 1) return emptyList()
        val w = codec.width
        val h = codec.height
        if (w <= 0 || h <= 0) return emptyList()
        val bitmap = Bitmap()
        if (!bitmap.allocN32Pixels(w, h)) return emptyList()
        val scale = if (maxDimension <= 0) 1f else maxDimension.toFloat() / maxOf(w, h)
        val targetW = if (scale < 1f) (w * scale).toInt().coerceAtLeast(1) else w
        val targetH = if (scale < 1f) (h * scale).toInt().coerceAtLeast(1) else h
        val out = ArrayList<DecodedFrame>(count)
        for (i in 0 until count) {
            codec.readPixels(bitmap, i)
            val image = Image.makeFromBitmap(bitmap)
            val frame = if (scale < 1f) {
                val surface = Surface.makeRasterN32Premul(targetW, targetH)
                surface.canvas.drawImageRect(
                    image,
                    Rect.makeWH(w.toFloat(), h.toFloat()),
                    Rect.makeWH(targetW.toFloat(), targetH.toFloat()),
                    SamplingMode.MITCHELL,
                    null,
                    false,
                )
                surface.makeImageSnapshot().toComposeImageBitmap()
            } else {
                image.toComposeImageBitmap()
            }
            image.close()
            // 有些伺服器/橋接器把 duration 寫成 0（意思是「用預設」），0 毫秒會讓整圈
            // 在同一幀內跑完、看起來像閃爍的雜訊；下限 40ms 是 GIF 的常見底值。
            out += DecodedFrame(frame, codec.getFrameInfo(i).duration.coerceAtLeast(40))
        }
        bitmap.close()
        out
    } finally {
        codec.close()
    }
}.getOrDefault(emptyList())

/** 影片解格的播放速率：15fps 對貼紙那種幾秒的循環夠用，也把記憶體壓得住。 */
private const val VideoFrameRate = 15

/**
 * 用 ffmpeg 把影片解成一格一格。
 *
 * 為什麼不直接讀 rawvideo：那樣要先算準每格位元組數、再處理 skia 的像素格式與
 * 位元組序，錯一格就整串錯位。改成請 ffmpeg 每格吐一張 PNG、用 PNG 的結尾記號
 * `IEND`＋CRC（8 位元組）切流，再走**已經在用的** `decodeImageBitmap`，
 * 沒有新增任何自己處理像素的程式碼。
 */
actual fun decodeAnimatedVideoFrames(bytes: ByteArray, maxDimension: Int, maxFrames: Int): List<DecodedFrame> {
    if (maxFrames <= 0 || bytes.isEmpty()) return emptyList()
    val file = java.io.File.createTempFile("nashira-anim-", ".bin")
    return try {
        file.writeBytes(bytes)
        val dims = runCapture(
            listOf(
                "ffprobe", "-v", "error", "-select_streams", "v:0",
                "-show_entries", "stream=width,height", "-of", "csv=p=0:s=x",
                file.absolutePath,
            )
        )?.decodeToString()?.trim().orEmpty()
        val sides = dims.split("x").mapNotNull { it.trim().toIntOrNull() }
        if (sides.size != 2 || sides[0] <= 0 || sides[1] <= 0) return emptyList()
        val sourceW = sides[0]
        val sourceH = sides[1]
        // 長邊不超過 maxDimension（與 decodeAnimatedFrames 同一套算法）
        val scale = if (maxDimension <= 0) 1f else maxDimension.toFloat() / maxOf(sourceW, sourceH)
        val width = if (scale < 1f) (sourceW * scale).toInt().coerceAtLeast(1) else sourceW
        val height = if (scale < 1f) (sourceH * scale).toInt().coerceAtLeast(1) else sourceH
        val stream = runCapture(
            listOf(
                "ffmpeg", "-v", "error", "-i", file.absolutePath,
                "-vf", "scale=$width:$height,fps=$VideoFrameRate",
                "-frames:v", maxFrames.toString(),
                "-f", "image2pipe", "-vcodec", "png", "pipe:1",
            )
        ) ?: return emptyList()
        val durationMs = 1000 / VideoFrameRate
        splitPngDocuments(stream).mapNotNull { png -> decodeImageBitmap(png, maxDimension = 0) }
            .map { DecodedFrame(it, durationMs) }
    } catch (e: Exception) {
        emptyList()
    } finally {
        file.delete()
    }
}

/** 跑一條命令、把 stdout 全收下來；非零出口或開不起來都回 null。 */
private fun runCapture(command: List<String>): ByteArray? = runCatching {
    val process = ProcessBuilder(command).redirectErrorStream(false).withoutLauncherEnv().start()
    // 一定要先把 stdout 讀空再 waitFor：管線緩衝區年滿時寫端會堵死，
    // 那時 waitFor 永遠等不到（這個坑在 VideoFrame.desktop.kt 的註解裡記過）。
    val out = process.inputStream.use { it.readBytes() }
    process.waitFor()
    if (process.exitValue() == 0) out else null
}.getOrNull()

/** 串流的 PNG 是一張接一張，用每一張結尾的 IEND＋CRC 八位元組切開。 */
private fun splitPngDocuments(stream: ByteArray): List<ByteArray> {
    val trailer = byteArrayOf(0x49, 0x45, 0x4e, 0x44, 0xae.toByte(), 0x42, 0x60, 0x82.toByte())
    val docs = ArrayList<ByteArray>()
    var start = 0
    var i = 0
    while (i + trailer.size <= stream.size) {
        var matches = true
        for (k in trailer.indices) {
            if (stream[i + k] != trailer[k]) {
                matches = false
                break
            }
        }
        if (matches) {
            val end = i + trailer.size
            if (end - start > trailer.size) docs += stream.copyOfRange(start, end)
            start = end
            i = end
        } else {
            i++
        }
    }
    return docs
}
