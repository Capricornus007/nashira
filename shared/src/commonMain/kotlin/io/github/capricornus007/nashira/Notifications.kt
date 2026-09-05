package io.github.capricornus007.nashira

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import de.connect2x.trixnity.core.model.RoomId
import de.connect2x.trixnity.core.model.UserId
import io.github.capricornus007.nashira.i18n.stringsFor
import io.github.capricornus007.nashira.matrix.MessageBody
import io.github.capricornus007.nashira.matrix.RoomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
 * 自己發的、邀請房、前台期間到達的都不發；同房重複通知由平台以同一 notificationId 更新。
 */
@Composable
internal fun NotificationHost(roomRepository: RoomRepository, myUserId: UserId) {
    val unread by remember(roomRepository) { roomRepository.unreadByRoom() }
        .collectAsState(initial = emptyMap())
    val summaries by remember(roomRepository) { roomRepository.roomSummaries() }
        .collectAsState(initial = emptyList())
    val strings = stringsFor(LocalUiState.current.language)
    val seenUnread = remember(roomRepository) { mutableStateMapOf<RoomId, Boolean>() }

    LaunchedEffect(unread, summaries) {
        if (AppNotifications.foreground.value) return@LaunchedEffect
        unread.forEach { (roomId, state) ->
            val previous = seenUnread[roomId]
            seenUnread[roomId] = state.unread
            if (previous == null || !state.unread) return@forEach
            val room = summaries.firstOrNull { it.roomId == roomId } ?: return@forEach
            if (room.isInvite) return@forEach
            val last = roomRepository.lastMessage(roomId).firstOrNull() ?: return@forEach
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
