package io.github.capricornus007.nashira

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 全螢幕檢視器的一組媒體＋目前位置。相簿傳整組，單張傳只有一筆的組，
 * 這樣檢視器只有一條路徑（都能連翻、都有計數），不必分兩種狀態。
 */
data class MediaViewer(
    val items: List<MessageBody.Image>,
    val index: Int,
)

/**
 * 照 Telegram 把連翻範圍放大到**整個聊天室的媒體序列**。
 * tdesktop 的全螢幕檢視器翻的不是「這一組」，而是那份聊天媒體切片：
 * `overlay_widget.cpp:3002-3064 sharedMediaType()` 把照片／影片歸成一類，
 * `_index`（`:685`）是那份切片裡的序號，`moveToNext`（`:5905-5913`）就是序號加減一。
 * 所以從相簿裡點開一張，左右鍵能一路翻到更早其他訊息裡的圖。
 *
 * [loaded] 是時間線目前載入的那一段（**新→舊**），這裡翻成舊→新再攤平，
 * 相簿那幾張依序進清單，「組內相鄰」天然成立。
 * 找不到起點（來源比對不上）就退回原本傳進來的那一組——寧可少翻，不要翻錯。
 */
internal fun mediaStripFrom(
    loaded: List<io.github.capricornus007.nashira.matrix.TimelineMessage>,
    media: List<MessageBody.Image>,
    at: Int,
): MediaViewer {
    val strip = ArrayList<MessageBody.Image>(loaded.size)
    loaded.asReversed().forEach { message ->
        when (val body = message.body) {
            is MessageBody.Image -> if (!body.isSticker) strip += body
            is MessageBody.Album -> body.items.forEach { if (!it.isSticker) strip += it }
            else -> Unit
        }
    }
    val anchor = media.getOrNull(at) ?: return MediaViewer(media, at.coerceIn(media.indices))
    val found = strip.indexOfFirst { it.source == anchor.source }
    return if (found >= 0) MediaViewer(strip, found) else MediaViewer(media, at)
}

/**
 * 全螢幕圖片檢視器（Discord／Element 式）：
 * 雙指縮放＋平移、雙擊在 1x／2.5x 間切換、點背景關閉、右上角下載。
 * 原圖走 getMedia（時間線用的是縮圖），下載也用同一份位元組。
 *
 * [items] 大于一筆時就是相簿：左右兩側給連翻鈕、方向鍵也能翻，
 * 從時間線點的那一格開始（[index]），翻完整組才結束——照 Telegram／Element 的燈光箱。
 */
