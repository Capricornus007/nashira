package io.github.capricornus007.nashira

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import io.github.capricornus007.nashira.i18n.Strings
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import de.connect2x.trixnity.client.MatrixClient
import de.connect2x.trixnity.client.media.MediaService
import de.connect2x.trixnity.utils.toByteArray
import io.github.capricornus007.nashira.matrix.MediaSource
import io.github.capricornus007.nashira.matrix.MessageBody
import kotlin.time.Clock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 全螢幕檢視器的一組媒體＋目前位置。相簿傳整組，單張傳只有一筆的組，
 * 這樣檢視器只有一條路徑（都能連翻、都有計數），不必分兩種狀態。
 *
 * 連翻的範圍是**整個房間**（格子來自本機媒體索引＋目前載入的時間線），
 * 所以清單裡可能出現「還沒補到金鑰」的格子，見 [MediaSlot.body]。
 */
data class MediaViewer(
    val slots: List<MediaSlot>,
    val index: Int,
)

/**
 * 連翻清單裡的一格。[body] 為 null 是刻意支援的狀態：這一格還在等本機庫把
 * `EncryptedFile`（含金鑰）補回來——索引那份副本**不存金鑰**，金鑰只留在
 * Room 庫裡，免得同一把鑰匙躺兩個檔案。補到之前顯示佔位，不謊報「開好了」，
 * 也不給按下載（沒金鑰根本存不出檔）。
 */
data class MediaSlot(
    val eventId: String,
    /** mxc（未加密）或 `EncryptedFile.url`：同一張圖在清單與時間線兩邊算同一個 */
    val identity: String,
    val timestamp: Long,
    val caption: String,
    /** 左下角第二行要顯示的發送者（顯示名，取不到才退回 ID 的 localpart） */
    val sender: String,
    val body: MessageBody.Image?,
)

/**
 * 檢視器做不了、必須交回上層的兩件事：
 * 「在聊天中顯示」要關全螢幕並跳時間線，「轉傳」要開房間選單——兩樣都只有聊天頁有。
 */
class MediaViewerActions(
    val showInChat: (String) -> Unit,
    val forward: (String) -> Unit,
    /** 開附件面板（整個房間的媒體格子）。用戶 2026-10-09：「那你倒是先去做啊？」 */
    val viewAll: () -> Unit,
)

/** 剛送出、索引還沒掃到的那一格：用它反查時間線裡那則訊息，才拿得到發送者顯示名。 */
private fun io.github.capricornus007.nashira.matrix.MessageBody.mentionsMedia(media: MessageBody.Image): Boolean =
    when (this) {
        is MessageBody.Image -> this == media
        is MessageBody.Album -> media in items
        else -> false
    }

/** 媒體身分鍵，與 `MediaIndexEntry.identity` 同一套算法（兩邊要能認出同一張圖）。 */
private fun MediaSource.identity(): String = when (this) {
    is MediaSource.Plain -> mxcUrl
    is MediaSource.Encrypted -> file.url
}

/**
 * 照 Telegram 把連翻範圍放大到**整個聊天室的媒體序列**。
 * tdesktop 的全螢幕檢視器翻的不是「這一組」，而是那份聊天媒體切片：
 * `overlay_widget.cpp:3002-3064 sharedMediaType()` 把照片／影片歸成一類，
 * `_index`（`:685`）是那份切片裡的序號，`moveToNext`（`:5905-5913`）就是序號加減一。
 * 所以從相簿裡點開一張，左右鍵能一路翻到更早其他訊息裡的圖。
 *
 * [roomIndex] 是本機媒體索引（整個房間掃過的結果，含還沒補到金鑰的）；
 * [loaded] 是時間線目前載入的那一段（**新→舊**），這裡翻成舊→新再攤平。
 * 兩邊靠 mxc 認同一張圖：時間線那側手上有完整金鑰，會蓋掉索引裡那個空格。
 * 找不到起點就退回原本傳進來的那一組——寧可少翻，不要翻錯。
 */
