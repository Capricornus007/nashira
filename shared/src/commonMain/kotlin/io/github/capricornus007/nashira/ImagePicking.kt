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
