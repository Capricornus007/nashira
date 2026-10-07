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

/**
 * 把一個**可直接抓的網址**交給系統預設程式（影片就交給 mpv／ celluloid 之类，串流播放）。
 *
 * 為什麼要有這條、而不是「先下載再開」：用戶 2026-10-08 一語點破
 *「圖片都是立刻點開的憑什麼視頻非得那麼久」。先下載＝點下去要等整檔跑完
 *（那條 9.69MiB 的影片在他那條 ~100KB/s 的鏈路上是幾十秒），
 * 畫面上看起來就是「點了沒反應」。給網址則是由播放器自己邊抓邊播，秒開。
 * 回 `false` 代表這臺做不到，呼叫端退回「下載→暫存檔→開」。
 */
expect fun openMediaUrlExternally(url: String, mimeType: String): Boolean
