package io.github.capricornus007.nashira

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
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
    surface.canvas.drawImageRect(image, org.jetbrains.skia.Rect.makeWH(width.toFloat(), height.toFloat()))
    surface.makeImageSnapshot().toComposeImageBitmap()
}.getOrNull()
