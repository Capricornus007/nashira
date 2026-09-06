package io.github.capricornus007.nashira.theme

import io.github.capricornus007.nashira.appContext

actual val dynamicColorSupported: Boolean = android.os.Build.VERSION.SDK_INT >= 31

actual val backgroundSyncSupported: Boolean = true
actual val keyboardLayoutSettingsSupported: Boolean = false

/**
 * 啟動／停止背景同步。服務類別在 androidApp 模組（shared 看不到它），
 * 所以用 Intent 的 component 名稱指定，避免把 app 模組反向依賴進 shared。
 */
actual fun applyBackgroundSync(enabled: Boolean) {
    val context = appContext ?: return
    val intent = android.content.Intent().setClassName(
        context,
        "io.github.capricornus007.nashira.android.SyncService",
    )
    runCatching {
        if (enabled) {
            context.startForegroundService(intent)
        } else {
            intent.action = "io.github.capricornus007.nashira.STOP_SYNC"
            context.startService(intent)
        }
    }
}
