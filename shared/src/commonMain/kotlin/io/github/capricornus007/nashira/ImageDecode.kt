package io.github.capricornus007.nashira

import androidx.compose.ui.graphics.ImageBitmap

/**
 * 把已編碼的圖片位元組（PNG/JPEG/WebP）解成 Compose 位圖；失敗回 null。
 *
 * [maxDimension] 是長邊上限：**必須降採樣**。原圖直接解會把整張塞進堆積
 * （一張 2000×2000 就是 16MB），貼圖面板一次載入幾十張就把 256MB 上限撐爆
 * （實測 release 版 OutOfMemoryError）。傳 0 表示不限制。
 */
expect fun decodeImageBitmap(bytes: ByteArray, maxDimension: Int = DefaultMaxImageDimension): ImageBitmap?

/** 聊天用途的長邊上限：手機與桌面顯示都遠小於這個值。 */
const val DefaultMaxImageDimension: Int = 1024
