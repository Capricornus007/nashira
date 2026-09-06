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

/**
 * 從影片位元組取第一格畫面。
 *
 * 為什麼需要：Telegram 的動態貼圖經橋接後是 `video/webm`（實測 Tairitsu 包 36 張
 * 全部是 video/webm），伺服器的縮圖端點對影片直接回 400，圖片解碼器也吃不下，
 * 於是整包只能顯示破圖。取一格當靜態預覽是所有客戶端的通用做法。
 *
 * 平台沒有可用的解碼器時回 null，UI 再退回帶標籤的佔位。
 */
expect fun decodeVideoFrame(bytes: ByteArray, maxDimension: Int = 256): ImageBitmap?
