package io.github.capricornus007.nashira.android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import io.github.capricornus007.nashira.App
import io.github.capricornus007.nashira.AndroidNotifications
import io.github.capricornus007.nashira.AppNotifications
import io.github.capricornus007.nashira.NashiraUri
import io.github.capricornus007.nashira.SettingsStorage
import io.github.capricornus007.nashira.appContext
import io.github.capricornus007.nashira.matrix.MatrixEngine
import io.github.capricornus007.nashira.matrix.TokenStorage
import io.github.capricornus007.nashira.setAppInForeground
import kotlinx.coroutines.launch
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
        val backgroundSync = runCatching {
            SettingsStorage().load()["backgroundSync"]?.toBooleanStrictOrNull()
        }.getOrNull() ?: true
        if (backgroundSync && TokenStorage().load() != null) SyncService.start(this)
        handleSsoIntent(intent)
        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleSsoIntent(intent)
    }

    private fun handleSsoIntent(intent: Intent) {
        val callback = intent.data?.toString()?.let(NashiraUri::parseSsoCallback) ?: return
        intent.data = null
        lifecycleScope.launch {
            MatrixEngine.loginWithToken(baseUrl = callback.second, loginToken = callback.first)
                .onFailure { error -> android.util.Log.e("NashiraSSO", "SSO token exchange failed", error) }
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
