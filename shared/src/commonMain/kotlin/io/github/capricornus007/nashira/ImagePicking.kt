package io.github.capricornus007.nashira

import androidx.compose.runtime.Composable

/** 從相簿／檔案系統選到的圖片。 */
data class PickedImage(
    val bytes: ByteArray,
    val mimeType: String,
    val fileName: String,
    val width: Int?,
    val height: Int?,
)

/** 任意檔案（m.file）。 */
data class PickedFile(
    val bytes: ByteArray,
    val mimeType: String,
    val fileName: String,
)

/**
 * 圖片選擇器：回傳「啟動選擇」的回呼。
 * Android 用系統相簿／檔案選擇；桌面用 Swing 的 JFileChooser。
 */
@Composable
expect fun rememberImagePickerLauncher(
    onPicked: (PickedImage) -> Unit,
): (() -> Unit)?

/** 任意檔案選擇器；平台不支援時回 null，UI 就不出現該入口。 */
@Composable
expect fun rememberFilePickerLauncher(
    onPicked: (PickedFile) -> Unit,
): (() -> Unit)?

/** 只有桌面把「剪貼簿裡有圖」當成輸入框的貼上來源（手機走系統選擇器／分享）。 */
expect val clipboardImagePasteSupported: Boolean

/**
 * 讀剪貼簿裡的圖片，回可直接暫存的項目；沒有圖片就回空清單。
 *
 * 兩種來源都要理會，而且**優先取檔案**：截圖工具放的是「影像本身」，檔案管理員複製的
 * 是「圖片檔」。同一份內容以檔案形式存在時，拿檔案才留得下**原編碼與檔名**——
 * 把 PNG 重新編成 JPEG 會無損變有損，也會丟掉 EXIF 與尺寸以外的所有資訊。
 */
expect suspend fun readClipboardImages(): List<PickedImage>

/**
 * 「剪貼簿現在有沒有圖」的廉價檢查。
 *
 * 之所以要單獨一個：按鍵處理必須**同步**決定要不要吃掉 Ctrl+V。若先吃掉再非同步讀，
 * 使用者貼純文字會被我們靜默弄壞（按了沒反應，也找不到原因）。這裡只問「有沒有那種格式」，
 * 真正把資料搬過來留給 [readClipboardImages]。
 */
expect fun clipboardHasImages(): Boolean