@Composable
fun ImageViewer(
    client: MatrixClient,
    items: List<MessageBody.Image>,
    index: Int,
    onSelectIndex: (Int) -> Unit,
    /** 「抓不到檔」時的提示文案（用呼叫端的 strings，不在這裡硬寫字串）。 */
    downloadFailedLabel: String,
    onDismiss: () -> Unit,
) {
    val current = items[index.coerceIn(items.indices)]
    val source = current.source
    val caption = current.caption
    val mimeType = current.mimeType
    val fileName = current.caption.ifBlank { "nashira-media" }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = false),
    ) {
        val key = source.cacheKeyShared()
        val isVideo = mimeType?.startsWith("video/") == true
        // 時間線那側早就把這張圖（圖片是縮圖、影片是首格）解好放在快取裡了，
        // 鍵就是 mxc／加密檔的 url。開檢視器時先拿它墊上，才不會變成空白轉圈：
        // 0.1.76 拿掉「開啟時預抓整檔」之後，影片開啟有約 5 秒什麼都沒有
        //（用戶 2026-10-08「依舊沒秒開」）——而那 5 秒要的畫面其實早就在手上了。
        val timelineKey = when (source) {
            is MediaSource.Plain -> source.mxcUrl
            is MediaSource.Encrypted -> source.file.url
        }
        var bitmap by remember(key) {
            mutableStateOf<ImageBitmap?>(MediaBitmapCache.get(key) ?: MediaBitmapCache.get(timelineKey))
        }
        var failed by remember(key) { mutableStateOf(false) }
        // 下載用原檔位元組（影片存 webm 原樣、圖片存原解析度），不重編碼
        var fileBytes by remember(key) { mutableStateOf<ByteArray?>(null) }
        LaunchedEffect(client, key) {
            if (bitmap != null) return@LaunchedEffect
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
        LaunchedEffect(index, items.size) {
            if (items.size > 1) runCatching { pagerFocus.requestFocus() }
        }
        val transformState = rememberTransformableState { zoomChange, panChange, _ ->
            scale = (scale * zoomChange).coerceIn(1f, 6f)
            offset = if (scale > 1f) offset + panChange else Offset.Zero
        }
        val snackbar = remember { SnackbarHostState() }
        val saver = rememberImageSaver()
        val scope = rememberCoroutineScope()

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
                            if (items.size <= 1) return@onPreviewKeyEvent false
                            val at = index.coerceIn(items.indices)
                            when (event.key) {
                                Key.DirectionLeft -> if (at > 0) onSelectIndex(at - 1)
                                Key.DirectionRight -> if (at < items.lastIndex) onSelectIndex(at + 1)
                                else -> return@onPreviewKeyEvent false
                            }
                            true
                        }
                    }
                }
                .background(Color.Black.copy(alpha = 0.92f))
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
                isVideo -> EmbeddedVideoPlayer(
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
                        )
                        .transformable(transformState),
                )
                failed -> Text(
                    caption.ifBlank { "🖼" },
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center),
                )
                else -> CircularProgressIndicator(
                    Modifier.align(Alignment.Center).padding(24.dp),
                    color = Color.White,
                    strokeWidth = 3.dp,
                )
            }

            // 外圍控件不常顯：滑鼠動一下才出現、靜置 2.5 秒淡出（用戶 2026-10-08：「並不應該常顯」）。
            if (chromeShown) {
                // 頂欄：關閉＋下載。半透明底確保任何圖片上都看得清。
                Surface(
                    color = Color.Black.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 36.dp, end = 12.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    // 影片走這條時通常還沒有位元組（不再預抓），現點現抓
                                    val bytes = fileBytes ?: fetchMediaBytesForPlayback(client, source)
                                    if (bytes == null || bytes.isEmpty()) {
                                        snackbar.showSnackbar(downloadFailedLabel)
                                        return@launch
                                    }
                                    val ext = when {
                                        mimeType?.contains("webp") == true -> "webp"
                                        mimeType?.contains("jpeg") == true || mimeType?.contains("jpg") == true -> "jpg"
                                        mimeType?.contains("webm") == true -> "webm"
                                        else -> "png"
                                    }
                                    val name = fileName.ifBlank { "nashira-media.$ext" }
                                    val type = mimeType ?: "image/png"
                                    saver(bytes, name, type)
                                        .onSuccess { where -> snackbar.showSnackbar(where) }
                                }
                            },
                        ) {
                            Icon(
                                BarIcons.Download,
                                contentDescription = null,
                                tint = Color.White,
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Filled.Close, contentDescription = null, tint = Color.White)
                        }
                    }
                }
                // 相簿連翻：左右兩側的箭頭鈕＋左上角計數，照 Element 的燈光箱
                //（到頭就停用，不是繞回——繞回讓人以為整組在循環，找不到「結束」）。
                // 不接方向鍵：桌面端 Dialog 沒拿到焦點時鍵盤事件根本進不來，
                // 擺一個「可能不會動」的按鍵比擺一個看得見按得動的鈕更糟。
                if (items.size > 1) {
                    val safeIndex = index.coerceIn(items.indices)
                    IconButton(
                        enabled = safeIndex > 0,
                        onClick = { onSelectIndex(safeIndex - 1) },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 8.dp)
                            .background(Color.Black.copy(alpha = 0.45f), CircleShape),
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(34.dp),
                        )
                    }
                    IconButton(
                        enabled = safeIndex < items.lastIndex,
                        onClick = { onSelectIndex(safeIndex + 1) },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                            .background(Color.Black.copy(alpha = 0.45f), CircleShape),
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(34.dp),
                        )
                    }
                    Surface(
                        color = Color.Black.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                        .graphicsLayer { alpha = chromeAlpha }
                        .padding(top = 36.dp, start = 12.dp),
                    ) {
                        Text(
                            text = "${safeIndex + 1} / ${items.size}",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                }
            }
            SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp))
        }
    }
}

/** 檢視器與時間線共用快取鍵，但要用原圖鍵避免互相覆蓋縮圖位圖。 */
private fun MediaSource.cacheKeyShared(): String = when (this) {
    is MediaSource.Plain -> "full:" + mxcUrl
    is MediaSource.Encrypted -> "full:" + file.url
}
