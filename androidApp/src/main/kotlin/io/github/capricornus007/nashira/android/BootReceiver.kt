package io.github.capricornus007.nashira.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.capricornus007.nashira.SettingsStorage
import io.github.capricornus007.nashira.appContext
import io.github.capricornus007.nashira.matrix.TokenStorage

/**
 * 開機／更新後把背景同步接回來。沒有這個，每次重開機都要先手動開一次 app 才會有通知。
 *
 * BOOT_COMPLETED 是背景啟動前台服務的豁免情境之一，所以這裡可以直接 startForegroundService。
 * 只有磁碟上真的有登入憑證、且使用者沒關掉背景同步時才啟動。
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action != Intent.ACTION_BOOT_COMPLETED && action != Intent.ACTION_MY_PACKAGE_REPLACED) return
        appContext = context.applicationContext
        TokenStorage.context = context.applicationContext
        if (TokenStorage().load() == null) return
        val enabled = runCatching { SettingsStorage().load()["backgroundSync"]?.toBooleanStrictOrNull() }
            .getOrNull() ?: true
        if (!enabled) return
        SyncService.start(context)
    }
}
