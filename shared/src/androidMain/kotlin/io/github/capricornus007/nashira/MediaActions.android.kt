package io.github.capricornus007.nashira

/**
 * 手機：這兩個動作需要 ContentProvider 暫存檔與 Intent 權限，還沒做。
 * 回 false 讓檢視器**不把按鈕畫出來**，而不是擺一顆按了沒反應的。
 */
actual fun copyImageToClipboard(bytes: ByteArray, mimeType: String): Boolean = false

actual fun openMediaExternally(bytes: ByteArray, fileName: String, mimeType: String): Boolean = false
