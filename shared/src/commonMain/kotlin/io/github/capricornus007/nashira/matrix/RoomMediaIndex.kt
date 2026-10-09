package io.github.capricornus007.nashira.matrix

import de.connect2x.trixnity.client.MatrixClient
import de.connect2x.trixnity.client.room
import de.connect2x.trixnity.client.store.TimelineEvent
import de.connect2x.trixnity.clientserverapi.model.room.GetEvents
import de.connect2x.trixnity.clientserverapi.model.user.Filters
import de.connect2x.trixnity.core.model.EventId
import de.connect2x.trixnity.core.model.RoomId
import de.connect2x.trixnity.core.model.events.ClientEvent.RoomEvent
import de.connect2x.trixnity.core.model.events.EventContent
import de.connect2x.trixnity.core.model.events.m.room.EncryptedMessageEventContent
import de.connect2x.trixnity.core.model.events.m.room.ImageInfo
import de.connect2x.trixnity.core.model.events.m.room.RoomMessageEventContent
import de.connect2x.trixnity.core.model.events.m.room.VideoInfo
import io.github.capricornus007.nashira.i18n.friendlyError
import io.github.capricornus007.nashira.mediaProbe
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Clock
import kotlin.time.TimeSource

/**
 * 房間的媒體索引：全螢幕檢視器要能「不用先把聊天一路翻開」就翻完整間房
 * （用戶 2026-10-09：「電報是直接可以切換聊天內所有圖片的」），
 * 之後的附件面板計數也用同一份資料。
 *
 * 為什麼要自己落盤：Trixnity 的 Room 庫**沒有**「列出某房間全部事件」的 API
 * （`MinimalRepository` 只有 get/save/delete，v5.8.1 實查），掃過的結果不留下來，
 * 每次開 app 就得重掃一次，談不上「秒出」。
 *
 * ⚠️ 索引**刻意不存加密媒體的金鑰**。本機 Room 庫本來就把解密後的內容明文落盤
 * （Trixnity `storeTimelineEventContentUnencrypted` 預設 `true`），再把
 * `EncryptedFile.key` 複製一份到別的檔案，只是多一個外洩點。加密房的條目只記
 * 「哪一號事件」＋本體 mxc（拿來查已解碼的圖），真要開原檔時用
 * [resolveIndexedMedia] 回庫取那份 `EncryptedFile`。
 */
@Serializable
internal data class MediaIndexEntry(
    val eventId: String,
    /** `content.msgtype`（`m.image`／`m.video`）。event type 本身一律是 `m.room.message`。 */
    val msgtype: String,
    val timestamp: Long,
    val sender: String,
    val caption: String = "",
    /** 未加密房：時間線選來顯示的那個 mxc（有縮圖就用縮圖，跟訊息畫面一致） */
    val mxc: String? = null,
    /** 加密房：`EncryptedFile.url`。只有 URL，金鑰不在這裡。 */
    val encryptedMxc: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val durationMs: Long? = null,
    val sizeBytes: Long? = null,
    val mimeType: String? = null,
) {
    /** 這筆要看原檔時得回庫取金鑰。 */
    val needsKey: Boolean get() = mxc == null && encryptedMxc != null

    /** 顯示用的身分鍵：同一張圖在「清單」與「時間線」兩邊算同一個。 */
    val identity: String get() = mxc ?: encryptedMxc ?: eventId

    /** 未加密的可以直接畫；加密的這側沒有金鑰，回 null 讓呼叫端去庫裡補。 */
    fun toMessageBodyOrNull(): MessageBody.Image? = mxc?.let {
        MessageBody.Image(caption, MediaSource.Plain(it), width, height, isSticker = false, mimeType, durationMs, sizeBytes)
    }
}

@Serializable
internal data class MediaIndexMeta(
    /** 已掃到房間最舊一筆：這房的清單算完整，計數才准報數字 */
    val scannedToOldest: Boolean = false,
    /** 已掃到的最舊事件 id，下次從這裡接續 */
    val oldestEventId: String? = null,
    /** 掃過的筆數（讓「還在整理」講得出進度） */
    val scannedEvents: Long = 0,
    val updatedAt: Long = 0,
)

/**
 * 索引檔：一房兩檔——`<房間id>.jsonl`（一行一筆，只追加）＋ `.meta.json`（整檔覆寫）。
 *
 * 追加而非整檔重寫：一次爬取可能新增幾千筆，整檔重寫是 O(n²) 的寫量；
 * JSONL 被關機截斷也只丟最後一行（讀取端逐行容錯），不會讓整份清單跟著消失。
 */
