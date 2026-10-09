package io.github.capricornus007.nashira.matrix

import de.connect2x.trixnity.client.MatrixClient
import de.connect2x.trixnity.client.room
import de.connect2x.trixnity.client.room.TimelineState
import de.connect2x.trixnity.client.store.RoomUser
import de.connect2x.trixnity.client.store.RoomOutboxMessage
import de.connect2x.trixnity.client.store.TimelineEvent
import de.connect2x.trixnity.core.model.events.m.room.RoomMessageEventContent
import de.connect2x.trixnity.core.model.events.m.RelatesTo
import de.connect2x.trixnity.core.model.EventId
import de.connect2x.trixnity.core.model.RoomId
import de.connect2x.trixnity.core.model.UserId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import de.connect2x.trixnity.core.model.events.m.ReactionEventContent
import de.connect2x.trixnity.core.model.events.MessageEventContent
import io.github.capricornus007.nashira.matrix.MediaSource
import io.github.capricornus007.nashira.matrix.MessageBody
import io.github.capricornus007.nashira.matrix.StickerEventContent

/**
 * 以 Trixnity 有狀態的 `Timeline` 為核心的房間時間線包裝。
 *
 * 為什麼不用 `getLastTimelineEvents`：那條路徑只會沿著 store 的 `previousEventId` 鏈往後走，
 * 而長住的大房間那條鏈幾乎都有缺口（sync 停頓、事件未補檔），走到缺口就停，畫面一片空白。
 * 這裡改用 `Timeline.init`——它會用 sync token 補抓缺口，解密完成會更新內層事件流並重新發射。
 *
 * 產出的頁面順序是「新 → 舊」（索引 0 最新），配 UI 的 reverseLayout。
 * 注意 Trixnity 的 `TimelineState.elements` 是「舊 → 新」（索引越大越新，見其 KDoc
 * "sorted with higher indexes being more recent"），所以這裡必須反轉一次；
 * 忘了反轉就會看到最舊的訊息貼在輸入框上方。
 */
