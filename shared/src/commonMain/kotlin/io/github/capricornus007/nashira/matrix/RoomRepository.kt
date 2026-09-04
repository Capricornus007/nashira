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
import kotlinx.coroutines.flow.MutableStateFlow
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
    /** 只是被邀請、還沒加入；清單上顯示接受／拒絕 */
    val isInvite: Boolean = false,
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
 * 時間線的一頁。[eventCount] 是這一頁實際拿到的原始事件數（含不可顯示的成員／狀態事件）：
 * 它到頂了才代表還有更早的歷史可以要，比對 [messages].size 會因為過濾而永遠不成立。
 */
data class TimelinePage(
    val messages: List<TimelineMessage>,
    val eventCount: Int,
)

/**
 * 房間數據門面：把 MatrixClient 的 store 流轉成 UI 直接可用的摘要/時間線。
 */
class RoomRepository(val client: MatrixClient) {

    /** 清單顯示已加入與被邀請的房間；leave/ban/knock 不顯示。 */
    private fun visible(room: Room): Boolean =
        room.membership == Membership.JOIN || room.membership == Membership.INVITE

    private fun isSpace(room: Room): Boolean = room.type is CreateEventContent.RoomType.Space

    /** 全部已加入房間的摘要流（sync 後自動更新）。 */
    fun roomSummaries(): Flow<List<RoomSummary>> =
        joinedRooms()
            .map { rooms -> rooms.filterNot(::isSpace).map(::summaryOf).sortedByActivity() }
            .flowOn(Dispatchers.Default)

    private fun joinedRooms(): Flow<List<Room>> =
        client.room.getAll().flattenValues().map { rooms -> rooms.filter(::visible) }

    /** 接受邀請。加入後 sync 會把房間轉成 JOIN，清單自動更新。 */
    suspend fun acceptInvite(roomId: RoomId): Result<Unit> = runCatching {
        client.api.room.joinRoom(roomId).getOrThrow()
        Unit
    }

    /** 拒絕邀請：離開房間並忘記它，避免它繼續留在清單裡。 */
    suspend fun declineInvite(roomId: RoomId): Result<Unit> = runCatching {
        client.api.room.leaveRoom(roomId).getOrThrow()
        client.room.forgetRoom(roomId)
    }

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
        isInvite = room.membership == Membership.INVITE,
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
     * 指定房間的最新時間線。發送者顯示名與頭像來自房間成員狀態，
     * 成員資料補齊後訊息會自動換上真名，不再只顯示 Matrix ID 的 localpart。
     *
     * 回傳順序是「新 → 舊」（索引 0 最新）。[maxSize] 由呼叫端持有：往上滾到頭時把它加大，
     * Trixnity 會自己往伺服器補抓更早的事件，這就是「載入更多歷史」。
     */
    fun timeline(roomId: RoomId, maxSize: MutableStateFlow<Int>): Flow<TimelinePage> =
        combine(
            client.room.getLastTimelineEvents(roomId).toFlowList(maxSize),
            memberMap(roomId),
        ) { eventFlows, members ->
            TimelinePage(
                // 原始事件數要在過濾前算：房間裡大量加入／改名事件不可顯示，
                // 用過濾後的訊息數去比對 maxSize 永遠比不到，換頁條件就再也不會成立。
                eventCount = eventFlows.size,
                messages = eventFlows.mapNotNull { eventFlow ->
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
                },
            )
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
