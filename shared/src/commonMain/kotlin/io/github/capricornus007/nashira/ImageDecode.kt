package io.github.capricornus007.nashira

import androidx.compose.ui.graphics.ImageBitmap

/** 把已編碼的圖片位元組（PNG/JPEG/WebP）解成 Compose 位圖；失敗回 null。 */
expect fun decodeImageBitmap(bytes: ByteArray): ImageBitmap?
