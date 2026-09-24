package io.github.capricornus007.nashira

/**
 * Android：音訊路由交給系統（設定裡的音訊區塊也不顯示，底欄那三顆鈕同樣由
 * audioDeviceSettingsSupported 擋掉），枚舉回空。
 * 回空只是雙保險，真正的「不顯示」在 UI 的旗標判斷。
 */
actual object AudioDevices {
    actual fun inputs(): List<AudioDevice> = emptyList()
    actual fun outputs(): List<AudioDevice> = emptyList()
    actual fun defaultInputLabel(): String? = null
    actual fun defaultOutputLabel(): String? = null
}
