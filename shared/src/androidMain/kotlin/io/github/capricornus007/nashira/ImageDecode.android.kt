package io.github.capricornus007.nashira

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun decodeImageBitmap(bytes: ByteArray, maxDimension: Int): ImageBitmap? = runCatching {
    if (maxDimension <= 0) {
        return@runCatching BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
    }
    // 先只讀尺寸，算出 2 的次方降採樣倍率，再真正解碼——BitmapFactory 只吃 2 的次方
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
    var sample = 1
    while (
        bounds.outWidth / sample > maxDimension || bounds.outHeight / sample > maxDimension
    ) {
        sample *= 2
    }
    val options = BitmapFactory.Options().apply { inSampleSize = sample }
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)?.asImageBitmap()
}.getOrNull()

/**
 * 手機端的多格解碼還沒做（要換 `ImageDecoder`/`AnimatedImageDrawable`，
 * 那是另一套繪製管線，Compose 的 `Image` 吃不進 drawable）。
 * 回空集合＝退回單格靜態顯示，不影響其他功能。
 */
actual fun decodeAnimatedFrames(bytes: ByteArray, maxDimension: Int): List<DecodedFrame> = emptyList()
