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

/** 動畫的其中一格：位圖 + 該格要停留多久（毫秒）。 */
class DecodedFrame(val bitmap: ImageBitmap, val durationMs: Int)

/**
 * 解 GIF／動態 WebP／動態 PNG 的**全部**格；不是動畫（或平台沒有多格解碼器）就回空集合，
 * 呼叫端退回單格靜態顯示。
 *
 * 為什麼必須做：Telegram 的動態貼圖經橋接進來是 `image/gif`，
 * `Image.makeFromEncoded` 只給第一格——用戶 2026-10-07 拿 Telegram 手機版對照點名
 * 「為什麼貼紙是靜態的」「tg 這裡實際貼紙也依舊是動態的」。
 */
expect fun decodeAnimatedFrames(bytes: ByteArray, maxDimension: Int = DefaultMaxImageDimension): List<DecodedFrame>

/**
 * 把**影片**解成一格一格（webm／mp4 的動態貼紙用）。
 *
 * 為什麼要另外一個入口：`decodeAnimatedFrames` 走的是 Skia 的 `Codec`，
 * 它只認 GIF／動態 WebP／動態 PNG 這類**圖片**容器，影片檔一律回「0 格」。
 * 用戶 2026-10-08「動態貼紙依舊沒實現」查出來就是這個：Telegram 橋把 TGS 貼紙
 * 轉成 webm/mp4 傳進 Matrix，Skia 那條解不出任何一格（日誌只有「動畫 0 格」）。
 *
 * 桌面用 ffmpeg 解（本專案已經依賴它抽影片封面）；其他平台回空清單＝保持靜態。
 */
expect fun decodeAnimatedVideoFrames(
    bytes: ByteArray,
    maxDimension: Int,
    maxFrames: Int,
): List<DecodedFrame>
