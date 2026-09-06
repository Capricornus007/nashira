package io.github.capricornus007.nashira

import de.connect2x.trixnity.client.MatrixClient
import de.connect2x.trixnity.client.notification
import de.connect2x.trixnity.core.model.UserId
import de.connect2x.trixnity.core.model.events.ClientEvent
import de.connect2x.trixnity.core.model.push.PushAction
import io.github.capricornus007.nashira.i18n.AppLanguage
import io.github.capricornus007.nashira.i18n.stringsFor
import io.github.capricornus007.nashira.matrix.MessageBody
import io.github.capricornus007.nashira.matrix.RoomRepository
import io.github.capricornus007.nashira.matrix.messageBodyOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest

/** 平台通知能力接口；Android 在 MainActivity 注入實現，桌面用 notify-send。 */
interface NotificationPlatform {
    /** 應用是否在前台：前台時不發系統通知（使用者正在看 app）。 */
    val foreground: StateFlow<Boolean>

    fun ensureChannels() = Unit

    fun show(roomKey: String, title: String, body: String, mentionCount: Int) = Unit
}

/** 平台通知門面。 */
object AppNotifications {
    var platform: NotificationPlatform = object : NotificationPlatform {
        override val foreground = MutableStateFlow(true) // 未注入平台時視為前台：一律不發
    }

    fun ensureChannels() = platform.ensureChannels()

    fun show(roomKey: String, title: String, body: String, mentionCount: Int) =
        platform.show(roomKey, title, body, mentionCount)

    val foreground: StateFlow<Boolean> get() = platform.foreground
}

/**
 * 系統通知的觸發端：直接收 Trixnity 的通知流。
 *
 * **必須走 `client.notification.getNotifications()`，不能自己看未讀旗標。**
 * 靜音是伺服器端的推播規則（`m.push_rules` 裡該房間的 rule，Element 按下靜音就寫在那），
 * 所有客戶端共用同一份；`isUnread` 只回答「有沒有未讀」，完全不看規則，於是靜音房
 * 照樣會被通知（使用者實測：Element 已靜音的 Gentoo-zh 與 #archlinux-cn-appearance
 * 仍被 Nashira 通知）。`getNotifications` 已經把事件逐一過過推播規則，
 * 靜音房、只提及模式、關鍵字規則都自然生效，也不必自己維護「上次通知到哪」。
 *
 * **不是 composable**：以前掛在 Compose 樹上，App 一離開前台、UI 不再重組就停了，
 * 而通知恰恰只在背景才需要發。現在跟著 MatrixSession 的生命週期跑。
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal suspend fun watchNotifications(client: MatrixClient, myUserId: UserId) {
    val rooms = RoomRepository(client)
    // 前台完全不訂閱：getNotifications 會把 sync 回來的每一則事件都解密並過推播規則，
    // 啟動時幾十個房間一起來，暫時分配會衝到數百 MB（實測 Java heap 峰值 512MB）。
    // 前台本來就不發通知，所以直接不收；退到背景才開始收。
    AppNotifications.foreground
        .flatMapLatest { inForeground ->
            if (inForeground) emptyFlow() else client.notification.getNotifications()
        }
        .collect { notification ->
            // 規則說要通知才通知（Trixnity 只發需要通知的事件，這裡是保險）
            if (notification.actions.none { it is PushAction.Notify }) return@collect
            val event = notification.event as? ClientEvent.RoomEvent.MessageEvent<*> ?: return@collect
            if (event.sender == myUserId) return@collect
            val strings = stringsFor(persistedLanguage())
            val body = event.content.messageBodyOrNull() ?: return@collect
            val text = when (body) {
                is MessageBody.Text -> body.text
                is MessageBody.Image -> if (body.isSticker) strings.notifSticker else strings.notifImage
                is MessageBody.Attachment -> body.name
                MessageBody.Undecryptable -> strings.notifUndecryptable
            }
            val roomId = event.roomId
            // 單一房間查詢就好：每來一則通知都展開整份摘要流會炸記憶體
            val roomName = rooms.roomName(roomId) ?: roomId.full
            val senderName = rooms.memberName(roomId, event.sender)
                ?: event.sender.full.removePrefix("@").substringBefore(':')
            val count = client.notification.getCount(roomId).firstOrNull() ?: 0
            AppNotifications.show(
                roomKey = roomId.full,
                title = "$roomName · $senderName",
                body = text,
                mentionCount = count,
            )
    }
}

private fun persistedLanguage(): AppLanguage {
    val stored = runCatching { SettingsStorage().load() }.getOrNull()?.get("language")
    return AppLanguage.entries.firstOrNull { it.name == stored } ?: AppLanguage.ZH_TW
}
