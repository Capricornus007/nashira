package io.github.capricornus007.nashira.matrix

import de.connect2x.trixnity.client.MatrixClient
import de.connect2x.trixnity.client.room
import de.connect2x.trixnity.client.notification
import de.connect2x.trixnity.client.room.getState
import de.connect2x.trixnity.client.room.message.text
import de.connect2x.trixnity.client.room.toFlowList
import de.connect2x.trixnity.client.flattenNotNull
import de.connect2x.trixnity.client.flattenValues
import de.connect2x.trixnity.client.store.Room
import de.connect2x.trixnity.client.store.RoomUser
import de.connect2x.trixnity.client.store.TimelineEvent
import de.connect2x.trixnity.client.store.type
import de.connect2x.trixnity.client.user
import kotlinx.coroutines.flow.first
import de.connect2x.trixnity.core.model.EventId
import de.connect2x.trixnity.core.model.RoomId
import de.connect2x.trixnity.core.model.UserId
import de.connect2x.trixnity.core.model.events.m.room.CanonicalAliasEventContent
import de.connect2x.trixnity.core.model.events.m.room.CreateEventContent
import de.connect2x.trixnity.core.model.events.m.room.Membership
import de.connect2x.trixnity.core.model.events.m.room.RoomMessageEventContent
import de.connect2x.trixnity.core.model.events.m.space.ChildEventContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/** UI 友好的房間摘要 */
data class RoomSummary(
    val roomId: RoomId,
    val name: String,
    val isDirect: Boolean,
    val avatarUrl: String? = null,
    /** 房間沒有自己的頭像時，用這些成員的頭像代替（私訊就是對方） */
    val heroes: List<UserId> = emptyList(),
    val spaceIds: Set<RoomId> = emptySet(),
    /** 最後一則相關事件的時間；清單按此遞減排序，避免每次同步重排造成跳動 */
    val lastActivity: Long = 0L,
)

/** Space／房間的未讀狀態：Discord 用小白點表示有未讀、紅圈數字表示提及數。 */
data class UnreadState(
    val unread: Boolean = false,
    val count: Int = 0,
)

data class SpaceSummary(
    val roomId: RoomId,
    val name: String,
    val avatarUrl: String? = null,
)

data class SpacesSnapshot(
    val spaces: List<SpaceSummary>,
    val rooms: List<RoomSummary>,
)

/** 房間成員（成員欄與訊息頭像共用） */
data class RoomMember(
    val userId: UserId,
    val name: String,
    val avatarUrl: String?,
)

/** 時間線上的單條訊息。發送者顯示名與頭像取自房間成員狀態，取不到才退回 localpart。 */
data class TimelineMessage(
    val eventId: EventId?,
    val roomId: RoomId,
    val sender: UserId,
    val senderName: String,
    val senderAvatarUrl: String?,
    val content: String,
    val timestamp: Long,
)

/**
 * 房間數據門面：把 MatrixClient 的 store 流轉成 UI 直接可用的摘要/時間線。
 */
class RoomRepository(val client: MatrixClient) {

    /** 已加入的房間才進清單；invite 待通知處理，leave/ban 不顯示。 */
    private fun visible(room: Room): Boolean = room.membership == Membership.JOIN

    private fun isSpace(room: Room): Boolean = room.type is CreateEventContent.RoomType.Space

    /** 全部已加入房間的摘要流（sync 後自動更新）。 */
    fun roomSummaries(): Flow<List<RoomSummary>> =
        joinedRooms()
            .map { rooms -> rooms.filterNot(::isSpace).map(::summaryOf).sortedByActivity() }
            .flowOn(Dispatchers.Default)

    private fun joinedRooms(): Flow<List<Room>> =
        client.room.getAll().flattenValues().map { rooms -> rooms.filter(::visible) }

