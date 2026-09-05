package io.github.capricornus007.nashira.android

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import io.github.capricornus007.nashira.App
import io.github.capricornus007.nashira.AndroidNotifications
import io.github.capricornus007.nashira.AppNotifications
import io.github.capricornus007.nashira.SettingsStorage
import io.github.capricornus007.nashira.appContext
import io.github.capricornus007.nashira.matrix.TokenStorage
import io.github.capricornus007.nashira.setAppInForeground

class MainActivity : ComponentActivity() {

    private val requestNotifications =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContext = applicationContext
        TokenStorage.context = applicationContext
        if (AppNotifications.platform !is AndroidNotifications) {
            AppNotifications.platform = AndroidNotifications
        }
        AppNotifications.ensureChannels()
        askForNotificationPermissionIfNeeded()
        // 背景同步：使用者沒關掉、且磁碟上有憑證時才起服務（沒登入前起了只是白佔通知列）
        val backgroundSync = runCatching {
            SettingsStorage().load()["backgroundSync"]?.toBooleanStrictOrNull()
        }.getOrNull() ?: true
        if (backgroundSync && TokenStorage().load() != null) SyncService.start(this)
        setContent {
            App() // 主題模式改由 App 內部管理（追隨系統/深/淺），不傳 defaultDark
        }
    }

    override fun onStart() {
        super.onStart()
        setAppInForeground(true)
    }

    override fun onStop() {
        setAppInForeground(false)
        super.onStop()
    }

    private fun askForNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        ) return
        requestNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
