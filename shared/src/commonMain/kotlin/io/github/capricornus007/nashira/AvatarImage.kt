package io.github.capricornus007.nashira

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import de.connect2x.trixnity.client.MatrixClient
import de.connect2x.trixnity.client.media.MediaService

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
        val media = client.di.get<MediaService>()
            .getThumbnail(mxcUrl, 96, 96, maxSize = 512_000)
            .getOrNull() ?: return@LaunchedEffect
        val bytes = media.toByteArray(this) ?: return@LaunchedEffect
        val decoded = decodeImageBitmap(bytes) ?: return@LaunchedEffect
        cache.put(mxcUrl, decoded)
        bitmap = decoded
    }
    val loaded = bitmap
    if (loaded != null) {
        Image(bitmap = loaded, contentDescription = null, modifier = modifier, contentScale = ContentScale.Crop)
    } else {
        Box(modifier = modifier.background(MaterialTheme.colorScheme.secondaryContainer), contentAlignment = Alignment.Center) {
            Text(
                fallback.trimStart('#', '!', '@').take(1).uppercase(),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

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