    /**
     * 房間與 Matrix Space 的從屬關係。
     *
     * 關係由 Space 自身的 `m.space.child` 狀態事件決定（每個 Space 一條查詢），
     * 而不是逐個房間反查 `m.space.parent`：後者要對每個房間開一條 state 流，
     * 房間集合在初次同步期間持續變動會讓 combine 一直重啟，子聊天室因此遲遲不出現。
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun spacesSnapshot(): Flow<SpacesSnapshot> {
        val rooms = joinedRooms()
        // Space 集合變動才重建子房間查詢；一般房間更新不影響。
        val spaceIds = rooms
            .map { list -> list.filter(::isSpace).map { it.roomId }.toSet() }
            .distinctUntilChanged()
        val childrenBySpace = spaceIds.flatMapLatest { ids ->
            if (ids.isEmpty()) {
                flowOf(emptyMap())
            } else {
                combine(ids.map { id -> spaceChildren(id).map { id to it } }) { it.toMap() }
            }
        }
        return combine(rooms, childrenBySpace) { list, children ->
            val spaceOf = mutableMapOf<RoomId, MutableSet<RoomId>>()
            children.forEach { (space, childIds) ->
                childIds.forEach { child -> spaceOf.getOrPut(child) { mutableSetOf() }.add(space) }
            }
            SpacesSnapshot(
                spaces = list.filter(::isSpace).map(::spaceSummary).sortedBy { it.name.lowercase() },
                rooms = list.filterNot(::isSpace)
                    .map { room -> summaryOf(room).copy(spaceIds = spaceOf[room.roomId].orEmpty()) }
                    .sortedByActivity(),
            )
        }.distinctUntilChanged().flowOn(Dispatchers.Default)
    }

    /**
     * 每個房間的未讀狀態。Trixnity 的 notification count 只算「會產生通知的訊息」，
     * 有未讀但不觸發通知的房間 count 為 0，所以白點另外看 isUnread。
     */
    fun unreadByRoom(): Flow<Map<RoomId, UnreadState>> =
        joinedRooms()
            .map { rooms -> rooms.filterNot(::isSpace).map { it.roomId } }
            .distinctUntilChanged()
            .flatMapLatest { ids ->
                if (ids.isEmpty()) {
                    flowOf(emptyMap())
                } else {
                    combine(ids.map { id -> unreadOf(id).map { id to it } }) { it.toMap() }
                }
            }
            .flowOn(Dispatchers.Default)

    private fun unreadOf(roomId: RoomId): Flow<UnreadState> =
        combine(
            client.notification.isUnread(roomId),
            client.notification.getCount(roomId),
        ) { unread, count -> UnreadState(unread = unread || count > 0, count = count) }
            .distinctUntilChanged()

    /**
     * 單一 Space 的子聊天室。先用 state key 立即給出結果，再用事件內容剔除已移除的子項
     * （被撤下的 `m.space.child` 內容為空，state key 仍留在 store 裡）。
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun spaceChildren(spaceId: RoomId): Flow<Set<RoomId>> =
        client.room.getAllState(spaceId, ChildEventContent::class)
            .flatMapLatest { stateFlows ->
                if (stateFlows.isEmpty()) {
                    flowOf(emptySet())
                } else {
                    combine(
                        stateFlows.map { (stateKey, events) -> events.map { stateKey to (it != null) } },
                    ) { pairs ->
                        pairs.mapNotNullTo(mutableSetOf()) { (stateKey, present) ->
                            if (present) stateKey.toRoomIdOrNull() else null
                        }
                    }.onStart {
                        emit(stateFlows.keys.mapNotNullTo(mutableSetOf()) { it.toRoomIdOrNull() })
                    }
                }
            }
            .distinctUntilChanged()

    private fun summaryOf(room: Room): RoomSummary = RoomSummary(
        roomId = room.roomId,
        name = displayNameOf(room),
        isDirect = room.isDirect,
        avatarUrl = room.avatarUrl,
        heroes = room.name?.heroes.orEmpty(),
        lastActivity = room.lastRelevantEventTimestamp?.toEpochMilliseconds() ?: 0L,
    )

    private fun spaceSummary(room: Room): SpaceSummary = SpaceSummary(
        roomId = room.roomId,
        name = displayNameOf(room),
        avatarUrl = room.avatarUrl,
    )

    private fun displayNameOf(room: Room): String {
        room.name?.explicitName?.takeIf { it.isNotBlank() }?.let { return it }
        val heroes = room.name?.heroes.orEmpty()
        if (heroes.isNotEmpty()) {
            return heroes.joinToString(", ") { it.full.removePrefix("@").substringBefore(':') }
        }
        return room.roomId.full
    }

    /** 房間成員（僅已載入的成員；Trixnity 依 lazy-load 逐步補齊）。 */
    fun members(roomId: RoomId): Flow<List<RoomMember>> =
        memberMap(roomId)
            .map { members ->
                members.values
                    .map { user ->
                        RoomMember(
                            userId = user.userId,
                            name = user.name.visibleNameOrNull() ?: user.userId.full.removePrefix("@").substringBefore(':'),
                            avatarUrl = user.event.content.avatarUrl,
                        )
                    }
                    .sortedBy { it.name.lowercase() }
            }
            .flowOn(Dispatchers.Default)

    private fun memberMap(roomId: RoomId): Flow<Map<UserId, RoomUser>> =
        client.user.getAll(roomId).flattenNotNull()

