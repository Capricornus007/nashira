package io.github.capricornus007.nashira.matrix

import android.annotation.SuppressLint
import android.content.Context

actual class TokenStorage actual constructor() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
    }

    private val prefs by lazy {
        val ctx = context ?: throw IllegalStateException("TokenStorage.context 未注入（MainActivity 應先設置）")
        ctx.getSharedPreferences("nashira_session", Context.MODE_PRIVATE)
    }

    actual fun save(baseUrl: String, userId: String, deviceId: String, accessToken: String) {
        prefs.edit()
            .putString("baseUrl", baseUrl)
            .putString("userId", userId)
            .putString("deviceId", deviceId)
            .putString("accessToken", accessToken)
            .apply()
    }

    actual fun load(): StoredToken? {
        val token = prefs.getString("accessToken", null) ?: return null
        return StoredToken(
            baseUrl = prefs.getString("baseUrl", "") ?: "",
            userId = prefs.getString("userId", "") ?: "",
            deviceId = prefs.getString("deviceId", "") ?: "",
            accessToken = token,
        )
    }

    actual fun clear() {
        prefs.edit().clear().apply()
    }
}