internal fun mediaStripFrom(
    loaded: List<io.github.capricornus007.nashira.matrix.TimelineMessage>,
    roomIndex: List<io.github.capricornus007.nashira.matrix.MediaIndexEntry>,
    media: List<MessageBody.Image>,
    at: Int,
): MediaViewer {
    val merged = mergedMediaSlots(loaded, roomIndex)
    val strip = merged.values.sortedBy { it.timestamp }
    val anchor = media.getOrNull(at) ?: return MediaViewer(strip, strip.indices.lastOrNull() ?: 0)
    val found = strip.indexOfFirst { it.identity == anchor.source.identity() }
    if (found >= 0) return MediaViewer(strip, found)
    // 起點不在清單上（剛送出、索引還沒掃到它）：就地補一格，別讓檢視器變成空的
    val slot = MediaSlot(
        eventId = anchor.source.identity(),
        identity = anchor.source.identity(),
        timestamp = Clock.System.now().toEpochMilliseconds(),
        caption = anchor.caption,
        sender = loaded.firstOrNull { it.body.mentionsMedia(anchor) }?.senderName.orEmpty(),
        body = anchor,
    )
    val withAnchor = (strip + slot).sortedBy { it.timestamp }
    return MediaViewer(withAnchor, withAnchor.indexOf(slot).coerceAtLeast(0))
}

/**
 * 「整個房間的媒體」＝本機索引 ＋ 畫面這一段，靠 mxc 認同一張圖。
 * 時間線那側手上有完整金鑰，會蓋掉索引裡那個等金鑰的空格。
 *
 * 抽出來是因為「檢視所有照片」的格子清單與檢視器的連翻清單**必須同一份**：
 * 在清單裡點第 40 格，開起來就要停在第 40 張，兩邊各排一次就會錯位。
 */
internal fun mergedMediaSlots(
    loaded: List<io.github.capricornus007.nashira.matrix.TimelineMessage>,
    roomIndex: List<io.github.capricornus007.nashira.matrix.MediaIndexEntry>,
): LinkedHashMap<String, MediaSlot> {
    val merged = LinkedHashMap<String, MediaSlot>()
    roomIndex.forEach { entry ->
        if (entry.msgtype != "m.image" && entry.msgtype != "m.video") return@forEach
        merged[entry.identity] = MediaSlot(
            eventId = entry.eventId,
            identity = entry.identity,
            timestamp = entry.timestamp,
            caption = entry.caption,
            // 索引只存 Matrix ID；顯示名要等這一則進到時間線才補得起來
            sender = entry.sender.removePrefix("@").substringBefore(":"),
            body = entry.toMessageBodyOrNull(),
        )
    }
    loaded.asReversed().forEach { message ->
        val images = when (val body = message.body) {
            is MessageBody.Image -> if (body.isSticker) emptyList() else listOf(body)
            is MessageBody.Album -> body.items.filterNot { it.isSticker }
            else -> emptyList()
        }
        val eventId = message.eventId?.full.orEmpty()
        images.forEachIndexed { position, image ->
            val identity = image.source.identity()
            merged[identity] = MediaSlot(
                // 索引拿不到事件 id 的舊訊息（多半是本地回顯）就拿身分鍵頂著，之後自然被蓋掉
                eventId = eventId.ifEmpty { identity },
                identity = identity,
                // 相簿那幾張同一則訊息、同一個時間戳；排完序自然相鄰
                timestamp = message.timestamp + position,
                caption = image.caption,
                sender = message.senderName,
                body = image,
            )
        }
    }
    return merged
}


/**
 * 全螢幕圖片檢視器（Discord／Element 式）：
 * 雙指縮放＋平移、雙擊在 1x／2.5x 間切換、點背景關閉、右上角下載。
 * 原圖走 getMedia（時間線用的是縮圖），下載也用同一份位元組。
 *
 * [slots] 大於一筆時就是連翻清單（整個房間的媒體）：左右兩側給連翻鈕、
 * 方向鍵也能翻，從時間線點的那一格開始（[index]）——照 Telegram／Element 的燈光箱。
 * 其中某些格子的本體還沒補到金鑰（[MediaSlot.body] 是 null），那時只轉圈佔位，
 * 不顯示下載鈕（沒金鑰存不出檔）。
 */