internal class RoomMediaIndexStore(directory: String) {
    private val root = directory.toPath()
    private val fs = FileSystem.SYSTEM
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    // 爬蟲與 UI 都寫同一組檔案，寫要排隊；讀也一起排，避免讀到追加一半的最後一行
    private val lock = Mutex()

    private fun nameOf(roomId: RoomId): String = roomId.full.replace(Regex("[^A-Za-z0-9_.-]"), "_")
    private fun entriesFile(roomId: RoomId) = root / (nameOf(roomId) + ".jsonl")
    private fun metaFile(roomId: RoomId) = root / (nameOf(roomId) + ".meta.json")

    suspend fun load(roomId: RoomId): Pair<MediaIndexMeta, List<MediaIndexEntry>> = lock.withLock {
        val meta = runCatching {
            if (!fs.exists(metaFile(roomId))) MediaIndexMeta()
            else json.decodeFromString<MediaIndexMeta>(fs.read(metaFile(roomId)) { readUtf8() })
        }.getOrDefault(MediaIndexMeta())
        val entries = runCatching {
            if (!fs.exists(entriesFile(roomId))) return@runCatching emptyList()
            fs.read(entriesFile(roomId)) {
                val list = ArrayList<MediaIndexEntry>()
                while (true) {
                    val line = readUtf8Line() ?: break
                    if (line.isBlank()) continue
                    runCatching { json.decodeFromString<MediaIndexEntry>(line) }.getOrNull()?.let { list += it }
                }
                list
            }
        }.getOrDefault(emptyList())
        meta to entries
    }

    suspend fun appendEntries(roomId: RoomId, newEntries: List<MediaIndexEntry>) {
        if (newEntries.isEmpty()) return
        lock.withLock {
            runCatching {
                fs.createDirectories(root)
                // okio 的 Sink 在 common 這側不是 java.io.Closeable，套不下 use{}，
                // 就手動 flush/close：寫到一半斷掉只會毀掉最後一行，讀取端逐行容錯
                val sink = fs.appendingSink(entriesFile(roomId)).buffer()
                try {
                    newEntries.forEach { entry ->
                        sink.writeUtf8(json.encodeToString(MediaIndexEntry.serializer(), entry))
                        sink.writeUtf8("\n")
                    }
                    sink.flush()
                } finally {
                    sink.close()
                }
            }
        }
    }

    suspend fun saveMeta(roomId: RoomId, meta: MediaIndexMeta) = lock.withLock {
        runCatching {
            fs.createDirectories(root)
            fs.write(metaFile(roomId)) { writeUtf8(json.encodeToString(MediaIndexMeta.serializer(), meta)) }
        }
    }

    /** 登出／換帳號：這房的索引一起清掉，別留著上一個帳號的清單。 */
    suspend fun clear(roomId: RoomId) = lock.withLock {
        listOf(entriesFile(roomId), metaFile(roomId)).forEach { path ->
            runCatching { if (fs.exists(path)) fs.delete(path) }
        }
    }
}

/** 爬一輪的結果。[error] 只進診斷日誌，這一輪不拿它當介面文字。 */
internal class MediaCrawlOutcome(
    val added: Int,
    val reachedOldest: Boolean,
    val scannedEvents: Long,
    val error: String? = null,
)

/** 事件數／時間任一到頂就這輪先收工，下次從中斷點接續（大房間一次掃不完不是錯誤）。 */
private const val MediaCrawlEventBudget = 20_000L
private const val MediaCrawlMillisBudget = 240_000L

/** 未加密房一頁 1000 條（Synapse 的 `MAX_LIMIT` 就是 1000）。 */
private const val MediaCrawlPageLimit = 1000L

/** 累積多少筆落盤一次（避免一筆 open/write/close 一次）。 */
private const val MediaIndexFlushEvery = 100

/** 加密房單筆等解密的時間，與「連續幾筆都等不到就不等」的門檻。 */
private const val MediaDecryptWaitMillis = 900L
private const val MediaDecryptGiveUpStreak = 3

/** 加密房開跑前先讓首屏安定多久。 */
private const val MediaCrawlEncryptedWarmupMillis = 2500L

private class MediaCrawlBudgetReached : Throwable()

/** 爬蟲停下来的原因。**「流結束」跟「掃到最舊」是兩件事**，混用會讓清單永久停在半截。 */
private enum class CrawlStop { ReachedStart, Stopped }

