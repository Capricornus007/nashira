package io.github.capricornus007.nashira

import android.annotation.SuppressLint
import android.content.Context
import io.github.capricornus007.nashira.matrix.TokenStorage

actual class SettingsStorage actual constructor() {
    private val prefs by lazy {
        val ctx: Context = TokenStorage.context ?: appContext
            ?: error("SettingsStorage 需要 application context（MainActivity 應先注入）")
        ctx.getSharedPreferences("nashira_settings", Context.MODE_PRIVATE)
    }

    @Suppress("UNCHECKED_CAST")
    actual fun load(): Map<String, String> =
        prefs.all.mapNotNull { (key, value) -> (value as? String)?.let { key to it } }.toMap()

    actual fun save(values: Map<String, String>) {
        prefs.edit().apply {
            clear()
            values.forEach { (key, value) -> putString(key, value) }
        }.apply()
    }
}
