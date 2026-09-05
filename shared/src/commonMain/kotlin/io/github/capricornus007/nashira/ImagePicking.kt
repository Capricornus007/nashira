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

/**
 * 圖片選擇器：回傳「啟動選擇」的回呼。
 * Android 用系統相簿／檔案選擇；桌面端目前 no-op（回 null 直接不出現入口）。
 */
@Composable
expect fun rememberImagePickerLauncher(
    onPicked: (PickedImage) -> Unit,
): (() -> Unit)?
