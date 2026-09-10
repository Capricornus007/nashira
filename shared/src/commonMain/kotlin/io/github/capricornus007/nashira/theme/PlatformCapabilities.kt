package io.github.capricornus007.nashira.theme

/** 平台能力：Android 支援動態取色；桌面（Linux）不支援（也無需） */
expect val dynamicColorSupported: Boolean

/**
 * 是否有「背景同步」這個概念。Android 需要前台服務才能在離開 app 後維持 /sync；
 * 桌面程式只要視窗還開著就一直在同步，沒有這個開關。
 */
expect val backgroundSyncSupported: Boolean

/**
 * 「送出鍵」「貼圖面板位置」這類實體鍵盤／桌面視窗慣例的設定是否顯示。
 * 手機上 Enter 行為由輸入法自己決定，面板位置也固定，顯示這兩項只會誤導。
 */
expect val keyboardLayoutSettingsSupported: Boolean

/** 套用背景同步設定（Android 啟動／停止前台服務；桌面 no-op）。 */
expect fun applyBackgroundSync(enabled: Boolean)

/**
 * 裝置在伺服器上的顯示名（工作階段清單裡別人看到的名字）。
 * 區分平台：Element 顯示 "Element Nightly on Nightly for Linux" 這種格式，
 * Nashira 對應 "Nashira on Android" / "Nashira on Linux"——幾台裝置同時登入時
 * 才分得出誰是誰（用戶工作階段清單真機實測需求）。
 */
expect val platformDeviceDisplayName: String