    /** 房間的正式別名（#room:server），供標題與輸入框顯示真實目標。 */
    fun canonicalAlias(roomId: RoomId): Flow<String?> =
        client.room.getState<CanonicalAliasEventContent>(roomId)
            .map { event -> event?.content?.alias?.full }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)

    /** 單一成員的頭像；私訊沒有房間頭像時用對方的頭像（與 Element 行為一致）。 */
    fun memberAvatar(roomId: RoomId, userId: UserId): Flow<String?> =
        client.user.getById(roomId, userId)
            .map { it?.event?.content?.avatarUrl }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)

    /**
     * 指定房間的最新時間線訊息流。發送者顯示名與頭像來自房間成員狀態，
     * 成員資料補齊後訊息會自動換上真名，不再只顯示 Matrix ID 的 localpart。
     */
    fun timeline(roomId: RoomId, limit: Int = 50): Flow<List<TimelineMessage>> =
        combine(
            client.room.getLastTimelineEvents(roomId)
                .toFlowList(kotlinx.coroutines.flow.MutableStateFlow(limit)),
            memberMap(roomId),
        ) { eventFlows, members ->
            eventFlows.mapNotNull { eventFlow ->
                val timelineEvent = eventFlow.first()
                val roomEvent = timelineEvent.event
                val member = members[roomEvent.sender]
                TimelineMessage(
                    eventId = roomEvent.id,
                    roomId = roomId,
                    sender = roomEvent.sender,
                    senderName = member?.name.visibleNameOrNull()
                        ?: roomEvent.sender.full.removePrefix("@").substringBefore(':'),
                    senderAvatarUrl = member?.event?.content?.avatarUrl,
                    content = timelineEvent.bodyOrNull() ?: return@mapNotNull null,
                    timestamp = roomEvent.originTimestamp,
                )
            }
        }.flowOn(Dispatchers.Default)

    /** 聊天室清單的最後一則訊息預覽（含發送者），對齊 Discord 的兩行列。 */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun lastMessage(roomId: RoomId): Flow<TimelineMessage?> =
        client.room.getLastTimelineEvent(roomId)
            .flatMapLatest { eventFlow -> eventFlow ?: flowOf(null) }
            .combine(memberMap(roomId)) { timelineEvent, members ->
                val roomEvent = timelineEvent?.event ?: return@combine null
                val body = timelineEvent.bodyOrNull() ?: return@combine null
                val member = members[roomEvent.sender]
                TimelineMessage(
                    eventId = roomEvent.id,
                    roomId = roomId,
                    sender = roomEvent.sender,
                    senderName = member?.name.visibleNameOrNull()
                        ?: roomEvent.sender.full.removePrefix("@").substringBefore(':'),
                    senderAvatarUrl = member?.event?.content?.avatarUrl,
                    content = body,
                    timestamp = roomEvent.originTimestamp,
                )
            }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)

    /**
     * 進房時把已讀標記推到最後一則事件：未讀白條與紅圈數字才會消掉，
     * 也讓其他客戶端看到同一個已讀位置。
     */
    suspend fun markRead(roomId: RoomId) {
        val lastEventId = client.room.getById(roomId).first()?.lastEventId ?: return
        client.api.room.setReadMarkers(roomId, fullyRead = lastEventId, read = lastEventId)
    }

    /** 發送文字訊息 */
    suspend fun sendText(roomId: RoomId, body: String): Result<String> =
        runCatching {
            client.room.sendMessage(roomId) {
                text(body)
            }
        }
}

/**
 * `m.space.child` 的 state key 是房間 ID。房間 v12 起的 ID 沒有 `:server` 後綴，
 * 所以只能用 Matrix 的 ID 規則判斷，不能自己檢查冒號。
 */
private fun String.toRoomIdOrNull(): RoomId? =
    if (RoomId.isValid(this)) RoomId(this) else null

/**
 * 取訊息本體。加密房間的明文在 `TimelineEvent.content`（解密後的 Result），
 * 直接讀 `event.content` 只會拿到 `m.room.encrypted`，訊息會整批消失。
 */
private fun TimelineEvent.bodyOrNull(): String? =
    (content?.getOrNull() ?: event.content).let { it as? RoomMessageEventContent }?.body

/**
 * 顯示名可能只由不可見字元組成（Telegram 橋接使用者常見，例如 U+2063 INVISIBLE SEPARATOR），
 * 這種名稱在 UI 上等於空白，應該退回 Matrix ID 的 localpart。
 */
private fun String?.visibleNameOrNull(): String? {
    if (this == null) return null
    val visible = filterNot { ch ->
        ch.isWhitespace() || ch.code in 0x200B..0x200F || ch.code in 0x2060..0x2064 || ch.code == 0xFEFF
    }
    return if (visible.isEmpty()) null else this
}

private fun List<RoomSummary>.sortedByActivity(): List<RoomSummary> =
    sortedWith(compareByDescending<RoomSummary> { it.lastActivity }.thenBy { it.name.lowercase() })