@Composable
fun ImageViewer(
    client: MatrixClient,
    slots: List<MediaSlot>,
    index: Int,
    onSelectIndex: (Int) -> Unit,
    /** 選單文字與提示都用呼叫端的語系，不在這裡硬寫字串。 */
    strings: Strings,
    /** 「在聊天中顯示」與「轉傳」要動時間線，檢視器自己碰不到，只能交回上層。 */
    actions: MediaViewerActions,
    onDismiss: () -> Unit,
) {
    val slot = slots[index.coerceIn(slots.indices)]
    // null ＝這一格的金鑰還在從本機庫補回來（索引那副本不存金鑰）
    val current = slot.body
    val source = current?.source
    val caption = current?.caption ?: slot.caption
    val mimeType = current?.mimeType
    val fileName = caption.ifBlank { "nashira-media" }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = false),
    ) {
        // 金鑰補到位時 identity 不變（同一張圖同一個 mxc），所以這個鍵不會在補好的那一刻
        // 改變，畫面不會整塊重建、不會跳回轉圈
        val key = source?.cacheKeyShared() ?: ("full:" + slot.identity)
        val isVideo = mimeType?.startsWith("video/") == true
        // 時間線那側早就把這張圖（圖片是縮圖、影片是首格）解好放在快取裡了，
        // 鍵就是 mxc／加密檔的 url。開檢視器時先拿它墊上，才不會變成空白轉圈：
        // 0.1.76 拿掉「開啟時預抓整檔」之後，影片開啟有約 5 秒什麼都沒有
        //（用戶 2026-10-08「依舊沒秒開」）——而那 5 秒要的畫面其實早就在手上了。
        val timelineKey = slot.identity
        var bitmap by remember(key) {
            mutableStateOf<ImageBitmap?>(MediaBitmapCache.get(key) ?: MediaBitmapCache.get(timelineKey))
        }
        var failed by remember(key) { mutableStateOf(false) }
        // 下載用原檔位元組（影片存 webm 原樣、圖片存原解析度），不重編碼
        var fileBytes by remember(key) { mutableStateOf<ByteArray?>(null) }
        LaunchedEffect(client, key, source) {
            if (bitmap != null) return@LaunchedEffect
            // 這一格連金鑰都還沒有：等 resolveIndexedMedia 補到 source，
            // 按鍵參數會重新觸發這個 Effect，這裡先空著等
            if (source == null) return@LaunchedEffect
            // 影片**不預抓整份檔**：實測那條 9.69MiB 的檔案會跟內嵌播放的串流搶同一條管子，
            // 結果兩邊都慢到像壞掉（用戶 2026-10-08「加載死慢」）。
            // 第一格由播放器自己解出來，「下載」鈕按下去時才抓（見下面那個按鈕）。
            if (isVideo) return@LaunchedEffect
            runCatching {
                val service = client.di.get<MediaService>()
                val media = when (source) {
                    is MediaSource.Plain -> service.getMedia(source.mxcUrl, maxSize = 32L * 1024 * 1024)
                    is MediaSource.Encrypted -> service.getEncryptedMedia(source.file, maxSize = 32L * 1024 * 1024)
                }.getOrThrow()
                val bytes = media.toByteArray(this) ?: return@runCatching
                // 0 位元組在實測裡真的出現過（家伺服器對某些遠端媒體回了個空 body），
                // 當成「抓到檔」會把下載鈕與內嵌播放的退路一起騙過去。
                fileBytes = bytes.takeIf { it.isNotEmpty() }
                // 影片：首格只當「內嵌播放器還沒解出第一格時墊的圖」；下載仍存原檔
                val decoded = if (isVideo) {
                    decodeVideoFrame(bytes, maxDimension = 2048)
                } else {
                    decodeImageBitmap(bytes, maxDimension = 2048)
                }
                if (decoded != null) {
                    MediaBitmapCache.put(key, decoded)
                    bitmap = decoded
                } else {
                    failed = true
                }
            }.onFailure { failed = true }
        }

        var scale by remember(key) { mutableFloatStateOf(1f) }
        var offset by remember(key) { mutableStateOf(Offset.Zero) }
        // 方向鍵連翻。桌面端 Dialog 裡「沒有東西持有焦點」時鍵盤事件根本不進組合，
        // 所以一定要主動把焦點要過來（上一輪我以「搶不到焦點」為由沒做這事，是用戶
        // 2026-10-08 兩句「快捷鍵左右切換根本没反应」「圖片裡也沒辦法左右切換」逼出來的）。
        // 事件掛在**根 Box** 上（onPreviewKeyEvent 是預覽、先於子節點），
        // 這樣焦點在關閉鈕／下載鈕上時方向鍵也照樣翻。
        val pagerFocus = remember { FocusRequester() }
        // 外圍那圈按鈕（左右箭頭、計數、下載＋關閉）**不該常顯**：
        // Telegram 是全靠容器收事件、控件浮現一陣子後自己收掉。用戶 2026-10-08 兩句
        // 「問題是周圍的這些按鈕並不會常顯啊？」＋「抱歉我是說並不應該常顯」。
        // 做法：任何滑鼠移動就把它們叫出來，靜靜不動 2.5 秒再收掉。
        var chromeVisible by remember { mutableStateOf(true) }
        var chromeTick by remember { mutableIntStateOf(0) }
        // Space／Enter 播放在這層分派（tdesktop 也是把按鍵收在容器：
        // `media_view_overlay_widget.cpp:5584-5591`），播放器只認那個計數器。
        var playToggleTick by remember { mutableIntStateOf(0) }
        LaunchedEffect(chromeTick) {
            if (chromeTick > 0) {
                delay(2500)
                chromeVisible = false
            }
        }
        // 「啪一聲消失」很死板（用戶 2026-10-09：「消失的好乾燥…有點死板」）：
        // 用透明度漸變，淡出期間仍留在畫面上，所以不影響點擊命中。
        val chromeAlpha by animateFloatAsState(if (chromeVisible) 1f else 0f, tween(220), label = "chrome")
        val chromeShown = chromeAlpha > 0.01f
        // 每一次換格都要重新要焦點：從影片切回圖片時，子樹整個換掉（內嵌播放器那層消失了），
        // 原本持焦的節點跟著銷毀，焦點就掉到沒有物件——用戶 2026-10-08 實測
        // 「從視頻切換到圖片就切換不回去了」就是這個。只請求一次（Unit）撐不過換格。
        LaunchedEffect(index, slots.size) {
            if (slots.size > 1) runCatching { pagerFocus.requestFocus() }
        }
        val transformState = rememberTransformableState { zoomChange, panChange, _ ->
            scale = (scale * zoomChange).coerceIn(1f, 6f)
            offset = if (scale > 1f) offset + panChange else Offset.Zero
        }
        val snackbar = remember { SnackbarHostState() }
        val saver = rememberImageSaver()
        val downloader = rememberMediaDownloader()
        val scope = rememberCoroutineScope()
        // 外圍圖示的顏色固定白：上一版這裡擺了一顆「換底色」（照 Element 的刷子），
        // 用戶 2026-10-09 兩句點名：「爲什麼照片查看要弄甚麼日夜切換按鈕？」＋
        // 「这他妈是編輯頁面好嗎」——那顆刷子在 Telegram 是**編輯**頁的入口，
        // 不是換主題。我們沒有編輯功能，擺一顆「看照片時切換晝夜」的钮等於自己發明。
        val chromeTint = Color.White
        val chromePill = Color.Black.copy(alpha = 0.55f)
        // 旋轉是純顯示（不改檔案本身），換圖就要回到正，別讓上一張的 90 度跟過來
        var rotation by remember(key) { mutableFloatStateOf(0f) }
        var menuOpen by remember { mutableStateOf(false) }
        // 存檔檔名：caption 通常就是原始檔名；沒有才自己起名（下載與另存為共用這一份）
        val saveExt = when {
            mimeType?.contains("webp") == true -> "webp"
            mimeType?.contains("jpeg") == true || mimeType?.contains("jpg") == true -> "jpg"
            mimeType?.contains("webm") == true -> "webm"
            else -> "png"
        }
        val saveName = if (caption.isBlank()) "nashira-media.$saveExt" else caption
        // 三顆要位元組的動作（下載／另存／複製）共用這條：已經在手上就用手上的，
        // 否則現點現抓——影片從不預抓（會跟串流搶管子，用戶 2026-10-08「加載死慢」）
        val grabBytes: suspend () -> ByteArray? = {
            fileBytes ?: source?.let { fetchMediaBytesForPlayback(client, it) }
        }

        Box(
            Modifier
                .fillMaxSize()
                .focusable()
                .focusRequester(pagerFocus)
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                    when (event.key) {
                        // Space／Enter：播放在影片上＝播放或暫停（播完再按就是重放）。
                        // 非影片時**不吃這個鍵**，留給以後可能出現的輸入。
                        Key.Spacebar, Key.Enter -> if (isVideo) {
                            playToggleTick++
                            chromeVisible = true
                            chromeTick++
                            true
                        } else false
                        else -> {
                            if (slots.size <= 1) return@onPreviewKeyEvent false
                            val at = index.coerceIn(slots.indices)
                            when (event.key) {
                                Key.DirectionLeft -> if (at > 0) onSelectIndex(at - 1)
                                Key.DirectionRight -> if (at < slots.lastIndex) onSelectIndex(at + 1)
                                else -> return@onPreviewKeyEvent false
                            }
                            true
                        }
                    }
                }
                .background(Color.Black.copy(alpha = 0.97f))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onDismiss() },
                        onDoubleTap = {
                            if (scale > 1f) {
                                scale = 1f; offset = Offset.Zero
                            } else {
                                scale = 2.5f
                            }
                        },
                    )
                }
                .pointerInput(Unit) {
                    // 滑鼠一动就把外圍控件叫出來（Telegram 的浮層就是這樣回來的）
                    awaitPointerEventScope {
                        while (true) {
                            val e = awaitPointerEvent(PointerEventPass.Initial)
                            if (e.type == PointerEventType.Move || e.type == PointerEventType.Exit) {
                                if (!chromeVisible) chromeVisible = true
                                chromeTick++
                            }
                        }
                    }
                },
        ) {
            val loaded = bitmap
            val playable = fileBytes
            when {
                // 影片：走內嵌播放器（同一個全螢幕殼、控制列疊在上面），
                // 不再另開外部視窗——用戶 2026-10-08「就不能弄的跟 tg discord 那種嗎」。
                // 有公網網址就直接串流（點開立刻播）；整檔還在下載、或加密房拿不到網址，
                // 才退回用手上這份位元組落地播放。
                // source == null 是「這一格還在等金鑰」，走最後面那個轉圈佔位。
                isVideo && source != null -> EmbeddedVideoPlayer(
                    client = client,
                    source = source,
                    bytes = playable,
                    poster = loaded,
                    boxWidth = 900.dp,
                    boxHeight = 900.dp,
                    playToggleTick = playToggleTick,
                    modifier = Modifier.fillMaxSize(),
                )
                loaded != null -> androidx.compose.foundation.Image(
                    bitmap = loaded,
                    contentDescription = caption.takeIf { it.isNotBlank() },
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y,
                            rotationZ = rotation,
                        )
                        .transformable(transformState),
                )
                failed -> Text(
                    caption.ifBlank { "🖼" },
                    color = chromeTint,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center),
                )
                else -> CircularProgressIndicator(
                    Modifier.align(Alignment.Center).padding(24.dp),
                    color = chromeTint,
                    strokeWidth = 3.dp,
                )
            }

            // 外圍控件不常顯：滑鼠動一下才出現、靜置 2.5 秒淡出（用戶 2026-10-08：「並不應該常顯」）。
            val safeIndex = index.coerceIn(slots.indices)
            if (chromeShown) {
                Box(Modifier.fillMaxSize().graphicsLayer { alpha = chromeAlpha }) {
                // 頂欄只留關閉。下載／旋轉／選單照 Element 挪到右下（用戶 2026-10-09 的 #34/#35），
                // 計數挪到左下並補上「誰在什麼時候發的」。
                Surface(
                    color = chromePill,
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 36.dp, end = 12.dp),
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = null, tint = chromeTint)
                    }
                }
                // 左下：第幾張／共幾張＋這一格的發送者與時間
                //（Element 是「第 6158 張照片，共 6159 張」＋「發送者 · 昨天 下午11:15」兩行）
                Surface(
                    color = chromePill,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 12.dp, bottom = 14.dp),
                ) {
                    Column(Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text(
                            text = "${safeIndex + 1} / ${slots.size}",
                            color = chromeTint,
                            style = MaterialTheme.typography.labelLarge,
                        )
                        if (slot.sender.isNotEmpty()) {
                            Text(
                                text = slot.sender + " · " +
                                    formatRelative(slot.timestamp, Clock.System.now().toEpochMilliseconds(), strings) +
                                    " " + formatClock(slot.timestamp),
                                color = chromeTint.copy(alpha = 0.72f),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                }
                // 右下動作列：換底色、下載、旋轉、三點選單——位置與順序照 #35 那張圖
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 8.dp),
                ) {
                    IconButton(
                        // 這一格還在等金鑰時按不动：沒金鑰根本存不出檔，
                        // 讓它按了才報「抓不到」等於把内部狀態問題丟給使用者
                        enabled = source != null,
                        onClick = {
                            scope.launch {
                                val bytes = grabBytes()
                                if (bytes == null || bytes.isEmpty()) {
                                    snackbar.showSnackbar(strings.downloadFailed)
                                    return@launch
                                }
                                downloader(bytes, saveName, mimeType ?: "image/png")
                                    .onSuccess { where -> snackbar.showSnackbar(where) }
                            }
                        },
                    ) {
                        Icon(BarIcons.Download, contentDescription = null, tint = chromeTint)
                    }
                    IconButton(
                        // 影片不給轉：轉的應該是畫面，不是播放器（內嵌播放器的控制列另有自己的方向）
                        enabled = !isVideo,
                        onClick = { rotation = (rotation + 90f) % 360f },
                    ) {
                        Icon(BarIcons.Rotate, contentDescription = null, tint = chromeTint)
                    }
                    Box {
                        IconButton(onClick = { menuOpen = !menuOpen }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = null, tint = chromeTint)
                        }
                        DropdownMenu(
                            expanded = menuOpen,
                            onDismissRequest = { menuOpen = false },
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            shadowElevation = 8.dp,
                        ) {
                            DropdownMenuItem(
                                text = { Text(strings.actionShowInChat) },
                                leadingIcon = { Icon(EyeIcon, contentDescription = null) },
                                onClick = {
                                    menuOpen = false
                                    actions.showInChat(slot.eventId)
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(strings.copy) },
                                leadingIcon = { Icon(BarIcons.ContentCopy, contentDescription = null) },
                                enabled = source != null,
                                onClick = {
                                    menuOpen = false
                                    scope.launch {
                                        val bytes = grabBytes()
                                        val ok = bytes != null && bytes.isNotEmpty() &&
                                            copyImageToClipboard(bytes, mimeType ?: "image/png")
                                        snackbar.showSnackbar(if (ok) strings.copiedToClipboard else strings.downloadFailed)
                                    }
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(strings.actionForward) },
                                leadingIcon = { Icon(BarIcons.Reply, contentDescription = null) },
                                enabled = source != null,
                                onClick = {
                                    menuOpen = false
                                    actions.forward(slot.eventId)
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(strings.viewAllPhotos) },
                                leadingIcon = { Icon(BarIcons.Gallery, contentDescription = null) },
                                onClick = {
                                    menuOpen = false
                                    actions.viewAll()
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(strings.actionSaveAs) },
                                leadingIcon = { Icon(BarIcons.Download, contentDescription = null) },
                                enabled = source != null,
                                onClick = {
                                    menuOpen = false
                                    scope.launch {
                                        val bytes = grabBytes()
                                        if (bytes == null || bytes.isEmpty()) {
                                            snackbar.showSnackbar(strings.downloadFailed)
                                            return@launch
                                        }
                                        saver(bytes, saveName, mimeType ?: "image/png")
                                            .onSuccess { where -> snackbar.showSnackbar(where) }
                                    }
                                },
                            )
                        }
                    }
                }
                // 連翻箭頭：左右兩側（到頭就停用，不是繞回——繞回讓人以為整組在循環，找不到「結束」）
                if (slots.size > 1) {
                    IconButton(
                        enabled = safeIndex > 0,
                        onClick = { onSelectIndex(safeIndex - 1) },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 8.dp)
                            .background(chromePill, CircleShape),
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = chromeTint,
                            modifier = Modifier.size(34.dp),
                        )
                    }
                    IconButton(
                        enabled = safeIndex < slots.lastIndex,
                        onClick = { onSelectIndex(safeIndex + 1) },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                            .background(chromePill, CircleShape),
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = chromeTint,
                            modifier = Modifier.size(34.dp),
                        )
                    }
                }
                }
            }

            // 提示往上挪：左下的計數與右下的動作列都住在底部，疊在它們上面就等於沒顯示
            SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter).padding(bottom = 76.dp))
        }
    }
}

