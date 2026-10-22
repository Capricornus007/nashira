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
interface NotificationPlatform {    /** 應用是否在前台：前台時不發系統通知（使用者正在看 app）。 */
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
 * 點系統通知帶進來的房間 id：`MainActivity` 寫、`ChatScreen` 消費。
 *
 * 為什麼不直接把 intent 塞進 UI：Compose 那側拿不到 Activity 的 intent；而且冷啟動時
 * 通知比房間摘要流先到位，得等 `summaries` 裡真的出現那一間才跳得過去——所以留成
 * 可觀察的全域值，讓消費端「等到了才清」，而不是讀一次沒有就丟掉。
 * （舊行為：`AndroidNotifications.show()` 一直有 `putExtra("roomId", …)`，但
 *  `MainActivity` 只處理 SSO 回調、全倉無人讀這條 extra ⇒ 點通知只會開 app、不進房間。）
 */
object PendingRoomOpen {
    // 不用 `by mutableStateOf(...)`：那要 `getValue`／`setValue` 兩個委派運算子的 import，
    // 少一個就是「編譯過但觀察不到」的那種坑（本倉 i18n 那邊踩過）。
    private val state = androidx.compose.runtime.mutableStateOf<String?>(null)
    var roomId: String?
        get() = state.value
        set(value) { state.value = value }
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
    // flatMapLatest 重新進入背景時，Trixnity 可能重播通知流中已有事件；以事件 ID
    // 去重，避免同一則同步事件再次觸發 Android/Desktop 系統通知。只保留有限窗口，
    // 避免長時間同步讓這個觀察器無界增長。
    val notifiedEventIds = LinkedHashSet<String>()
    AppNotifications.foreground
        .flatMapLatest { inForeground ->
            if (inForeground) emptyFlow() else client.notification.getNotifications()
        }
        .collect { notification ->
            if (notification.actions.none { it is PushAction.Notify }) return@collect
            val event = notification.event as? ClientEvent.RoomEvent.MessageEvent<*> ?: return@collect
            if (event.sender == myUserId) return@collect
            if (!notifiedEventIds.add(event.id.full)) return@collect
            if (notifiedEventIds.size > MaxRememberedNotificationIds) {
                notifiedEventIds.remove(notifiedEventIds.first())
            }
            val strings = stringsFor(persistedLanguage())
            val body = event.content.messageBodyOrNull() ?: return@collect
            val text = when (body) {
                is MessageBody.Text -> body.text
                is MessageBody.Image -> if (body.isSticker) strings.notifSticker else strings.notifImage
                // 相簿是時間線才做的併組，通知逐則事件永遠不會是 Album；這是穷尽分支的保险
                is MessageBody.Album -> strings.notifImage
                is MessageBody.Voice -> strings.voiceMessage
                is MessageBody.Attachment -> body.name
                MessageBody.Undecryptable -> strings.notifUndecryptable
            }
            val roomId = event.roomId
            // 靜音的房間不彈系統通知（用戶 2026-09-30 #48：設了靜音還在通知）。
            // 逐則查一次就好：通知本就稀有，為它展開整份推播規則流反而更貴。
            if (rooms.isMuted(roomId)) return@collect
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

private const val MaxRememberedNotificationIds = 512
