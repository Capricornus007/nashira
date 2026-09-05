package io.github.capricornus007.nashira.matrix

import de.connect2x.trixnity.client.MatrixClient
import de.connect2x.trixnity.client.room
import de.connect2x.trixnity.client.notification
import de.connect2x.trixnity.client.room.getAccountData
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
import kotlinx.coroutines.flow.firstOrNull
import de.connect2x.trixnity.core.model.EventId
import de.connect2x.trixnity.core.model.RoomId
import de.connect2x.trixnity.core.model.events.m.room.EncryptionEventContent
import de.connect2x.trixnity.core.model.events.m.room.ImageInfo
import de.connect2x.trixnity.utils.toByteArrayFlow
import io.github.capricornus007.nashira.PickedImage
import de.connect2x.trixnity.core.model.UserId
import kotlinx.coroutines.flow.MutableStateFlow
import de.connect2x.trixnity.core.model.events.m.room.CanonicalAliasEventContent
import de.connect2x.trixnity.core.model.events.m.room.CreateEventContent
import de.connect2x.trixnity.core.model.events.m.room.Membership
import de.connect2x.trixnity.core.model.events.m.room.RoomMessageEventContent
import de.connect2x.trixnity.core.model.events.UnknownEventContent
import de.connect2x.trixnity.core.model.events.m.room.EncryptedMessageEventContent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import de.connect2x.trixnity.core.model.events.m.room.EncryptedFile
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.intOrNull
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
import de.connect2x.trixnity.client.room.message.reply
import de.connect2x.trixnity.core.model.events.m.MarkedUnreadEventContent
import de.connect2x.trixnity.core.model.events.m.TagEventContent
import io.github.capricornus007.nashira.PickedFile

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
    /** `m.tag` 標籤（`m.favourite` / `m.lowpriority`）：收藏排最前、低優先排最後 */
    val tags: Set<String> = emptySet(),
)

