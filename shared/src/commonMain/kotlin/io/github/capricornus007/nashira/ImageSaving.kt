package io.github.capricornus007.nashira

import androidx.compose.runtime.Composable

/**
 * 把媒體位元組存到使用者可見的位置（下載）。
 *
 * Android 寫進 MediaStore（相簿）；桌面彈 JFileChooser 選路徑。
 * 回傳給使用者看的說明（例如存到哪裡），失敗時 Result.failure。
 */
@Composable
expect fun rememberImageSaver(): suspend (bytes: ByteArray, fileName: String, mimeType: String) -> Result<String>

/**
 * 「下載」——不問路徑，直接進系統下載位置，並回傳一句「存到哪了」給使用者看。
 *
 * 與 [rememberImageSaver] 的分工照 Element 的檢視器：右下那顆下載箭頭是這個（靜默），
 * 三點選單裡的「另存為…」才是彈對話框選位置的那個。兩家客戶端都是兩個動作，
 * 我們如果只做一顆，就等於在「問」跟「不問」之間替他選一個。
 *
 * Android 沒有「靜默寫進系統下載夾」這條路（分區儲存下要走 MediaStore，
 * 而 MediaStore 的 Pictures/Nashira 正是 [rememberImageSaver] 做的事），
 * 所以手機端兩者同一實作——這不是我偷懶，是平台只給一條路。
 */
@Composable
expect fun rememberMediaDownloader(): suspend (bytes: ByteArray, fileName: String, mimeType: String) -> Result<String>
