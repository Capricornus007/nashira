package io.github.capricornus007.nashira

/**
 * 手機：複製到剪貼簿需要 ContentProvider 與 Intent 權限，還沒做。
 * 回 false 讓檢視器**不把按鈕畫出來**，而不是擺一顆按了沒反應的。
 */
actual fun copyImageToClipboard(bytes: ByteArray, mimeType: String): Boolean = false