class RoomTimeline(
    private val client: MatrixClient,
    private val roomId: RoomId,
    private val memberMap: Flow<Map<UserId, RoomUser>>,
) {
    private val timeline = client.room.getTimeline { it }

    /** 圖釘清單：訊息選單要顯示「圖釘／取消圖釘」，還要在訊息上標出來 */
    private val pinned = RoomRepository(client).pinnedEvents(roomId)

    private val initState = MutableStateFlow(false)
    /**
     * 成員表的當下值。本機回顯（outbox）那條路徑拿不到 `pageFlow()` combine 進來的
     * members，但自己剛發出去的訊息最該顯示自己的暱稱——寫成 localpart 會讓它看起來
     * 像別人發的（用戶 2026-09-29 點名「為什麼發送的時候要變原來昵稱」）。
     */
    private val latestMembers = MutableStateFlow<Map<UserId, RoomUser>>(emptyMap())
    private val loadingBefore = MutableStateFlow(false)

    suspend fun init(startFrom: EventId) {
        timeline.init(
            roomId = roomId,
            startFrom = startFrom,
            configStart = {
                fetchTimeout = TimelineFetchTimeout
                decryptionTimeout = TimelineDecryptTimeout
            },
            configBefore = { minSize = 1; maxSize = 60 },
        )
        initState.value = true
    }

    /** 把時間線移到搜尋結果所在的事件附近。 */
    suspend fun jumpTo(eventId: EventId) {
        // 跳轉是「以目標事件重建視窗」，顯示視窗跟著收回預設：
        // 不然之前往舊翻撐大的視窗會跟著新視窗一起 materialize 一大票，記憶體白漲。
        windowSize.value = TimelineWindowSize
        timeline.init(
            roomId = roomId,
            startFrom = eventId,
            configStart = {
                fetchTimeout = TimelineFetchTimeout
                decryptionTimeout = TimelineDecryptTimeout
            },
            configBefore = { minSize = 1; maxSize = 60 },
        )
        initState.value = true
    }

    /** 往前（更早）載入一頁歷史；UI 滾到最舊端時呼叫。返回前會等這一頁到位。 */
    suspend fun loadBefore() {
        loadingBefore.value = true
        runCatching { timeline.loadBefore() }
        // ⚠️ 撈到東西就要把「顯示視窗」一起放大，否則翻了等於沒翻：
        // `state.elements` 是**全部已載入**的事件（會隨往前翻無限增長），
        // 而下面 pageFlow 只取最後 `TimelineWindowSize` 條（那是 2026-09 治 OOM 加的）。
        // 兩件事湊起來就是「舊事件進了記憶體、卻永遠被裁在畫面外」——
        // 用戶 2026-10-09 實測：`shown` 死卡在 257、`atOldest` 恆真、載入圈一直閃。
        // 每撈一次放大一段（上限 MaxWindowSize）。`timeline.state` 是 Flow 不是 StateFlow，
        // 在這裡拿不到即時 elements 數量，所以不判斷「有沒有真的變多」：
        // 真的撈到東西時這正是我們要的；撈不到時 `canLoadMore` 會轉 false，呼叫端就停了，
        // 放大本身不會多花記憶體（視窗不會超過實際已載入的事件數）。
        windowSize.value = (windowSize.value + TimelineWindowSize).coerceAtMost(MaxWindowSize)
        loadingBefore.value = false
    }

    /** 目前顯示多少條事件（往舊翻會逐段放大，回到活邊緣就收回預設）。 */
    private val windowSize = MutableStateFlow(TimelineWindowSize)

    /**
     * 跳回活邊緣（最新一則）。深滾歷史後 UI 的「跳到最新」按鈕用——
     * Trixnity 視窗會隨 loadBefore 往舊端滑，滾回來拿不到新端內容
     * （2026-09-14 深滾後回不到底部的實測），直接以最新事件重建視窗。
     */
    suspend fun jumpToLiveEdge() {
        windowSize.value = TimelineWindowSize
        val lastEventId = client.room.getById(roomId).firstOrNull()?.lastRelevantEventId ?: return
        jumpTo(lastEventId)
    }

    /**
     * 活邊緣（P5 修復）：`Timeline` 的視窗是 init 時的靜態快照（internalInit/loadAfter
     * 都是 toList() 收斂），sync 進來的新事件**不會**自動出現在開著的時間線——
     * 真機實證：發送語音後要退出重進房間才看得到，看起來像「一直卡在發送中」。
     * 這裡訂閱 `Room.lastRelevantEventId`（RoomListHandler 在 sync 時維護的活欄位），
     * 變了就 loadAfter 把視窗延伸到最新。已是最新時 loadAfter 是 no-op，重複呼叫無害。
     */
    fun startLiveEdge(scope: kotlinx.coroutines.CoroutineScope) {
        scope.launch {
            client.room.getById(roomId)
                .map { it?.lastRelevantEventId }
                .distinctUntilChanged()
                .collect {
                    runCatching { timeline.loadAfter { minSize = 1; maxSize = 100 } }
                        .onFailure { println("NASHIRA_TIMELINE: live edge loadAfter failed: ${it.message}") }
                }
        }
    }

    /**
     * 收斂成 UI 用的頁流。訊息數 = 目前這一段 timeline 的事件總數，
     * 包括不可顯示的成員／狀態事件——往上還有沒有更多就看它；純文字訊息數
     * 因為過濾而永遠偏小，不能拿來判斷到頭了沒有。
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun pageFlow(): Flow<TimelinePage> = timeline.state
        // 成員表要一起收：大房（幾千人）的 member 事件是延後載入的，先到的是空表。
        // 只在建頁時取一次 memberMap 的當下值，發送者名字會全部退回 localpart，
        // 而且之後成員到了也不會重畫——使用者看到的就是「一部分有名字一部分是 telegram_數字」。
        .combine(memberMap) { state, members -> state to members }
        .combine(pinned) { (state, members), pinnedIds -> Triple(state, members, pinnedIds) }
        .flatMapLatest { (state, members, pinnedIds) ->
            // Timeline state 的元素是「事件流」。解密完成會更新該流並重新發射，
            // timeline.state 也因為事件流換值而重算；這裡在 Flow 的 suspend map 裡等每條流的最新值。
            flow {
                // 只取最新的一段：elements 會隨著往前翻歷史無限增長，而每個元素都是
                // 一條事件流。整份materialize 會把整個房間歷史留在堆積裡——治理房或
                // 幾乎沒有訊息的房間（自動往前翻）實測會撐到 255MB/256MB 然後 OOM。
                val windowed = state.elements.takeLast(windowSize.value)
                latestMembers.value = members
                // 先把每條事件流取到當下值：反應要先掃一遍才知道哪則訊息掛了哪些反應
                val events = windowed.map { eventFlow -> eventFlow.first() }
                val reactions = aggregateReactions(events)
                val edits = aggregateEdits(events)
                emit(
                    TimelinePage(
                        // elements 是舊→新，UI 要新→舊。相簿併組要在「舊→新」上做
                        // （時間差往後加才有意義），所以併完再翻回 UI 要的順序。
                        messages = groupMediaAlbums(
                            events.mapNotNull { event ->
                                toMessage(event, members, reactions, pinnedIds, edits)
                            },
                        ).asReversed(),
                        eventCount = state.elements.size,
                        canLoadMore = state.canLoadBefore,
                        loadingBefore = loadingBefore.value,
                    ),
                )
            }
        }
        // 本機回顯：outbox 裡還沒被伺服器回音確認的訊息貼在最前面（UI 是新→舊）。
        // 少了這一段，點下送出到 sync 回來之前畫面完全沒反應，使用者只能猜有沒有送出去。
        .combine(outboxFlow()) { page, pending ->
            if (pending.isEmpty()) page else page.copy(messages = pending + page.messages)
        }
        .distinctUntilChanged()

    /**
     * outbox 的每一筆是一條流（Trixnity 會就地更新上傳進度／錯誤）。
     * 已經拿到 eventId 的表示伺服器收下了，真正的時間線事件很快就會到，
     * 這裡濾掉避免同一則訊息出現兩次。
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun outboxFlow(): Flow<List<TimelineMessage>> =
        client.room.getOutbox(roomId)
            .flatMapLatest { entries ->
                if (entries.isEmpty()) flowOf(emptyList())
                else combine(entries) { list -> list.mapNotNull { toPendingMessage(it) } }
            }

    private fun toPendingMessage(outbox: RoomOutboxMessage<*>?): TimelineMessage? {
        if (outbox == null) return null
        if (outbox.isDraft || outbox.eventId != null) return null
        val body = outbox.content.messageBodyOrNull()
            ?: when (val content = outbox.content) {
                is StickerEventContent -> MessageBody.Image(
                    caption = content.body,
                    source = content.file?.let(MediaSource::Encrypted)
                        ?: content.url?.let(MediaSource::Plain)
                        ?: return null,
                    width = content.info?.width,
                    height = content.info?.height,
                    isSticker = true,
                    mimeType = content.info?.mimeType,
                )
                else -> null
            }
            ?: return null
        // 自己的暱稱與頭像照樣從成員表取，取不到才退回 localpart：
        // 之前這裡直接寫 localpart，剛發出去的訊息頭上掛的是 ID 而不是暱稱
        val me = client.userId
        val meMember = latestMembers.value[me]
        return TimelineMessage(
            eventId = null,
            roomId = roomId,
            sender = client.userId,
            senderName = meMember?.name.visibleNameOrNull()
                ?: me.full.removePrefix("@").substringBefore(':'),
            senderAvatarUrl = meMember?.event?.content?.avatarUrl,
            outboxTransactionId = outbox.transactionId,
            body = body,
            timestamp = outbox.createdAt.toEpochMilliseconds(),
            pending = true,
            // 原本填的是 it::class.simpleName，等於把「MediaTooLarge」這種術語丟給用戶。
            sendError = outbox.sendError?.let { sendErrorText(it) },
        )
    }

    /**
     * outbox 的失敗類別翻成人話。
     *
     * 走不了 friendlyError：Trixnity 存的不是 Throwable，是
     * `RoomOutboxMessage.SendError` 這個 sealed class（實測型別不符、編譯直接擋）。
     * 每一支都要給得出「該做什麼」，不然顯示了跟沒顯示一樣。
     */
    private fun sendErrorText(e: RoomOutboxMessage.SendError): String = when (e) {
        is RoomOutboxMessage.SendError.MediaTooLarge -> "檔案太大，伺服器拒收"
        is RoomOutboxMessage.SendError.NoMediaPermission -> "這個房間不讓你上傳媒體"
        is RoomOutboxMessage.SendError.NoEventPermission -> "你在這個房間沒有發言權限"
        is RoomOutboxMessage.SendError.EncryptionAlgorithmNotSupported ->
            "這個房間用的加密方式你的裝置不支援"
        is RoomOutboxMessage.SendError.EncryptionError -> "加密失敗，請重新登入或檢查裝置金鑰"
        is RoomOutboxMessage.SendError.BadRequest -> "伺服器拒絕了這則內容"
        is RoomOutboxMessage.SendError.Unknown -> "原因不明，請再試一次"
    }

    /**
     * 把載入視窗內的 `m.reaction` 事件收成「目標事件 → 表情 → 統計」。
     *
     * 反應是獨立事件，只看訊息本身永遠看不到它們；而送出反應卻看不到結果的功能等於沒做。
     * 只統計目前載入的這一段（跟 Element 一樣，往前翻更多歷史才會補上更早的反應）。
     */
    private fun aggregateReactions(events: List<TimelineEvent>): Map<EventId, Map<String, ReactionInfo>> {
        val result = mutableMapOf<EventId, MutableMap<String, ReactionInfo>>()
        events.forEach { event ->
            val content = event.content?.getOrNull() ?: event.event.content
            val reaction = content as? ReactionEventContent ?: return@forEach
            val annotation = reaction.relatesTo ?: return@forEach
            val target = annotation.eventId
            val key = annotation.key ?: return@forEach
            val bucket = result.getOrPut(target) { mutableMapOf() }
            val previous = bucket[key]
            val mine = if (event.event.sender == client.userId) event.event.id else previous?.mine
            bucket[key] = ReactionInfo(count = (previous?.count ?: 0) + 1, mine = mine)
        }
        return result
    }

    /**
     * 把載入視窗內的 `m.replace` 事件收成「被編輯的事件 → 最新內容」。
     * 跟反應同一套思路：只聚合目前載入的這一段；列表是舊→新，後掃到的覆蓋先前的
     * （後到的編輯較新）。編輯事件本身會被 [toMessage] 濾掉，只在原訊息上生效。
     */
    private fun aggregateEdits(events: List<TimelineEvent>): Map<EventId, MessageEventContent> {
        val result = mutableMapOf<EventId, MessageEventContent>()
        events.forEach { event ->
            val content = event.content?.getOrNull() ?: event.event.content
            val replace = (content as? MessageEventContent)?.relatesTo as? RelatesTo.Replace ?: return@forEach
            // 只認文字編輯：newContent 一定是 MessageEventContent，非文字的聚合無從渲染
            val newContent = replace.newContent
            if (newContent is RoomMessageEventContent.TextBased) {
                result[replace.eventId] = newContent
            }
        }
        return result
    }

    private suspend fun toMessage(
        timelineEvent: TimelineEvent,
        members: Map<UserId, RoomUser>,
        reactions: Map<EventId, Map<String, ReactionInfo>>,
        pinnedIds: List<EventId>,
        edits: Map<EventId, MessageEventContent> = emptyMap(),
    ): TimelineMessage? {
        val roomEvent = timelineEvent.event
        // m.replace 編輯事件不單獨顯示：聚合後掛在原訊息上（見 aggregateEdits）
        val eventContent = timelineEvent.content?.getOrNull() ?: roomEvent.content
        if ((eventContent as? MessageEventContent)?.relatesTo is RelatesTo.Replace) return null
        val member = members[roomEvent.sender]
        val edited = edits[roomEvent.id]
        return TimelineMessage(
            eventId = roomEvent.id,
            roomId = roomId,
            sender = roomEvent.sender,
            senderName = member?.name.visibleNameOrNull()
                ?: roomEvent.sender.full.removePrefix("@").substringBefore(':'),
            senderAvatarUrl = member?.event?.content?.avatarUrl,
            // 編輯過的訊息優先用最新內容；聚合到的 newContent 一定是 TextBased
            body = edited?.messageBodyOrNull() ?: timelineEvent.messageBodyOrNull() ?: return null,
            timestamp = roomEvent.originTimestamp,
            edited = edited != null,
            pinned = roomEvent.id in pinnedIds,
            replyToEventId = (eventContent as? MessageEventContent)?.relatesTo?.let { it as? RelatesTo.Reply }?.replyTo?.eventId,
        )
    }
}

/**
 * 等待解密的上限。Trixnity 預設 INFINITE：缺金鑰的事件會讓那條事件流永遠不發，
 * 內層元素一直補不齊。限時之後解密失敗的事件會以失敗的 Result 進來，
 * UI 就能標成「無法解密」而不是整頁空白。
 */
private val TimelineDecryptTimeout: Duration = 4.seconds

/**
 * 一次映射到 UI 的事件上限。往前翻歷史時 Trixnity 的 elements 只增不減，
 * 不設窗就等於把整個房間留在記憶體裡。
 */
private const val TimelineWindowSize = 300

/**
 * 往舊翻最多同時顯示多少條事件。上限的意義是**記憶體**：每條都要 materialize
 * （解密＋解析正文），2026-09 那次治理房 OOM 就是整份歷史被留在堆裡（255MB/256MB）。
 * 2000 ≈ 七頁，夠翻到好幾天以前，又不會變成「無上限」。
 */
private const val MaxWindowSize = 2000

/** 抓缺檔（sync gap）的上限。 */
private val TimelineFetchTimeout: Duration = 30.seconds
