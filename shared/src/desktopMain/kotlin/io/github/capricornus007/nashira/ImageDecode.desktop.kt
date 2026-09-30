package io.github.capricornus007.nashira

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
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
