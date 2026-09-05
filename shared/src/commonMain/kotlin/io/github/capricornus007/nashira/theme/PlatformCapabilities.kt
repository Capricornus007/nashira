package io.github.capricornus007.nashira.theme

/** 平台能力：Android 支援動態取色；桌面（Linux）不支援（也無需） */
expect val dynamicColorSupported: Boolean

/**
 * 是否有「背景同步」這個概念。Android 需要前台服務才能在離開 app 後維持 /sync；
 * 桌面程式只要視窗還開著就一直在同步，沒有這個開關。
 */
expect val backgroundSyncSupported: Boolean

/** 套用背景同步設定（Android 啟動／停止前台服務；桌面 no-op）。 */
expect fun applyBackgroundSync(enabled: Boolean)
