package io.github.capricornus007.nashira

import androidx.compose.runtime.Composable

/** 桌面端選圖尚未實作（回 null：UI 不出現入口）。 */
@Composable
actual fun rememberImagePickerLauncher(
    onPicked: (PickedImage) -> Unit,
): (() -> Unit)? = null
