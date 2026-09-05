package io.github.capricornus007.nashira

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import de.connect2x.trixnity.core.model.RoomId
import io.github.capricornus007.nashira.i18n.Strings
import io.github.capricornus007.nashira.matrix.MediaSource
import io.github.capricornus007.nashira.matrix.RoomRepository
import io.github.capricornus007.nashira.matrix.StickerItem
import io.github.capricornus007.nashira.matrix.StickerPack
import io.github.capricornus007.nashira.matrix.StickerRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

/**
 * Discord 式貼圖面板：頂部一排貼圖包籤，下面是當前包的貼圖網格。
 * 包清單來自 MSC2545（個人包 + emote 房間包），點貼圖直接送出。
 */
@Composable
fun StickerPicker(
    roomRepository: RoomRepository,
    roomId: RoomId,
    strings: Strings,
    onSend: (StickerItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val client = roomRepository.client
    val repository = remember(client) { StickerRepository(client) }
    val packs by remember(client) { repository.packs() }.collectAsState(initial = emptyList())

    // 房間包用房間顯示名（fallback roomId）
    val names by remember(client) { roomRepository.roomSummaries() }
        .collectAsState(initial = emptyList())

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth().height(280.dp),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        if (packs.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    strings.stickerEmpty,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            val packNames = packs.map { pack ->
                if (pack.roomId == null) strings.sticker
                else names.firstOrNull { it.roomId == pack.roomId }?.name ?: pack.name
            }
            val pagerState = rememberPagerState(pageCount = { packs.size })
            val scope = rememberCoroutineScope()
            Column {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    packNames.forEachIndexed { index, name ->
                        val selected = pagerState.currentPage == index
                        Text(
                            name,
                            maxLines = 1,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { scope.launch { pagerState.animateScrollToPage(index) } }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        )
                    }
                }
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                    StickerGrid(packs[page], client, onSend)
                }
            }
        }
    }
}

@Composable
private fun StickerGrid(pack: StickerPack, client: MatrixClient, onSend: (StickerItem) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(pack.stickers, key = { it.shortcode }) { sticker ->
            Box(
                Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSend(sticker) },
                contentAlignment = Alignment.Center,
            ) {
                StickerThumb(client, sticker)
            }
        }
    }
}

@Composable
private fun StickerThumb(client: MatrixClient, sticker: StickerItem) {
    val key = remember(sticker) { sticker.mxcUrl ?: sticker.file?.url ?: sticker.shortcode }
    var bitmap by remember(key) { mutableStateOf(MediaBitmapCache.get(key)) }
    var failed by remember(key) { mutableStateOf(false) }
    LaunchedEffect(client, key) {
        if (bitmap != null) return@LaunchedEffect
        repeat(MediaFetchAttempts) { attempt ->
            if (attempt > 0) delay(MediaRetryDelayMillis * attempt)
            val media = client.di.get<MediaService>().let { service ->
                when (val source = sticker.mxcUrl?.let { MediaSource.Plain(it) } ?: sticker.file?.let { MediaSource.Encrypted(it) }) {
                    is MediaSource.Plain -> service.getThumbnail(source.mxcUrl, 240, 240, maxSize = MaxMediaBytes)
                    is MediaSource.Encrypted -> service.getEncryptedMedia(source.file, maxSize = MaxMediaBytes)
                    null -> null
                }
            }?.getOrNull()
            val decoded = media?.toByteArray(this)?.let { decodeImageBitmap(it) }
            if (decoded != null) {
                MediaBitmapCache.put(key, decoded)
                bitmap = decoded
                return@LaunchedEffect
            }
        }
        failed = true
    }
    val loaded = bitmap
    when {
        loaded != null -> Image(
            bitmap = loaded,
            contentDescription = sticker.body,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
        )
        failed -> Text("🖼", style = MaterialTheme.typography.titleLarge)
        else -> CircularProgressIndicator(Modifier.padding(12.dp), strokeWidth = 2.dp)
    }
}
