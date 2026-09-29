package io.github.capricornus007.nashira

/**
 * 圖片檢視器的兩個平台動作。回 `false` 代表「這個平台做不到」，
 * 呼叫端據此**不把按鈕畫出來**——擺一顆按了沒反應的按鈕比沒有按鈕更糟
 *（用戶 2026-09-29 反復點名「不要發明別家沒有／自己做不到的东西」）。
 */

/** 把圖片放進系統剪貼簿（Telegram 桌面版的「複製圖片」）。 */
expect fun copyImageToClipboard(bytes: ByteArray, mimeType: String): Boolean

/** 用系統預設程式開啟（先落地成暫存檔）。 */
expect fun openMediaExternally(bytes: ByteArray, fileName: String, mimeType: String): Boolean