/**
 * 掃這一間房的媒體。**未加密房**走裸 `/messages`（一次 1000 條，伺服端先把不帶
 * 媒體的濾掉）；**加密房**走 Trixnity 的 `getTimelineEvents`——要它幫忙解密、
 * 順帶補 gap 並把事件寫進庫，之後開原檔才要得到金鑰。
 */
internal suspend fun crawlRoomMedia(
    client: MatrixClient,
    roomId: RoomId,
    store: RoomMediaIndexStore,
    onBatch: suspend (List<MediaIndexEntry>) -> Unit = {},
): MediaCrawlOutcome {
    val room = client.room.getById(roomId).firstOrNull()
        ?: return MediaCrawlOutcome(0, false, 0, "找不到這個房間")
    val (meta, existing) = store.load(roomId)
    if (meta.scannedToOldest) return MediaCrawlOutcome(0, true, meta.scannedEvents)
    val anchor = meta.oldestEventId?.takeIf { EventId.isValid(it) }?.let { EventId(it) }
        ?: room.lastEventId
        ?: return MediaCrawlOutcome(0, false, 0, null)

    val known = existing.mapTo(mutableSetOf()) { it.eventId }
    // 加密房是一筆一筆走（要解密、要寫庫），會跟首屏搶硬碟與連線，让它先安定一下；
    // 未加密房一次 1000 條媒體、幾個請求就完，等那麼久只會讓計數慢下來
    //（用戶 2026-10-09 拿 Element 的「第 6159 張照片，共 6159 張」點名：
    // 「一開始就直接加載出來全部數目」）
    if (room.encrypted) delay(MediaCrawlEncryptedWarmupMillis)
    val started = TimeSource.Monotonic.markNow()
    val pending = ArrayList<MediaIndexEntry>()
    var scanned = 0L
    var added = 0
    var oldestSeen: String? = null
    var reachedOldest = false
    var failure: String? = null

    val handle: suspend (MediaIndexItem) -> Unit = { item ->
        scanned++
        if (scanned > MediaCrawlEventBudget || started.elapsedNow().inWholeMilliseconds > MediaCrawlMillisBudget) {
            throw MediaCrawlBudgetReached()
        }
        oldestSeen = item.eventId
        val entry = mediaIndexEntryOf(item)
        if (entry != null && known.add(entry.eventId)) {
            pending += entry
            added++
            if (pending.size >= MediaIndexFlushEvery) {
                store.appendEntries(roomId, pending)
                onBatch(pending.toList())
                pending.clear()
            }
        }
    }
    var cancelled = false
    try {
        // 「這條流結束了」不等於「這個房間掃到頭了」：抓包失敗、讀到補不起來的缺口時
        // Trixnity 也會提前收尾。誤記成掃完，這房的清單會永久停在半截而且再也不補
        reachedOldest = (if (room.encrypted) {
            crawlEncryptedRoom(client, roomId, anchor, handle)
        } else {
            crawlPlainRoom(client, roomId, anchor, handle)
        }) == CrawlStop.ReachedStart
    } catch (_: MediaCrawlBudgetReached) {
        // 預算用盡：存下中斷點，下次接續（scannedToOldest 保持 false）
    } catch (_: CancellationException) {
        // 他關掉這一間房、或離開聊天室：中斷點要先落盤，再把取消往上傳
        cancelled = true
    } catch (e: Exception) {
        failure = friendlyError(e)
    }
    // 落盤要在 NonCancellable 裡做：被取消的協程連 Mutex.lock() 都會立刻拋取消，
    // 那樣掃過的位置就白掃了（下次開房間從頭再來一遍）
    withContext(NonCancellable) {
        if (pending.isNotEmpty()) store.appendEntries(roomId, pending)
        store.saveMeta(
            roomId,
            meta.copy(
                scannedToOldest = reachedOldest,
                oldestEventId = oldestSeen ?: meta.oldestEventId,
                scannedEvents = meta.scannedEvents + scanned,
                updatedAt = Clock.System.now().toEpochMilliseconds(),
            ),
        )
    }
    // 正常結束才把最後不滿一批的條目推給 UI（取消時這一間房早就離開畫面了）
    if (!cancelled && pending.isNotEmpty()) onBatch(pending.toList())
    if (cancelled) throw CancellationException()
    return MediaCrawlOutcome(added, reachedOldest, scanned, failure)
}

/** 爬蟲中間層：兩條路（裸事件／已解密事件流）都收斂成同一個形狀再抽媒體。 */
internal class MediaIndexItem(
    val eventId: String,
    val sender: String,
    val timestamp: Long,
    val content: EventContent?,
)

