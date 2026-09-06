package io.github.capricornus007.nashira

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import de.connect2x.trixnity.client.MatrixClient
import de.connect2x.trixnity.client.media.MediaService
import de.connect2x.trixnity.utils.toByteArray
import kotlinx.coroutines.delay
import io.github.capricornus007.nashira.matrix.MediaSource

/** 貼圖不畫底、不裁切，尺寸比照 Element／SchildiChat 的行內貼圖。 */
private val StickerMaxWidth = 148.dp

/** 圖片訊息的最大寬度；再寬會把發送者名字擠掉，Discord 也大約是這個比例。 */
private val ImageMaxWidth = 264.dp

/**
 * 時間線裡的圖片／貼圖。未加密房走 mxc 縮圖端點，加密房用事件自帶的金鑰解檔案，
 * 兩者都經 Trixnity 的媒體快取；解出來的位圖按來源鍵記憶，滾動不會重新下載。
 */
@Composable
fun MessageImage(
    client: MatrixClient,
    source: MediaSource,
    width: Int?,
    height: Int?,
    isSticker: Boolean,
    caption: String,
    modifier: Modifier = Modifier,
    mimeType: String? = null,
    /** 點圖開全螢幕檢視器；null（例如被隱藏的佔位）就不吃點擊。 */
    onOpen: (() -> Unit)? = null,
    /** 隱藏時顯示的佔位文案；null 表示未隱藏。 */
    hiddenLabel: String? = null,
) {
    val key = remember(source) { source.cacheKey() }
    var bitmap by remember(key) { mutableStateOf(MediaBitmapCache.get(key)) }
    var failed by remember(key) { mutableStateOf(false) }
    // Telegram 橋的動態貼圖是 video/webm：縮圖端點回 400、圖片解碼器吃不下，
    // 要抓原檔抽第一幀（與貼圖面板同一套 decodeVideoFrame）。
    val isVideo = remember(mimeType) { mimeType?.startsWith("video/") == true }
    LaunchedEffect(client, key) {
        if (bitmap != null) return@LaunchedEffect
        // 啟動初期伺服器版本還沒讀進來，請求會走舊版媒體端點被 404，所以失敗要重試幾次
        repeat(MediaFetchAttempts) { attempt ->
            if (attempt > 0) delay(MediaRetryDelayMillis * attempt)
            val media = client.di.get<MediaService>().let { service ->
                when (source) {
                    is MediaSource.Plain ->
                        if (isVideo || attempt > 0) {
                            // 影片沒有縮圖端點；圖片第一輪縮圖失敗也直接退原檔
                            service.getMedia(source.mxcUrl, maxSize = MaxMediaBytes)
                        } else {
                            service.getThumbnail(source.mxcUrl, 480, 480, maxSize = MaxMediaBytes)
                        }
                    is MediaSource.Encrypted -> service.getEncryptedMedia(source.file, maxSize = MaxMediaBytes)
                }
            }.getOrNull()
            val bytes = media?.toByteArray(this) ?: return@repeat
            val decoded = if (isVideo) {
                decodeVideoFrame(bytes, maxDimension = 512)
            } else {
                bytes.let { decodeImageBitmap(it) }
            }
            if (decoded != null) {
                MediaBitmapCache.put(key, decoded)
                bitmap = decoded
                return@LaunchedEffect
            }
        }
        failed = true
    }

    val maxWidth = if (isSticker) StickerMaxWidth else ImageMaxWidth
    // 事件裡的長寬只用來保留版位，避免圖片載入後把整條時間線往下推
    val ratio = ratioOf(width, height, bitmap)
    val frame = modifier
        .widthIn(max = maxWidth)
        .then(if (isSticker) Modifier else Modifier.clip(RoundedCornerShape(12.dp)))
    val loaded = bitmap
    when {
        // 隱藏的圖片：佔位可點擊恢復（Element 的「隱藏」也是可逆的）
        hiddenLabel != null -> Box(
            frame.fillMaxWidth().height(96.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                hiddenLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        loaded != null -> Image(
            bitmap = loaded,
            contentDescription = caption.takeIf { it.isNotBlank() },
            modifier = frame
                .fillMaxWidth()
                .aspectRatio(ratio)
                .then(if (onOpen != null) Modifier.clickable(onClick = onOpen) else Modifier),
            contentScale = if (isSticker) ContentScale.Fit else ContentScale.Crop,
        )
        // 載入失敗就退回檔名，至少看得出這裡本來有東西
        failed -> Text(
            caption.ifBlank { "🖼" },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        // 貼圖的佔位不畫灰底：多數貼圖有透明背景，灰塊會在載入前一閃，看起來
        // 像是「貼圖壞了」。圖片訊息保留灰底（裁切圓角需要一個可見的版位）。
        else -> if (isSticker) {
            Box(frame.fillMaxWidth().aspectRatio(ratio), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            }
        } else {
            Box(
                frame.fillMaxWidth().aspectRatio(ratio)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            }
        }
    }
}

/** 沒有尺寸資訊時給 4:3，載入後改用真實比例。極端長圖夾在 0.5–2.0 之間，避免一張圖佔滿整頁。 */
private fun ratioOf(width: Int?, height: Int?, bitmap: ImageBitmap?): Float {
    val w = bitmap?.width ?: width
    val h = bitmap?.height ?: height
    if (w == null || h == null || w <= 0 || h <= 0) return 4f / 3f
    return (w.toFloat() / h.toFloat()).coerceIn(0.5f, 2f)
}

/** 縮圖上限 2 MiB：時間線一次可能掛十幾張圖，原圖直接拉會把手機流量與記憶體吃光。 */
internal const val MaxMediaBytes = 2L * 1024 * 1024

private fun MediaSource.cacheKey(): String = when (this) {
    is MediaSource.Plain -> mxcUrl
    is MediaSource.Encrypted -> file.url
}

/** 進程內位圖快取：時間線來回滾動會反覆掛載同一則圖片訊息，貼圖面板也共用這份。 */
/**
 * 進程內位圖快取。**按位元組上限淘汰，不能只數個數**：64 張全解析度貼圖就能吃掉
 * 兩百多 MB，把 Android 的 256MB 堆積上限撐爆（實測 OutOfMemoryError）。
 */
internal object MediaBitmapCache {
    /** 約 48MB：足夠一個貼圖面板與一屏訊息，離 256MB 上限還有很大餘裕。 */
    private const val MAX_BYTES = 48L * 1024 * 1024
    private val entries = LinkedHashMap<String, ImageBitmap>()
    private var bytes = 0L

    private fun sizeOf(bitmap: ImageBitmap): Long = bitmap.width.toLong() * bitmap.height * 4

    fun get(key: String): ImageBitmap? = entries[key]

    fun put(key: String, bitmap: ImageBitmap) {
        entries.remove(key)?.let { bytes -= sizeOf(it) }
        entries[key] = bitmap
        bytes += sizeOf(bitmap)
        // LinkedHashMap 的插入順序就是淘汰順序（最舊的先走）
        while (bytes > MAX_BYTES && entries.size > 1) {
            val oldest = entries.keys.firstOrNull() ?: break
            entries.remove(oldest)?.let { bytes -= sizeOf(it) }
        }
    }
}

/** 抓原檔位元組（檢視器與「下載」選單共用；32MB 上限擋異常大檔）。 */
internal suspend fun fetchMediaBytes(client: MatrixClient, source: MediaSource): ByteArray? {
    val service = client.di.get<MediaService>()
    val media = when (source) {
        is MediaSource.Plain -> service.getMedia(source.mxcUrl, maxSize = 32L * 1024 * 1024)
        is MediaSource.Encrypted -> service.getEncryptedMedia(source.file, maxSize = 32L * 1024 * 1024)
    }.getOrNull() ?: return null
    return kotlinx.coroutines.coroutineScope { runCatching { media.toByteArray(this) }.getOrNull() }
}
