package io.github.capricornus007.nashira

/**
 * 圖片檢視器的平台動作。回 `false` 代表「這個平台做不到」，
 * 呼叫端據此**不把按鈕畫出來**——擺一顆按了沒反應的按鈕比沒有按鈕更糟
 *（用戶 2026-09-29 反復點名「不要發明別家沒有／自己做不到的东西」）。
 *
 * 「用系統播放器開啟影片」那兩條（`openMediaExternally`／`openMediaUrlExternally`）
 * 已經删掉：影片改成在檢視器裡內嵌播放（待辦 #95），外部視窗會讓人「點好幾次延遲好幾秒
 * 還開好幾個窗口」（用戶 2026-10-08 原話）。
 */

/** 把圖片放進系統剪貼簿（Telegram 桌面版的「複製圖片」）。 */
expect fun copyImageToClipboard(bytes: ByteArray, mimeType: String): Boolean