private fun RoomEvent<*>.toIndexItem(): MediaIndexItem =
    MediaIndexItem(id.full, sender.full, originTimestamp, content)

private fun TimelineEvent.toIndexItem(): MediaIndexItem? {
    val decrypted = content
    // 解不开的（這裝置沒鑰匙的老訊息）不進索引：否則清單上會排出一串點開是空白、
    // 時間線上也寫著「無法解密」的洞
    if (decrypted != null && decrypted.isFailure) return null
    if (decrypted == null && event.content is EncryptedMessageEventContent) return null
    return MediaIndexItem(event.id.full, event.sender.full, event.originTimestamp, decrypted?.getOrNull() ?: event.content)
}

/** 把一則訊息抽成索引條目；不是圖片／影片就回 null。 */
internal fun mediaIndexEntryOf(item: MediaIndexItem): MediaIndexEntry? {
    val message = item.content as? RoomMessageEventContent ?: return null
    if (message !is RoomMessageEventContent.FileBased) return null
    // 只收圖片與影片：m.file／m.audio 不進連翻清單（附件面板那側再分類）
    if (message.type != "m.image" && message.type != "m.video") return null
    // 沿用時間線那套抽法（縮圖優先、mimeType 跟著縮圖修正），不在索引這側重寫一次
    val body = message.messageBodyOrNull() as? MessageBody.Image ?: return null
    val info = message.info
    val plain = body.source as? MediaSource.Plain
    val encrypted = (body.source as? MediaSource.Encrypted)?.file
    if (plain == null && encrypted == null) return null
    return MediaIndexEntry(
        eventId = item.eventId,
        msgtype = message.type,
        timestamp = item.timestamp,
        sender = item.sender,
        caption = body.caption,
        mxc = plain?.mxcUrl,
        encryptedMxc = encrypted?.url,
        width = body.width ?: (info as? ImageInfo)?.width ?: (info as? VideoInfo)?.width,
        height = body.height ?: (info as? ImageInfo)?.height ?: (info as? VideoInfo)?.height,
        durationMs = body.durationMs ?: (info as? VideoInfo)?.duration,
        sizeBytes = body.sizeBytes ?: info?.size,
        mimeType = body.mimeType ?: info?.mimeType,
    )
}

/**
 * 未加密房：裸 `/messages` 往舊走。`contains_url` 請伺服端先把純文字訊息濾掉，
 * 於是一頁是 1000 **條媒體**而不是 1000 條訊息。
 */
private suspend fun crawlPlainRoom(
    client: MatrixClient,
    roomId: RoomId,
    anchor: EventId,
    onEvent: suspend (MediaIndexItem) -> Unit,
): CrawlStop {
    // 只標 contains_url、不標 types：媒體訊息的 event type 是 `m.room.message`，
    // `m.image` 只是 content.msgtype——把 types 寫成 m.image 會掃到空（實查 spec 與 Trixnity 的 mapping）
    val filterJson = client.api.json.encodeToString(
        Filters.RoomFilter.RoomEventFilter(containsUrl = true),
    )
    // `/messages` 認的是 token，不是 event id：用 `/context` 換錨點 token，
    // 順帶給的那 100 條先吃掉
    // 三個停下來的位置都要寫日誌、連伺服端給的原因：實測有一房掃到 48 條就停
    //（到頭=false）而日誌一個字都沒有，等於叫用戶幫我們猜。
    val context = client.api.room.getEventContext(roomId, anchor, filter = filterJson, limit = 100)
        .onFailure { mediaProbe("媒體索引 ${roomId.full} /context 失敗：${it.message} → 這輪不掃") }
        .getOrNull() ?: return CrawlStop.Stopped
    context.eventsBefore.orEmpty().forEach { onEvent(it.toIndexItem()) }
    val start = context.start
    if (start == null) {
        mediaProbe("媒體索引 ${roomId.full} /context 沒給 start token → 這輪不掃")
        return CrawlStop.Stopped
    }
    var token: String = start
    var previousToken: String? = null
    while (true) {
        currentCoroutineContext().ensureActive()
        val page = client.api.room
            .getEvents(roomId, from = token, dir = GetEvents.Direction.BACKWARDS, limit = MediaCrawlPageLimit, filter = filterJson)
            .onFailure { mediaProbe("媒體索引 ${roomId.full} /messages 失敗：${it.message} → 停在 $token") }
            .getOrNull() ?: return CrawlStop.Stopped
        page.chunk.orEmpty().forEach { onEvent(it.toIndexItem()) }
        val next = page.end ?: return CrawlStop.ReachedStart
        // 到頭時伺服端會回同一個 token（或直接 null），兩種都要停，否則原地轉圈
        if (next == token || next == previousToken) return CrawlStop.ReachedStart
        previousToken = token
        token = next
    }
}

