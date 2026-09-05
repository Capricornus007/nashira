package io.github.capricornus007.nashira

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import io.github.capricornus007.nashira.android.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val CHANNEL_MESSAGES = "messages"
private const val NOTIFICATION_ID_BASE = 0x4E5348 // 'NSH'：跟房間 hash 疊出穩定 id

/** MainActivity onStart/onStop 維護：前台期間到達的訊息不發通知。 */
internal val foregroundFlow = MutableStateFlow(true)

internal fun setAppInForeground(inForeground: Boolean) {
    foregroundFlow.value = inForeground
}

/** Android 通知實現；在 MainActivity.onCreate 注入 AppNotifications。 */
internal object AndroidNotifications : NotificationPlatform {
    override val foreground: StateFlow<Boolean> get() = foregroundFlow

    override fun ensureChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val context = appContext ?: return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_MESSAGES,
            context.getString(R.string.notif_channel_messages),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.notif_channel_messages_desc)
        }
        manager.createNotificationChannel(channel)
    }

    override fun show(roomKey: String, title: String, body: String, mentionCount: Int) {
        val context = appContext ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) return

        val openApp = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("roomId", roomKey)
        }
        val pending = PendingIntent.getActivity(
            context,
            roomKey.hashCode(),
            openApp,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_MESSAGES)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(pending)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setNumber(mentionCount.takeIf { it > 0 } ?: 0)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_BASE + roomKey.hashCode(), notification)
        } catch (_: SecurityException) {
            // 權限在通知發出前被使用者撤掉：靜默丟棄
        }
    }
}
