package io.github.capricornus007.nashira.theme

actual val dynamicColorSupported: Boolean = false

actual val backgroundSyncSupported: Boolean = false
actual val keyboardLayoutSettingsSupported: Boolean = true

actual fun applyBackgroundSync(enabled: Boolean) = Unit
