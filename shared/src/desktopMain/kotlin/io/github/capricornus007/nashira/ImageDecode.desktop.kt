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
