package io.github.capricornus007.nashira

/** Android：音訊路由交給系統（設定裡的音訊區塊也不顯示），枚舉回空。 */
actual object AudioDevices {
    actual fun inputs(): List<String> = emptyList()
    actual fun outputs(): List<String> = emptyList()
}