/**
 * 加密房：用 Trixnity 的向後走訪。它一次給「一筆事件的一條流」，而這條流是 store 的
 * 觀察流、不保證會自己結束，所以只能用 `drop(1).first()` 等「下一次變化」，
 * 等不到就用手上這份（這裝置沒鑰匙的老訊息，不是這一輪能解決的事）。
 */
private suspend fun crawlEncryptedRoom(
    client: MatrixClient,
    roomId: RoomId,
    anchor: EventId,
    onEvent: suspend (MediaIndexItem) -> Unit,
): CrawlStop {
    var misses = 0
    var stopWaiting = false
    var lastEvent: TimelineEvent? = null
    client.room
        .getTimelineEvents(
            roomId = roomId,
            startFrom = anchor,
            direction = GetEvents.Direction.BACKWARDS,
            config = {
                // 預設 INFINITE：缺金鑰的老事件會讓整輪爬蟲永久卡在那一筆
                decryptionTimeout = (MediaDecryptWaitMillis * 4).milliseconds
                fetchTimeout = 20.seconds
                fetchSize = 200
                // minSize／maxSize 刻意不設：要一口氣走到房間開頭才算掃完
            },
        )
        .collect { eventFlow ->
            val settled = settleDecryption(eventFlow, wait = !stopWaiting)
            if (settled != null) lastEvent = settled
            val item = settled?.toIndexItem()
            if (item == null) {
                // 連續好幾筆都等不到：這房的鑰匙明顯不在這裝置上，
                // 別再一筆等 0.9 秒（10k 條就是兩小時），後面直接取當下值
                if (++misses >= MediaDecryptGiveUpStreak && !stopWaiting) {
                    mediaProbe("媒體索引 ${roomId.full} 連續 $misses 條等不到金鑰 → 之後不再等（這裝置沒這段的鑰匙）")
                    stopWaiting = true
                }
            } else {
                misses = 0
                onEvent(item)
            }
        }
    // 真的走到房間開頭，最後一筆就不會有「前一筆」；
    // 缺口補不起來而提前收尾的那種，previousEventId 還在，算 Stopped
    return if (lastEvent?.previousEventId == null) CrawlStop.ReachedStart else CrawlStop.Stopped
}

private suspend fun settleDecryption(
    eventFlow: kotlinx.coroutines.flow.Flow<TimelineEvent>,
    wait: Boolean,
): TimelineEvent? {
    val first = withTimeoutOrNull((MediaDecryptWaitMillis * 4).milliseconds) { eventFlow.firstOrNull() }
        ?: return null
    if (!wait) return first
    if (first.content != null || first.event.content !is EncryptedMessageEventContent) return first
    repeat(3) {
        val next = withTimeoutOrNull(MediaDecryptWaitMillis.milliseconds) { eventFlow.drop(1).first() }
            ?: return first
        if (next.content != null) return next
    }
    return first
}

/**
 * 加密條目回庫取本體（金鑰只在庫裡）。拿不到就回 null——清單那側顯示佔位，
 * 不謊報成功。
 *
 * `getTimelineEvent` 在「本機沒有這一筆」時會從最新端一路往回爬來找它，
 * 對上萬條的清單是災難，所以 fetchTimeout 調小：找不到就算找不到。
 */
internal suspend fun resolveIndexedMedia(
    client: MatrixClient,
    roomId: RoomId,
    eventId: String,
): MessageBody.Image? {
    if (!EventId.isValid(eventId)) return null
    return withTimeoutOrNull(6.seconds) {
        val event = client.room
            .getTimelineEvent(roomId, EventId(eventId)) {
                decryptionTimeout = 4.seconds
                fetchTimeout = 1500.milliseconds
                fetchSize = 20
            }
            .firstOrNull()
            ?: return@withTimeoutOrNull null
        event.messageBodyOrNull() as? MessageBody.Image
    }
}

/**
 * 索引檔的位置：跟媒體快取、Room 庫同一個帳號目錄。鍵的算法與
 * `MatrixSession.databaseKey` 一致（`<家伺服器 host>-<帳號 localpart>`）。
 */
internal fun mediaIndexDirectoryFor(client: MatrixClient): String =
    mediaIndexDirectory("${client.baseUrl.host}-${client.userId.full.removePrefix("@").substringBefore(':')}")
