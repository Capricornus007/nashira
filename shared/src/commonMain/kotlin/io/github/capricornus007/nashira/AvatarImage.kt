package io.github.capricornus007.nashira

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import de.connect2x.trixnity.client.MatrixClient
import de.connect2x.trixnity.client.media.MediaService
import kotlinx.coroutines.delay

/**
 * Matrix mxc 頭像。縮圖經 Trixnity 媒體快取取得，解出來的位圖按 mxc URL 記憶，
 * 列表滾動不會重新下載；取不到才退回字母佔位。
 */
@Composable
fun AvatarImage(
    client: MatrixClient,
    mxcUrl: String?,
    fallback: String,
    modifier: Modifier = Modifier,
) {
    val cache = remember(client) { AvatarCache }
    var bitmap by remember(client, mxcUrl) { mutableStateOf(mxcUrl?.let { cache.get(it) }) }
    LaunchedEffect(client, mxcUrl) {
        if (bitmap != null || mxcUrl.isNullOrBlank() || !mxcUrl.startsWith("mxc://")) return@LaunchedEffect
        // 啟動初期 Trixnity 還不知道伺服器支援認證媒體（ServerData 尚未從資料庫載入），
        // 那段時間的請求會走舊版 /_matrix/media 端點，較新上傳的媒體一律 404。
        // 失敗必須重試：一次就放棄會讓頭像整個工作階段都空著（自己的頭像最常中）。
        repeat(MediaFetchAttempts) { attempt ->
            if (attempt > 0) delay(MediaRetryDelayMillis * attempt)
            val media = client.di.get<MediaService>()
                .getThumbnail(mxcUrl, 96, 96, maxSize = 512_000)
                .getOrNull()
            val decoded = media?.toByteArray(this)?.let { decodeImageBitmap(it, maxDimension = 128) }
            if (decoded != null) {
                cache.put(mxcUrl, decoded)
                bitmap = decoded
                return@LaunchedEffect
            }
        }
    }
    val loaded = bitmap
    if (loaded != null) {
        Image(bitmap = loaded, contentDescription = null, modifier = modifier, contentScale = ContentScale.Crop)
        return
    }
    // 字母佔位的字級必須跟著容器縮放：固定 labelLarge 在 17dp 的資料夾預覽格裡會被裁掉半個字
    BoxWithConstraints(
        modifier = modifier.background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        val diameter = min(maxWidth, maxHeight)
        Text(
            fallback.trimStart('#', '!', '@').take(1).uppercase(),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = (diameter.value * 0.44f).sp,
            lineHeight = (diameter.value * 0.52f).sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
        )
    }
}

/** 媒體取回的嘗試次數；只為了跨過啟動初期那一小段「還不知道伺服器版本」的窗口。 */
internal const val MediaFetchAttempts = 4

/** 重試間隔基數（毫秒），第 n 次等 n 倍，避免離線時空轉。 */
internal const val MediaRetryDelayMillis = 1200L

/** 進程內頭像位圖快取：房間清單與 Space 圖示會反覆掛載同一個 mxc URL。 */
private object AvatarCache {
    private const val MAX_ENTRIES = 256
    private val entries = LinkedHashMap<String, ImageBitmap>()

    fun get(mxcUrl: String): ImageBitmap? = entries[mxcUrl]

    fun put(mxcUrl: String, bitmap: ImageBitmap) {
        entries[mxcUrl] = bitmap
        if (entries.size > MAX_ENTRIES) {
            val oldest = entries.keys.firstOrNull() ?: return
            entries.remove(oldest)
        }
    }
}