/**
 * 附件面板的「所有照片」：整個房間已索引的媒體格子（用戶 2026-10-09
 * 「這個群組只能點開看五個照片，我記得電報那邊是可以直接一次性整理查看媒體數的」）。
 *
 * 清單與檢視器的連翻用的是同一份 [mergedMediaSlots]，所以點第 N 格開起來就停在第 N 張。
 * 加密房那些還沒補到金鑰的格子先顯示佔位並請上層去補（[onRequestResolve]），
 * 補到才換成縮圖——不會出現「點了開起來是空白」。
 */
@Composable
fun MediaGallery(
    client: MatrixClient,
    slots: List<MediaSlot>,
    resolved: Map<String, MessageBody.Image?>,
    onRequestResolve: (String) -> Unit,
    onOpen: (Int) -> Unit,
    onDismiss: () -> Unit,
    strings: Strings,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = false),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.97f))
                .clickable(onClick = onDismiss),
        ) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    Modifier.fillMaxWidth().padding(start = 20.dp, end = 8.dp, top = 16.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = strings.viewAllPhotos,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "${slots.size}",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = null, tint = Color.White)
                    }
                }
                if (slots.isEmpty()) {
                    Text(
                        text = strings.mediaGalleryEmpty,
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(112.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            start = 12.dp, end = 12.dp, bottom = 16.dp,
                        ),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        itemsIndexed(slots) { position, slot ->
                            val body = slot.body ?: resolved[slot.eventId]
                            Box(
                                Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White.copy(alpha = 0.06f)),
                            ) {
                                if (body != null) {
                                    MessageImage(
                                        client = client,
                                        source = body.source,
                                        width = body.width,
                                        height = body.height,
                                        isSticker = false,
                                        caption = body.caption,
                                        mimeType = body.mimeType,
                                        durationMs = body.durationMs,
                                        sizeBytes = body.sizeBytes,
                                        insideAlbumCell = true,
                                        modifier = Modifier.fillMaxSize(),
                                        onOpen = { onOpen(position) },
                                    )
                                } else {
                                    // 這格還在等金鑰：進到畫面才請上層去補，不自己發請求
                                    LaunchedEffect(slot.eventId) { onRequestResolve(slot.eventId) }
                                    CircularProgressIndicator(
                                        Modifier.align(Alignment.Center).padding(18.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** 檢視器與時間線共用快取鍵，但要用原圖鍵避免互相覆蓋縮圖位圖。 */
private fun MediaSource.cacheKeyShared(): String = when (this) {
    is MediaSource.Plain -> "full:" + mxcUrl
    is MediaSource.Encrypted -> "full:" + file.url
}
