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
import androidx.compose.foundation.lazy.grid.GridItemSpan
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextOverflow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import io.github.capricornus007.nashira.emoji.EmojiEntry
/**
 * Discord 式貼圖面板：頂部「貼圖／表情」分頁。貼圖頁是一排貼圖包籤＋當前包的貼圖
 * 網格（點擊直接送 m.sticker）；表情頁是整個 Unicode 表情瀏覽器（`EmojiBrowser`），
 * 點的 Unicode 表情插進輸入列游標處，點的 MSC2545 自訂表情則隨文字一起以
 * formatted_body 發送（P5-2）。包清單來自 MSC2545（個人包 + emote 房間包）。
 */
@Composable
fun StickerPicker(
    roomRepository: RoomRepository,
    roomId: RoomId,
    strings: Strings,
    onSend: (StickerItem) -> Unit,
    onPickEmoticon: (StickerItem) -> Unit = {},
    /** 點 Unicode 表情：插進輸入列游標處（不是送訊息）。 */
    onPickEmoji: (EmojiEntry) -> Unit = {},
    /**
     * 輸入列游標前那個詞：面板不自帶搜尋欄，**打字就是過濾**（MoregramX／Nagram XF
     * 實錄與 64Gram 桌面版的行為）。空字串＝不過濾。
     */
    emojiFilter: String = "",
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
        // 高度由呼叫端決定（ChatScreen 給「與鍵盤等高」的那個值）——這裡不能再 .height(300.dp)，
        // 修飾鏈上後面的 height 會蓋掉傳進來的，面板永遠 300dp，跟輸入法高度永遠對不上。
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 8.dp,
    ) {
        // P5-2：分頁——貼圖（發 m.sticker）／表情（插入輸入列當 custom emoji）
        var emojiTab by remember { mutableStateOf(false) }
        // 分頁列是「網格的第一格」而不是釘在面板頂：MoregramX／Nagram XF 實錄
        // （~/ref-shots/mgx-panel.mp4）往下捲時它會跟著滑走、捲回頂才回來。
        // 置中純文字、選中上主色，對齊 64Gram 桌面版的「表情符號／貼圖／GIF」。
        val tabs: @Composable () -> Unit = {
            Row(
                Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                listOf(strings.sticker to false, strings.emoticons to true).forEach { (label, isEmoji) ->
                    val active = emojiTab == isEmoji
                    Text(
                        label,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (active) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { emojiTab = isEmoji }
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                    )
                }
            }
        }
        Column(Modifier.fillMaxSize()) {
            if (emojiTab) {
                val emoticons by remember(client) { repository.emoticons() }.collectAsState(initial = emptyList())
                EmojiBrowser(
                    strings = strings,
                    client = client,
                    emoticons = emoticons,
                    onPickEmoji = onPickEmoji,
                    onPickEmoticon = onPickEmoticon,
                    query = emojiFilter,
                    header = tabs,
                    modifier = Modifier.weight(1f),
                )
            } else if (packs.isEmpty()) {
                Column(Modifier.weight(1f).fillMaxWidth()) {
                    tabs()
                    Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            strings.stickerEmpty,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
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
                // 貼圖頁：分頁條**釘住**（MoregramX 逐幀實測——捲動時它不動），會跟著捲走的
                // 只有當前包名；表情頁相反，分類圖示條會隨捲動消失（見 EmojiBrowser）。
                // 底部包封面條兩頁都固定（64Gram／MoregramX 都是這個配置）。
                tabs()
                StickerGrid(
                    pack = packs[index],
                    client = client,
                    onSend = onSend,
                    header = {
                        Text(
                            packNames.getOrElse(index) { "" },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 2.dp),
                        )
                    },
                    modifier = Modifier.weight(1f),
                )
                // 包選擇是封面圖示條（Telegram／Discord／Element 都是這樣）：
                // 原本的長文字標籤在包多時會橫向溢出，只能靠拖曳，滑鼠與觸控板都不順手。
                // LazyRow 本身吃滾輪與拖曳，且只渲染可見項。
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
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
private fun StickerGrid(
    pack: StickerPack,
    client: MatrixClient,
    onSend: (StickerItem) -> Unit,
    /** 網格第一格（跨整行）：分頁列＋當前包名。放進來才會跟著貼圖一起捲動。 */
    header: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(96.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item(key = "h-pack-header", span = { GridItemSpan(maxLineSpan) }) { header() }
        items(pack.stickers, key = { it.shortcode + (it.mxcUrl ?: it.file?.url ?: "") }) { sticker ->
            Box(
                Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onSend(sticker) },
            ) {
                StickerThumb(client, sticker)
            }
        }
    }
}

@Composable
internal fun StickerThumb(
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
