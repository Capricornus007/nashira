package io.github.capricornus007.nashira

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.graphicsLayer
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
                    modifier = Modifier.align(Alignment.TopStart).padding(top = 36.dp, start = 12.dp),
                ) {
                    Text(
                        text = "${safeIndex + 1} / ${items.size}",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
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
