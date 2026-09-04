package io.github.capricornus007.nashira

/**
 * UI 設定的持久化。沿用 TokenStorage 的 expect/actual 模式
 * （Android SharedPreferences / 桌面 properties 檔），不另外引入 DataStore：
 * 這裡存的是十來個純量開關，同步讀寫已足夠，且與既有存儲寫法一致。
 */
expect class SettingsStorage() {
    fun load(): Map<String, String>
    fun save(values: Map<String, String>)
}
