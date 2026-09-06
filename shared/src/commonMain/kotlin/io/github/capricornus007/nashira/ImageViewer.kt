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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 全螢幕圖片檢視器（Discord／Element 式）：
 * 雙指縮放＋平移、雙擊在 1x／2.5x 間切換、點背景關閉、右上角下載。
 * 原圖走 getMedia（時間線用的是縮圖），下載也用同一份位元組。
 */
@Composable
fun ImageViewer(
    client: MatrixClient,
    source: MediaSource,
    caption: String,
    fileName: String,
    mimeType: String?,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = false),
    ) {
        val key = source.cacheKeyShared()
        var bitmap by remember(key) { mutableStateOf<ImageBitmap?>(MediaBitmapCache.get(key)) }
        var failed by remember(key) { mutableStateOf(false) }
        // 下載用原檔位元組（影片存 webm 原樣、圖片存原解析度），不重編碼
        var fileBytes by remember(key) { mutableStateOf<ByteArray?>(null) }
        LaunchedEffect(client, key) {
            if (bitmap != null) return@LaunchedEffect
            runCatching {
                val service = client.di.get<MediaService>()
                val media = when (source) {
                    is MediaSource.Plain -> service.getMedia(source.mxcUrl, maxSize = 32L * 1024 * 1024)
                    is MediaSource.Encrypted -> service.getEncryptedMedia(source.file, maxSize = 32L * 1024 * 1024)
                }.getOrThrow()
                val bytes = media.toByteArray(this) ?: return@runCatching
                fileBytes = bytes
                // 影片類媒體在檢視器裏先以首幀顯示；下載仍存原檔
                val decoded = if (mimeType?.startsWith("video/") == true) {
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

        var scale by remember { mutableFloatStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }
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
            when {
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
                                val bytes = fileBytes
                                if (bytes == null) return@launch
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
                        // icons-core 沒有 Download 圖示；用文字箭頭（與貼圖重試磚同模式）
                        Text("↓", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = null, tint = Color.White)
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