/** Space／房間的未讀狀態：Discord 用小白點表示有未讀、紅圈數字表示提及數。 */
data class UnreadState(
    val unread: Boolean = false,
    val count: Int = 0,
    /** 使用者手動標記的未讀（`m.marked_unread`），與「有新訊息」區分開。 */
    val markedUnread: Boolean = false,
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

/**
 * 訊息內容。圖片與貼圖要保留媒體來源（加密房的圖是 EncryptedFile，不是 mxc URL），
 * 解密失敗也要留下痕跡：直接丟掉會讓時間線看起來「少了幾句」卻找不出原因。
 */
sealed interface MessageBody {
    /** 純文字、emote、通知 */
    data class Text(val text: String) : MessageBody

    /** 圖片或貼圖。[caption] 是原始 body（檔名或描述），貼圖不畫背景也不裁切。 */
    data class Image(
        val caption: String,
        val source: MediaSource,
        val width: Int?,
        val height: Int?,
        val isSticker: Boolean,
    ) : MessageBody

    /** 檔案／音訊／影片：先用檔名標示，還沒做內建播放 */
    data class Attachment(val name: String) : MessageBody

    /** 這台裝置拿不到金鑰 */
    data object Undecryptable : MessageBody
}

/** 媒體來源：未加密房是 mxc URL，加密房是帶金鑰的 EncryptedFile。 */
sealed interface MediaSource {
    data class Plain(val mxcUrl: String) : MediaSource
    data class Encrypted(val file: EncryptedFile) : MediaSource
}

/** 時間線上的單條訊息。發送者顯示名與頭像取自房間成員狀態，取不到才退回 localpart。 */
data class TimelineMessage(
    val eventId: EventId?,
    val roomId: RoomId,
    val sender: UserId,
    val senderName: String,
    val senderAvatarUrl: String?,
    val body: MessageBody,
    val timestamp: Long,
    /** 還在 outbox（本機回顯）：尚未被伺服器回音確認。 */
    val pending: Boolean = false,
    /** outbox 送出失敗的原因；非 null 時 UI 要標紅並允許重試。 */
    val sendError: String? = null,
)

/**
 * 時間線的一頁。[eventCount] 是這一頁實際拿到的原始事件數（含不可顯示的成員／狀態事件）：
 * 它到頂了才代表還有更早的歷史可以要，比對 [messages].size 會因為過濾而永遠不成立。
 */
data class TimelinePage(
    val messages: List<TimelineMessage>,
    val eventCount: Int,
    /** 是否還有更早的歷史可以抓。 */
    val canLoadMore: Boolean = false,
    /** 正在抓更早的一頁（UI 在頂部擺轉圈）。 */
    val loadingBefore: Boolean = false,
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
    @OptIn(ExperimentalCoroutinesApi::class)
    fun roomSummaries(): Flow<List<RoomSummary>> =
        joinedRooms()
            .map { rooms -> rooms.filterNot(::isSpace).map(::summaryOf) }
            .flatMapLatest { summaries ->
                if (summaries.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    // 標籤要進排序：不然「收藏／低優先」按了只是寫給別的客戶端看，
                    // Nashira 自己的清單毫無變化，使用者當然覺得那兩個選項沒作用。
                    combine(summaries.map { summary -> tagsOf(summary.roomId).map { summary.copy(tags = it) } }) {
                        it.toList().sortedByTagsAndActivity()
                    }
                }
            }
            .flowOn(Dispatchers.Default)

    /** 房間的 `m.tag` 名稱集合，取自已同步的房間 account data。 */
    private fun tagsOf(roomId: RoomId): Flow<Set<String>> =
        client.room.getAccountData<TagEventContent>(roomId)
            .map { content -> content?.tags?.keys?.map { it.name }?.toSet() ?: emptySet() }
            .distinctUntilChanged()

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
            markedUnread(roomId),
        ) { unread, count, marked ->
            // 手動標記的未讀也算未讀，否則「標記為未讀」按了畫面完全沒反應
            // （isUnread 只看有沒有需要通知的新訊息，不看 m.marked_unread）
            UnreadState(unread = unread || count > 0 || marked, count = count, markedUnread = marked)
        }
            .distinctUntilChanged()

    /** MSC2867 的手動未讀標記（`m.marked_unread` 房間 account data）。 */
    fun markedUnread(roomId: RoomId): Flow<Boolean> =
        client.room.getAccountData<MarkedUnreadEventContent>(roomId)
            .map { it?.unread == true }
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

    /**
     * 房間成員（顯示名／頭像）對照表。
     *
     * 大房間預設沒有載入完整成員（`Room.membersLoaded == false`），這條流在載入完成前**不會發**，
     * 所以一律補一個空 map 起頭：訊息不該等成員資料才顯示。同時觸發一次成員載入，
     * 名字與頭像補齊後流會再發、UI 自動換上真名。
     */
    private fun memberMap(roomId: RoomId): Flow<Map<UserId, RoomUser>> =
        client.user.getAll(roomId).flattenNotNull()
            .onStart {
                emit(emptyMap())
                runCatching { client.user.loadMembers(roomId) }
            }

    /** 房間的正式別名（#room:server），供標題與輸入框顯示真實目標。 */
    fun canonicalAlias(roomId: RoomId): Flow<String?> =
        client.room.getState<CanonicalAliasEventContent>(roomId)
            .map { event -> event?.content?.alias?.full }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)

    /**
     * 單一成員的顯示名。**通知路徑必須用這個而不是 [members]**：members 為了不卡住大房
     * 會先發一次空表（大房 membersLoaded=false 時 getAll 根本不發），
     * 取 first() 就永遠拿到空的、於是每個發送者都退回 localpart。
     * 這裡直接讀單一成員，讀不到才觸發一次載入再試。
     *
     * 回 null 的情況是該帳號在 Matrix 側真的沒有可用顯示名（Telegram 橋接使用者常見：
     * displayname 只由不可見字元組成），這種情況所有客戶端都只能顯示 localpart。
     */
    suspend fun memberName(roomId: RoomId, userId: UserId): String? {
        client.user.getById(roomId, userId).firstOrNull()?.name.visibleNameOrNull()?.let { return it }
        runCatching { client.user.loadMembers(roomId) }
        return client.user.getById(roomId, userId).firstOrNull()?.name.visibleNameOrNull()
    }

    /** 單一成員的頭像；私訊沒有房間頭像時用對方的頭像（與 Element 行為一致）。 */
    fun memberAvatar(roomId: RoomId, userId: UserId): Flow<String?> =
        client.user.getById(roomId, userId)
            .map { it?.event?.content?.avatarUrl }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)

    /**
     * 指定房間的最新時間線。回傳順序是「新 → 舊」（索引 0 最新）。
     *
     * 直接用 `getLastTimelineEvents` 從 store 往後走有致命缺陷：多數房間 store 裡的
     * `previousEventId` 鏈是斷的（gap 未補、缺檔），往回一步就停，時間線永遠空白。
     * 改用有狀態的 [de.connect2x.trixnity.client.room.Timeline]：它知道用 sync token
     * 補抓 gap、解密會更新內層事件流並重新發射，也允許手動 `loadBefore` 載入更早歷史。
     *
     * 這條流是冷的：每次訂閱都重建一個 Timeline 並以最後一則事件開頭，結束即丟。
     * 同一個房間只會有一份在訂閱，成本可以接受。
     */
    fun timeline(roomId: RoomId): RoomTimeline = RoomTimeline(client, roomId, memberMap(roomId))

    /** 聊天室清單的最後一則訊息預覽（含發送者），對齊 Discord 的兩行列。 */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun lastMessage(roomId: RoomId): Flow<TimelineMessage?> =
        client.room.getLastTimelineEvent(roomId)
            .flatMapLatest { eventFlow -> eventFlow ?: flowOf(null) }
            .combine(memberMap(roomId)) { timelineEvent, members ->
                val roomEvent = timelineEvent?.event ?: return@combine null
                val body = timelineEvent.messageBodyOrNull() ?: return@combine null
                val member = members[roomEvent.sender]
                TimelineMessage(
                    eventId = roomEvent.id,
                    roomId = roomId,
                    sender = roomEvent.sender,
                    senderName = member?.name.visibleNameOrNull()
                        ?: roomEvent.sender.full.removePrefix("@").substringBefore(':'),
                    senderAvatarUrl = member?.event?.content?.avatarUrl,
                    body = body,
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

    /** 發送貼圖（MSC2545 m.sticker）：走 StickerRepository 的加密路徑，回 eventId 便於撤回 */
    suspend fun sendSticker(roomId: RoomId, sticker: StickerItem): Result<String> =
        StickerRepository(client).sendSticker(roomId, sticker)

    /** 回覆某則訊息（m.in_reply_to）。 */
    suspend fun sendReply(roomId: RoomId, replyTo: EventId, body: String): Result<String> =
        runCatching {
            client.room.sendMessage(roomId) {
                reply(replyTo, null)
                text(body)
            }
        }

    /** 撤回訊息（m.room.redaction）。只有自己的訊息或有權限時伺服器才會接受。 */
    suspend fun redact(roomId: RoomId, eventId: EventId): Result<Unit> =
        runCatching { client.api.room.redactEvent(roomId, eventId).getOrThrow() }.map { }

    /**
     * 標記／取消標記未讀（MSC2867 `m.marked_unread`）。
     * 這是房間層的 account data，其他客戶端也看得到同一個狀態。
     */
    suspend fun setMarkedUnread(roomId: RoomId, unread: Boolean): Result<Unit> =
        runCatching {
            client.api.room.setAccountData(MarkedUnreadEventContent(unread), roomId, client.userId).getOrThrow()
        }

    /** 收藏／低優先等 `m.tag`；enabled=false 時刪掉該標籤。 */
    suspend fun setTag(roomId: RoomId, tag: String, enabled: Boolean): Result<Unit> =
        runCatching {
            if (enabled) {
                client.api.room.setTag(client.userId, roomId, tag, TagEventContent.Tag()).getOrThrow()
            } else {
                client.api.room.deleteTag(client.userId, roomId, tag).getOrThrow()
            }
        }

    /** 目前生效的 `m.tag` 名稱集合（`m.favourite` / `m.lowpriority` …）。 */
    suspend fun tags(roomId: RoomId): Set<String> =
        client.api.room.getTags(client.userId, roomId).getOrNull()
            ?.tags?.keys?.map { it.name }?.toSet()
            ?: emptySet()

    /** 離開房間或 Space。 */
    suspend fun leave(roomId: RoomId): Result<Unit> =
        runCatching { client.api.room.leaveRoom(roomId).getOrThrow() }

    /** 邀請使用者加入房間或 Space。 */
    suspend fun invite(roomId: RoomId, userId: String): Result<Unit> =
        runCatching { client.api.room.inviteUser(roomId, UserId(userId)).getOrThrow() }

    /**
     * matrix.to 永久連結。房間有正式別名就用別名（別人點得開），否則用房間 ID
     * 並附上 via 參數——沒有 via，對方伺服器不知道去哪裡問這個房間。
     */
    suspend fun permalink(roomId: RoomId, eventId: EventId? = null): String {
        val alias = client.room.getState<CanonicalAliasEventContent>(roomId).firstOrNull()?.content?.alias?.full
        val target = alias ?: roomId.full
        val via = if (alias == null) "?via=${client.userId.domain}" else ""
        val event = eventId?.let { "/${it.full}" } ?: ""
        return "https://matrix.to/#/$target$event$via"
    }

    /** 發送任意檔案（m.file）。上傳路徑與 sendImage 相同，只是 content 型別不同。 */
    suspend fun sendFile(roomId: RoomId, picked: PickedFile): Result<String> = runCatching {
        val mediaService = client.di.get<de.connect2x.trixnity.client.media.MediaService>()
        val contentType = io.ktor.http.ContentType.parse(picked.mimeType)
        val info = de.connect2x.trixnity.core.model.events.m.room.FileInfo(
            mimeType = picked.mimeType,
            size = picked.bytes.size.toLong(),
        )
        val encrypted = client.room.getState<EncryptionEventContent>(roomId).firstOrNull() != null
        val content = if (encrypted) {
            RoomMessageEventContent.FileBased.File(
                body = picked.fileName,
                fileName = picked.fileName,
                file = mediaService.prepareUploadEncryptedMedia(picked.bytes.toByteArrayFlow()),
                info = info,
            )
        } else {
            val cacheUri = mediaService.prepareUploadMedia(picked.bytes.toByteArrayFlow(), contentType)
            RoomMessageEventContent.FileBased.File(
                body = picked.fileName,
                fileName = picked.fileName,
                url = mediaService.uploadMedia(cacheUri).getOrThrow(),
                info = info,
            )
        }
        client.room.sendMessage(roomId) { content(content) }
    }

    /**
     * 發送本地圖片（m.image）。加密房先 prepareUploadEncryptedMedia 取得
     * EncryptedFile（事件層再由 outbox 走 megolm）；明文房上傳取 mxc url。
     */
    suspend fun sendImage(roomId: RoomId, image: PickedImage): Result<String> = runCatching {
        val mediaService = client.di.get<de.connect2x.trixnity.client.media.MediaService>()
        val contentType = io.ktor.http.ContentType.parse(image.mimeType)
        val info = ImageInfo(
            mimeType = image.mimeType,
            width = image.width,
            height = image.height,
            size = image.bytes.size.toLong(),
        )
        val encrypted = client.room.getState<EncryptionEventContent>(roomId).firstOrNull() != null
        val content = if (encrypted) {
            val file = mediaService.prepareUploadEncryptedMedia(image.bytes.toByteArrayFlow())
            RoomMessageEventContent.FileBased.Image(
                body = image.fileName,
                file = file,
                info = info,
            )
        } else {
            val cacheUri = mediaService.prepareUploadMedia(image.bytes.toByteArrayFlow(), contentType)
            val mxc = mediaService.uploadMedia(cacheUri).getOrThrow()
            RoomMessageEventContent.FileBased.Image(
                body = image.fileName,
                url = mxc,
                info = info,
            )
        }
        client.room.sendMessage(roomId) {
            content(content)
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
 * 取訊息內容。加密房間的明文在 `TimelineEvent.content`（解密後的 Result），
 * 直接讀 `event.content` 只會拿到 `m.room.encrypted`，訊息會整批消失。
 */
internal fun TimelineEvent.messageBodyOrNull(): MessageBody? {
    val decrypted = content
    if (decrypted != null && decrypted.isFailure) return MessageBody.Undecryptable
    // content 還是 null 表示尚未解密：可能在等金鑰（Trixnity 預設無限期等）。
    // 這種事件不能靜靜丟掉，否則整個加密房間會是一片空白，看不出「這裡有訊息但我沒鑰匙」。
    if (decrypted == null && event.content is EncryptedMessageEventContent) return MessageBody.Undecryptable
    return (decrypted?.getOrNull() ?: event.content).messageBodyOrNull()
}

/**
 * 內容層的映射。outbox（本機回顯）只有 content 沒有 TimelineEvent，兩邊共用這一份，
 * 免得「已送出的訊息」和「還在送的訊息」長得不一樣。
 */
internal fun de.connect2x.trixnity.core.model.events.EventContent.messageBodyOrNull(): MessageBody? =
    when (this) {
        is RoomMessageEventContent.TextBased -> MessageBody.Text(body)
        is RoomMessageEventContent.FileBased.Image ->
            imageBody(body, url, file, info as? ImageInfo, isSticker = false)
        is RoomMessageEventContent.FileBased -> MessageBody.Attachment(fileName ?: body)
        // 自己送出的貼圖是註冊過的 StickerEventContent
        is StickerEventContent -> imageBody(body, url, file, info, isSticker = true)
        // Trixnity 5.8.1 沒有 m.sticker 的內容型別，別人送的貼圖以 UnknownEventContent 帶原始 JSON 進來
        is UnknownEventContent -> if (eventType == "m.sticker") stickerBody(raw) else null
        else -> null
    }

/** 圖片訊息優先用縮圖，省掉整張原圖的流量；加密房的縮圖同樣是 EncryptedFile。 */
private fun imageBody(
    caption: String,
    url: String?,
    file: EncryptedFile?,
    info: ImageInfo?,
    isSticker: Boolean,
): MessageBody {
    val source = info?.thumbnailFile?.let(MediaSource::Encrypted)
        ?: info?.thumbnailUrl?.let(MediaSource::Plain)
        ?: file?.let(MediaSource::Encrypted)
        ?: url?.let(MediaSource::Plain)
        ?: return MessageBody.Attachment(caption)
    return MessageBody.Image(caption, source, info?.width, info?.height, isSticker)
}

/** 貼圖事件的形狀跟 m.image 相同（body/url/file/info），只是型別沒被 Trixnity 註冊。 */
private fun stickerBody(raw: JsonObject): MessageBody? {
    val info = raw["info"]?.let { it as? JsonObject }
    val encryptedFile = raw["file"]?.let { element ->
        runCatching { StickerJson.decodeFromJsonElement<EncryptedFile>(element) }.getOrNull()
    }
    val thumbnailFile = info?.get("thumbnail_file")?.let { element ->
        runCatching { StickerJson.decodeFromJsonElement<EncryptedFile>(element) }.getOrNull()
    }
    val source = thumbnailFile?.let(MediaSource::Encrypted)
        ?: info?.get("thumbnail_url")?.mxcOrNull()?.let(MediaSource::Plain)
        ?: encryptedFile?.let(MediaSource::Encrypted)
        ?: raw["url"]?.mxcOrNull()?.let(MediaSource::Plain)
        ?: return null
    return MessageBody.Image(
        caption = raw["body"]?.let { (it as? JsonPrimitive)?.contentOrNull }.orEmpty(),
        source = source,
        width = info?.get("w")?.let { (it as? JsonPrimitive)?.intOrNull },
        height = info?.get("h")?.let { (it as? JsonPrimitive)?.intOrNull },
        isSticker = true,
    )
}

private fun JsonElement.mxcOrNull(): String? =
    (this as? JsonPrimitive)?.contentOrNull?.takeIf { it.startsWith("mxc://") }

/** 貼圖的 m.file 區塊要自己解；Trixnity 的 Json 實例不對外開放，用寬鬆設定自建一個。 */
private val StickerJson = Json { ignoreUnknownKeys = true }

/**
 * 顯示名可能只由不可見字元組成（Telegram 橋接使用者常見，例如 U+2063 INVISIBLE SEPARATOR），
 * 這種名稱在 UI 上等於空白，應該退回 Matrix ID 的 localpart。
 */
internal fun String?.visibleNameOrNull(): String? {
    if (this == null) return null
    val visible = filterNot { ch ->
        ch.isWhitespace() || ch.code in 0x200B..0x200F || ch.code in 0x2060..0x2064 || ch.code == 0xFEFF
    }
    return if (visible.isEmpty()) null else this
}


/**
 * 收藏在最前、低優先在最後，同組內按最後活動時間遞減——與 Element 的清單分組一致。
 */
private fun List<RoomSummary>.sortedByTagsAndActivity(): List<RoomSummary> =
    sortedWith(
        compareBy<RoomSummary> {
            when {
                "m.favourite" in it.tags -> 0
                "m.lowpriority" in it.tags -> 2
                else -> 1
            }
        }.thenByDescending { it.lastActivity },
    )
private fun List<RoomSummary>.sortedByActivity(): List<RoomSummary> =
    sortedWith(compareByDescending<RoomSummary> { it.lastActivity }.thenBy { it.name.lowercase() })
