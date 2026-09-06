package io.github.capricornus007.nashira.matrix

import de.connect2x.trixnity.client.MatrixClient
import de.connect2x.trixnity.client.room
import de.connect2x.trixnity.client.room.TimelineState
import de.connect2x.trixnity.client.store.RoomUser
import de.connect2x.trixnity.client.store.RoomOutboxMessage
import de.connect2x.trixnity.client.store.TimelineEvent
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import de.connect2x.trixnity.core.model.events.m.ReactionEventContent

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

    private val initState = MutableStateFlow(false)
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

    /** 往前（更早）載入一頁歷史；UI 滾到最舊端時呼叫。返回前會等這一頁到位。 */
    suspend fun loadBefore() {
        loadingBefore.value = true
        runCatching { timeline.loadBefore() }
        loadingBefore.value = false
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
        .flatMapLatest { (state, members) ->
            // Timeline state 的元素是「事件流」。解密完成會更新該流並重新發射，
            // timeline.state 也因為事件流換值而重算；這裡在 Flow 的 suspend map 裡等每條流的最新值。
            flow {
                // 先把每條事件流取到當下值：反應要先掃一遍才知道哪則訊息掛了哪些反應
                val events = state.elements.map { eventFlow -> eventFlow.first() }
                val reactions = aggregateReactions(events)
                emit(
                    TimelinePage(
                        // elements 是舊→新，UI 要新→舊
                        messages = events.asReversed().mapNotNull { event -> toMessage(event, members, reactions) },
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
        val body = outbox.content.messageBodyOrNull() ?: return null
        return TimelineMessage(
            eventId = null,
            roomId = roomId,
            sender = client.userId,
            senderName = client.userId.full.removePrefix("@").substringBefore(':'),
            senderAvatarUrl = null,
            body = body,
            timestamp = outbox.createdAt.toEpochMilliseconds(),
            pending = true,
            sendError = outbox.sendError?.let { it::class.simpleName },
        )
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

    private suspend fun toMessage(
        timelineEvent: TimelineEvent,
        members: Map<UserId, RoomUser>,
        reactions: Map<EventId, Map<String, ReactionInfo>>,
    ): TimelineMessage? {
        val roomEvent = timelineEvent.event
        val member = members[roomEvent.sender]
        return TimelineMessage(
            eventId = roomEvent.id,
            roomId = roomId,
            sender = roomEvent.sender,
            senderName = member?.name.visibleNameOrNull()
                ?: roomEvent.sender.full.removePrefix("@").substringBefore(':'),
            senderAvatarUrl = member?.event?.content?.avatarUrl,
            body = timelineEvent.messageBodyOrNull() ?: return null,
            timestamp = roomEvent.originTimestamp,
            reactions = reactions[roomEvent.id].orEmpty(),
        )
    }
}

/**
 * 等待解密的上限。Trixnity 預設 INFINITE：缺金鑰的事件會讓那條事件流永遠不發，
 * 內層元素一直補不齊。限時之後解密失敗的事件會以失敗的 Result 進來，
 * UI 就能標成「無法解密」而不是整頁空白。
 */
private val TimelineDecryptTimeout: Duration = 4.seconds

/** 抓缺檔（sync gap）的上限。 */
private val TimelineFetchTimeout: Duration = 30.seconds
