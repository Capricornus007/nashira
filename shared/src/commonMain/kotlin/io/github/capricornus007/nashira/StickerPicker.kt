package io.github.capricornus007.nashira

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
    onSendImage: (PickedImage) -> Unit,
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
            // 包名優先用包自己的 display_name（state_key 兜底）。同一個貼圖倉庫房會掛
            // 好幾包，用房間名當標籤會變成三個一樣的「貼圖包倉庫」，分不出來。
            val packNames = packs.map { pack ->
                when {
                    pack.roomId == null -> strings.sticker
                    pack.name.isNotBlank() -> pack.name
                    else -> names.firstOrNull { it.roomId == pack.roomId }?.name ?: pack.roomId.full
                }
            }
            val pagerState = rememberPagerState(pageCount = { packs.size })
            val scope = rememberCoroutineScope()
            // 相簿圖片入口：平台不支援（桌面）時不出現
            val imageLauncher = rememberImagePickerLauncher { image ->
                onSendImage(image)
            }
            // Surface 的內容槽是 Box 語意：圖片入口和分頁列必須在同一個 Column 裡，
            // 否則兩者疊在同一格（實測「發送圖片」壓在包名上）。
            Column(Modifier.fillMaxSize()) {
                if (imageLauncher != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { imageLauncher() },
                    ) {
                        Text(
                            strings.sendImage,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        )
                    }
                }
                // 包多了要能橫向捲，不然後面的包點不到
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp, vertical = 6.dp),
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
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().weight(1f)) { page ->
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
            val service = client.di.get<MediaService>()
            val source = sticker.mxcUrl?.let { MediaSource.Plain(it) } ?: sticker.file?.let { MediaSource.Encrypted(it) }
            // 縮圖端點對 webp/動圖常常直接失敗（伺服器不生縮圖）。最後一輪改抓原圖，
            // 否則整包貼圖只會顯示破圖佔位。
            val fullSize = attempt >= MediaFetchAttempts - 1
            val media = when (source) {
                is MediaSource.Plain ->
                    if (fullSize) service.getMedia(source.mxcUrl, maxSize = MaxMediaBytes)
                    else service.getThumbnail(source.mxcUrl, 240, 240, maxSize = MaxMediaBytes)
                is MediaSource.Encrypted -> service.getEncryptedMedia(source.file, maxSize = MaxMediaBytes)
                null -> null
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
