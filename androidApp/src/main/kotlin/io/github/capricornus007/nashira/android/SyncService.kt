package io.github.capricornus007.nashira.android

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import io.github.capricornus007.nashira.R
import io.github.capricornus007.nashira.appContext
import io.github.capricornus007.nashira.matrix.MatrixEngine
import io.github.capricornus007.nashira.matrix.TokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * 背景同步的前台服務。
 *
 * 為什麼一定要它：Matrix 的 `/sync` 是長輪詢，只要行程被回收就沒有新訊息，也就不會有通知。
 * Android 沒有「訊息類」的前台服務型別，`dataSync` 在 Android 15 起被限制每天 6 小時，
 * 所以用 `specialUse`（不設時限，需在 manifest 宣告 subtype）。
 *
 * 服務不自己持有 client：session 是 [MatrixEngine] 的行程級單例，服務要做的只有
 * 「把行程留著」＋「沒有 session 時從磁碟恢復一個」。通知觀察器綁在 session 上
 * （見 MatrixSession.start），所以這裡不重複那段邏輯。
 *
 * `android:stopWithTask` 預設 false，因此從最近工作清單滑掉 app 不會停掉這個服務
 * （AOSP 行為；crDroid 這類原生系 ROM 沒有 OEM 殺後台）。
 */
class SyncService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        TokenStorage.context = applicationContext
        ensureChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
        scope.launch { MatrixEngine.restoreFromDisk() }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }
        // 行程被系統回收後自動重啟，重啟時再從磁碟恢復 session
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // IMPORTANCE_MIN：常駐通知不該有聲音也不該擠在最上面
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_SYNC,
                getString(R.string.notif_channel_sync),
                NotificationManager.IMPORTANCE_MIN,
            ).apply {
                description = getString(R.string.notif_channel_sync_desc)
                setShowBadge(false)
            },
        )
    }

    private fun buildNotification(): Notification {
        val open = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Builder(this, CHANNEL_SYNC)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.notif_sync_title))
            .setContentText(getString(R.string.notif_sync_text))
            .setContentIntent(open)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    companion object {
        private const val CHANNEL_SYNC = "sync"
        private const val NOTIFICATION_ID = 0x4E5359 // 'NSY'
        const val ACTION_STOP = "io.github.capricornus007.nashira.STOP_SYNC"

        fun start(context: Context) {
            val intent = Intent(context, SyncService::class.java)
            runCatching { context.startForegroundService(intent) }
        }

        fun stop(context: Context) {
            val intent = Intent(context, SyncService::class.java).apply { action = ACTION_STOP }
            runCatching { context.startService(intent) }
        }
    }
}
