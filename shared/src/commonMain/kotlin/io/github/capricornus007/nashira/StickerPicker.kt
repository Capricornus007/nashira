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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.Color

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
        modifier = modifier.fillMaxWidth().height(300.dp),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 8.dp,
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
            var selected by remember(packs.size) { mutableStateOf(0) }
            val index = selected.coerceIn(0, packs.lastIndex)
            Column(Modifier.fillMaxSize()) {
                // 包選擇改成封面圖示條（Telegram／Discord／Element 都是這樣）：
                // 原本的長文字標籤在包多時會橫向溢出，只能靠拖曳，滑鼠與觸控板都不順手。
                // LazyRow 本身吃滾輪與拖曳，且只渲染可見項。
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    itemsIndexed(packs) { position, pack ->
                        PackTab(
                            client = client,
                            pack = pack,
                            label = packNames.getOrElse(position) { pack.name },
                            selected = position == index,
                            onClick = { selected = position },
                        )
                    }
                }
                Text(
                    packNames.getOrElse(index) { "" },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 2.dp),
                )
                StickerGrid(packs[index], client, onSend)
            }
        }
    }
}

/**
 * 圖示條上的一個包：封面用 `pack.avatar_url`，沒設就拿第一張貼圖。
 *
 * 封面必須走 `StickerThumb` 而不是 `AvatarImage`：貼圖多半是 webp，伺服器的縮圖端點
 * 對它常常直接失敗，只有 StickerThumb 那條「最後一輪抓原圖」的退路能載到（實機上
 * 用 AvatarImage 時整條圖示條都只剩字母佔位）。
 */
@Composable
private fun PackTab(
    client: MatrixClient,
    pack: StickerPack,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    // 封面候選鏈：pack.avatar → 第一張「圖片」貼圖 → 第一張貼圖。avatar 在矩陣上
    // 常見「設了但媒體已 404／被配額清掉」，單一候選會讓圖示永遠卡 ↻。
    val candidates = remember(pack) {
        buildList {
            pack.avatarUrl?.let { url ->
                add(StickerItem(shortcode = label, body = label, mxcUrl = url, file = null, info = null, mimeType = null))
            }
            pack.stickers.firstOrNull { it.mimeType?.startsWith("video/") != true }?.let(::add)
            pack.stickers.firstOrNull()?.let(::add)
        }.distinctBy { it.mxcUrl ?: it.file?.url ?: it.shortcode }
    }
    var attempt by remember(pack) { mutableStateOf(0) }
    val cover = candidates.getOrNull(attempt)
    Box(
        Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (cover != null) {
            StickerThumb(client, cover, onFailed = { attempt += 1 })
        } else {
            Text(label.take(1).uppercase(), style = MaterialTheme.typography.labelMedium)
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
private fun StickerThumb(
    client: MatrixClient,
    sticker: StickerItem,
    /** 載入失敗時交給呼叫端接手（PackTab 換下一個封面候選）；null 時顯示 ↻ 重試磚。 */
    onFailed: (() -> Unit)? = null,
) {
    val key = remember(sticker) { sticker.mxcUrl ?: sticker.file?.url ?: sticker.shortcode }
    var bitmap by remember(key) { mutableStateOf(MediaBitmapCache.get(key)) }
    var failed by remember(key) { mutableStateOf(false) }
    var reloadToken by remember(key) { mutableStateOf(0) }
    val isVideo = remember(sticker.mimeType) { sticker.mimeType?.startsWith("video/") == true }
    LaunchedEffect(client, key, reloadToken) {
        failed = false
        if (bitmap != null) return@LaunchedEffect
        val service = client.di.get<MediaService>()
        val source = sticker.mxcUrl?.let { MediaSource.Plain(it) } ?: sticker.file?.let { MediaSource.Encrypted(it) }
        repeat(MediaFetchAttempts) { attempt ->
            if (attempt > 0) delay(MediaRetryDelayMillis)
            val media = when (source) {
                is MediaSource.Plain -> {
                    // Synapse 對 animated webp / sticker thumbnail 很常直接 404 或回空。
                    // 第一輪縮圖失敗後立刻退回原圖，不要一直卡 spinner。
                    val preferThumbnail = !isVideo && attempt == 0
                    val result = if (preferThumbnail) {
                        service.getThumbnail(source.mxcUrl, 240, 240, maxSize = MaxMediaBytes)
                    } else {
                        service.getMedia(source.mxcUrl, maxSize = MaxMediaBytes)
                    }
                    result.getOrNull()
                }
                is MediaSource.Encrypted -> {
                    service.getEncryptedMedia(source.file, maxSize = MaxMediaBytes).getOrNull()
                }
                null -> null
            }
            val mediaBytes = media?.toByteArray(this)
            if (mediaBytes == null) return@repeat
            val decoded = if (isVideo) {
                decodeVideoFrame(mediaBytes, maxDimension = 256)
            } else {
                decodeImageBitmap(mediaBytes, maxDimension = 256)
            }
            if (decoded != null) {
                MediaBitmapCache.put(key, decoded)
                bitmap = decoded
                failed = false
                return@LaunchedEffect
            }
        }
        failed = true
        onFailed?.invoke()
    }
    val loaded = bitmap
    when {
        loaded != null -> Image(
            bitmap = loaded,
            contentDescription = sticker.body,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
        )
        // 呼叫端接手失敗（PackTab 換候選）時不畫重試磚——重試磚是給格狀圖用的
        failed && onFailed == null -> Box(
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .clickable {
                    failed = false
                    bitmap = null
                    reloadToken += 1
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(if (isVideo) "▶" else "↻", style = MaterialTheme.typography.titleLarge)
        }
        else -> CircularProgressIndicator(Modifier.padding(12.dp), strokeWidth = 2.dp)
    }
}
