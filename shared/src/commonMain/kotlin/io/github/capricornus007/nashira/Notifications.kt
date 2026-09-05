package io.github.capricornus007.nashira

import androidx.compose.runtime.Composable
import de.connect2x.trixnity.core.model.EventId
import de.connect2x.trixnity.core.model.RoomId
import de.connect2x.trixnity.core.model.UserId
import io.github.capricornus007.nashira.i18n.AppLanguage
import io.github.capricornus007.nashira.i18n.stringsFor
import io.github.capricornus007.nashira.matrix.MessageBody
import io.github.capricornus007.nashira.matrix.RoomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull

/** 平台通知能力接口；Android 在 MainActivity 注入實現，桌面端用默認 no-op。 */
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
 * 新訊息通知的觸發端：盯每個房間的未讀流，未讀由 false → true 的邊沿發一則通知。
 *
 * **不是 composable**：以前掛在 Compose 樹上，App 一離開前台、UI 不再重組就停了，
 * 而通知恰恰只在背景才需要發。改成跟著 [io.github.capricornus007.nashira.matrix.MatrixSession]
 * 的生命週期跑，Android 端由前台服務保住行程即可。
 *
 * 自己發的、邀請房、前台期間到達的都不發；同房重複通知由平台以同一 notificationId 更新。
 */
internal suspend fun watchUnreadForNotifications(roomRepository: RoomRepository, myUserId: UserId) {
    // 每個房間「已經通知過的最後一則」。第一次看到某個房間只記基線不發通知，
    // 否則一啟動就把所有既有未讀全部彈一遍（實測會一次冒出二十幾條）。
    val notified = mutableMapOf<RoomId, EventId?>()
    combine(roomRepository.unreadByRoom(), roomRepository.roomSummaries()) { unread, summaries ->
        unread to summaries
    }.collect { (unread, summaries) ->
        // 語言設定存在磁碟上（UiState 只活在 Compose 樹裡），這裡直接讀
        val strings = stringsFor(persistedLanguage())
        unread.forEach { (roomId, state) ->
            val room = summaries.firstOrNull { it.roomId == roomId } ?: return@forEach
            if (room.isInvite) return@forEach
            val last = roomRepository.lastMessage(roomId).firstOrNull()
            val seen = notified.containsKey(roomId)
            val previous = notified[roomId]
            notified[roomId] = last?.eventId
            // 只在「這個房間出現了比上次通知更新的訊息」時才發：
            // 單看 unread 旗標會在每次 sync 重發同一則（旗標一直是 true）。
            if (!seen || !state.unread) return@forEach
            if (last == null || last.eventId == previous) return@forEach
            if (AppNotifications.foreground.value) return@forEach
            if (last.sender == myUserId) return@forEach
            val body = when (val content = last.body) {
                is MessageBody.Text -> content.text
                is MessageBody.Image -> if (content.isSticker) strings.notifSticker else strings.notifImage
                is MessageBody.Attachment -> content.name
                MessageBody.Undecryptable -> strings.notifUndecryptable
            }
            AppNotifications.show(
                roomKey = roomId.full,
                title = "${room.name} · ${last.senderName}",
                body = body,
                mentionCount = state.count,
            )
        }
    }
}

private fun persistedLanguage(): AppLanguage {
    val stored = runCatching { SettingsStorage().load() }.getOrNull()?.get("language")
    return AppLanguage.entries.firstOrNull { it.name == stored } ?: AppLanguage.ZH_TW
}
