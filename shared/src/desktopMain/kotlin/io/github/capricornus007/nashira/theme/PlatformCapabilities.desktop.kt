package io.github.capricornus007.nashira.theme

actual val dynamicColorSupported: Boolean = false

actual val backgroundSyncSupported: Boolean = false
actual val keyboardLayoutSettingsSupported: Boolean = true

actual fun applyBackgroundSync(enabled: Boolean) = Unit

actual val platformDeviceDisplayName: String = "Nashira on Linux"

actual val audioDeviceSettingsSupported: Boolean = true

// java.awt.Cursor 的左右雙箭頭：Compose 的 PointerIcon 有直接吃 AWT Cursor 的建構子
actual val horizontalResizeIcon: androidx.compose.ui.input.pointer.PointerIcon
    get() = androidx.compose.ui.input.pointer.PointerIcon(
        java.awt.Cursor(java.awt.Cursor.E_RESIZE_CURSOR)
    )

actual val verticalResizeIcon: androidx.compose.ui.input.pointer.PointerIcon
    get() = androidx.compose.ui.input.pointer.PointerIcon(
        java.awt.Cursor(java.awt.Cursor.S_RESIZE_CURSOR)
    )
