package io.github.capricornus007.nashira.matrix

import de.connect2x.trixnity.client.MatrixClient
import de.connect2x.trixnity.client.room.RoomService
import de.connect2x.trixnity.client.room
import de.connect2x.trixnity.client.room.message.text
import de.connect2x.trixnity.client.room.toFlowList
import kotlinx.coroutines.flow.first
import de.connect2x.trixnity.client.room.getState
import de.connect2x.trixnity.core.model.EventId
import de.connect2x.trixnity.core.model.RoomId
import de.connect2x.trixnity.core.model.events.m.room.NameEventContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/** UI 友好的房間摘要 */
data class RoomSummary(
    val roomId: RoomId,
    val name: String,
    val isDirect: Boolean,
)

/** 時間線上的單條訊息 */
data class TimelineMessage(
    val eventId: EventId?,
    val roomId: RoomId,
    val sender: String,
    val content: String,
    val timestamp: Long,
)

/**
 * 房間數據門面：把 MatrixClient 的 store 流轉成 UI 直接可用的摘要/時間線。
 */
class RoomRepository(private val client: MatrixClient) {

    /** 全部已加入房間的摘要流（sync 後自動更新） */
    fun roomSummaries(): Flow<List<RoomSummary>> =
        client.room.getAll().map { roomFlows ->
            roomFlows.map { (roomId, roomFlow) ->
                combine(
                    roomFlow,
                    client.room.getState(roomId, NameEventContent::class),
                ) { room, nameEvent ->
                    RoomSummary(
                        roomId = roomId,
                        name = nameEvent?.content?.name ?: roomId.full,
                        isDirect = room?.isDirect == true,
                    )
                }
            }
        }.flatMapLatest { flows ->
            if (flows.isEmpty()) flowOf(emptyList()) else combine(flows) { it.toList() }
        }

    /** 指定房間的最新時間線訊息流（取最後 N 條，新訊息到達自動更新） */
    fun timeline(roomId: RoomId, limit: Int = 50): Flow<List<TimelineMessage>> =
        client.room.getLastTimelineEvents(roomId)
            .toFlowList(kotlinx.coroutines.flow.MutableStateFlow(limit))
            .map { eventFlows ->
                eventFlows.mapNotNull { eventFlow ->
                    val timelineEvent = eventFlow.first()
                    val roomEvent = timelineEvent.event
                    TimelineMessage(
                        eventId = roomEvent.id,
                        roomId = roomId,
                        sender = roomEvent.sender.full,
                        content = (roomEvent.content as? de.connect2x.trixnity.core.model.events.m.room.RoomMessageEventContent.TextBased)
                            ?.body ?: return@mapNotNull null,
                        timestamp = roomEvent.originTimestamp,
                    )
                }
            }

    /** 發送文字訊息 */
    suspend fun sendText(roomId: RoomId, body: String): Result<String> =
        client.room.sendMessage(roomId) {
            text(body)
        }.let { Result.success(it) }
}
