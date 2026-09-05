package io.github.capricornus007.nashira.matrix

import de.connect2x.trixnity.client.MatrixClient
import de.connect2x.trixnity.client.room
import de.connect2x.trixnity.client.room.TimelineState
import de.connect2x.trixnity.client.store.RoomUser
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
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * 以 Trixnity 有狀態的 `Timeline` 為核心的房間時間線包裝。
 *
 * 為什麼不用 `getLastTimelineEvents`：那條路徑只會沿著 store 的 `previousEventId` 鏈往後走，
 * 而長住的大房間那條鏈幾乎都有缺口（sync 停頓、事件未補檔），走到缺口就停，畫面一片空白。
 * 這裡改用 `Timeline.init`——它會用 sync token 補抓缺口，解密完成會更新內層事件流並重新發射。
 *
 * 產出的頁面順序是「新 → 舊」（索引 0 最新），正好配 UI 的 reverseLayout。
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
        .flatMapLatest { state ->
            // Timeline state 的元素是「事件流」。解密完成會更新該流並重新發射，
            // timeline.state 也因為事件流換值而重算；這裡在 Flow 的 suspend map 裡等每條流的最新值。
            flow {
                emit(
                    TimelinePage(
                        messages = state.elements.mapNotNull { eventFlow -> toMessage(eventFlow) },
                        eventCount = state.elements.size,
                        canLoadMore = state.canLoadBefore,
                        loadingBefore = loadingBefore.value,
                    ),
                )
            }
        }
        .distinctUntilChanged()

    private suspend fun toMessage(eventFlow: Flow<TimelineEvent>): TimelineMessage? {
        val members = memberMap.firstOrNull() ?: emptyMap()
        val timelineEvent = eventFlow.first()
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
