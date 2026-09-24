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
import androidx.compose.ui.graphics.Color
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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.itemsIndexed as gridItemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.rememberCoroutineScope
import io.github.capricornus007.nashira.emoji.EmojiEntry
import io.github.capricornus007.nashira.emoji.EmojiIndex
import io.github.capricornus007.nashira.emoji.EmojiTable
import kotlinx.coroutines.launch
/**
 * Discord 式貼圖面板：頂部「貼圖／表情」分頁，貼圖頁是一排貼圖包籤＋當前包的
 * 貼圖網格（點擊直接送出），表情頁是所有包裡 usage=emoticon 的條目
 * （點擊插進輸入列，隨文字一起以 formatted_body 發送，P5-2）。
 * 包清單來自 MSC2545（個人包 + emote 房間包）。
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
        // P5-2：頂部分頁——貼圖（發 m.sticker）／表情（插入輸入列當 custom emoji）
        var emojiTab by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                listOf(false to strings.sticker, true to strings.emoticons).forEach { (isEmoji, label) ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (emojiTab == isEmoji) MaterialTheme.colorScheme.secondaryContainer
                        else Color.Transparent,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { emojiTab = isEmoji },
                    ) {
                        Text(
                            label,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (emojiTab == isEmoji) MaterialTheme.colorScheme.onSecondaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                        )
                    }
                }
            }
            if (emojiTab) {
                EmojiPage(repository, client, strings, onPickEmoticon, onPickEmoji)
            } else if (packs.isEmpty()) {
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
                // 包選擇是封面圖示條（Telegram／Discord／Element 都是這樣）：
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
 * 「表情」分頁：搜尋＋最近使用＋自訂表情（MSC2545 emoticon）＋Unicode 分類，
 * 全部混在同一個網格裡——Discord 就是這麼排的（自訂表情排在分類前面），
 * 拆成兩個分頁反而要多點一次。
 *
 * 分類節標用 glyph 不用文字：九個分類×六種語言是 54 個翻譯鍵，而 Discord／
 * Telegram 的分類條本來就只有圖標沒有文字；同一個 glyph 兼作節標與分隔線。
 */
@Composable
private fun EmojiPage(
    repository: StickerRepository,
    client: MatrixClient,
    strings: Strings,
    onPickEmoticon: (StickerItem) -> Unit,
    onPickEmoji: (EmojiEntry) -> Unit,
) {
    val ui = LocalUiState.current
    var query by remember { mutableStateOf("") }
    val emoticons by remember(client) { repository.emoticons() }.collectAsState(initial = emptyList())
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val recents = remember(ui.emojiRecents) { EmojiIndex.byHexcodes(ui.emojiRecents) }
    val plan = remember(query, recents, emoticons, strings) {
        buildEmojiPlan(strings, query, recents, emoticons)
    }

    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            },
            placeholder = { Text(strings.emojiSearchHint, style = MaterialTheme.typography.bodyMedium) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        )
        // 分類圖示條：點了捲到該分類。搜尋時收起來（結果本来就是扁平清單，沒有分類可跳）
        if (query.isBlank()) {
            LazyRow(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                itemsIndexed(EmojiIndex.categories) { _, cat ->
                    val target = plan.categoryIndex[cat.group] ?: return@itemsIndexed
                    IconButton(
                        onClick = { scope.launch { gridState.animateScrollToItem(target) } },
                        modifier = Modifier.size(34.dp),
                    ) {
                        Text(cat.icon, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(40.dp),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            gridItemsIndexed(
                plan.rows,
                key = { _, row -> row.key },
                span = { _, row ->
                    if (row is EmojiRow.Header) GridItemSpan(maxLineSpan) else GridItemSpan(1)
                },
            ) { _, row ->
                when (row) {
                    is EmojiRow.Header -> EmojiSectionHeader(row, onClearRecents = { ui.emojiRecents = emptyList() })
                    is EmojiRow.Emoji -> EmojiCell(row.entry) {
                        // 用過就進「最近使用」；插進輸入列由呼叫端決定（游標位置只有它知道）
                        ui.rememberEmojiUsage(row.entry.hexcode)
                        onPickEmoji(row.entry)
                    }
                    is EmojiRow.Emote -> Box(
                        Modifier.size(40.dp).padding(2.dp).clip(RoundedCornerShape(8.dp))
                            .clickable { onPickEmoticon(row.item) },
                    ) {
                        StickerThumb(client, row.item)
                    }
                }
            }
        }
    }
}

private sealed interface EmojiRow {
    val key: String
    /** 橫跨整行的節標。[text] 與 [icon] 二擇一：前者是「最近使用／表情」這類本地化標題，後者是分類 glyph。 */
    data class Header(override val key: String, val icon: String?, val text: String?, val clearable: Boolean) : EmojiRow
    data class Emoji(override val key: String, val entry: EmojiEntry) : EmojiRow
    data class Emote(override val key: String, val item: StickerItem) : EmojiRow
}

private class EmojiPlan(val rows: List<EmojiRow>, val categoryIndex: Map<Int, Int>)

/** 把「搜尋結果」或「最近＋自訂＋九個分類」攤成一格一筆的清單，順帶記每個分類的起始索引。 */
private fun buildEmojiPlan(
    strings: Strings,
    query: String,
    recents: List<EmojiEntry>,
    emoticons: List<StickerItem>,
): EmojiPlan {
    val rows = ArrayList<EmojiRow>()
    if (query.isNotBlank()) {
        val hits = EmojiIndex.search(query)
        if (hits.isEmpty()) {
            rows += EmojiRow.Header("h-none", null, strings.emojiNoResults, false)
        } else {
            hits.forEach { rows += EmojiRow.Emoji("s-" + it.hexcode, it) }
        }
        return EmojiPlan(rows, emptyMap())
    }
    if (recents.isNotEmpty()) {
        rows += EmojiRow.Header("h-recent", null, strings.emojiRecent, true)
        recents.forEach { rows += EmojiRow.Emoji("r-" + it.hexcode, it) }
    }
    if (emoticons.isNotEmpty()) {
        rows += EmojiRow.Header("h-emote", null, strings.emoticons, false)
        emoticons.forEach {
            rows += EmojiRow.Emote("e-" + it.shortcode + (it.mxcUrl ?: it.file?.url ?: ""), it)
        }
    }
    val index = LinkedHashMap<Int, Int>()
    for (cat in EmojiIndex.categories) {
        val entries = EmojiTable.byGroup[cat.group].orEmpty()
        if (entries.isEmpty()) continue
        index[cat.group] = rows.size
        rows += EmojiRow.Header("h-cat" + cat.group, cat.icon, null, false)
        entries.forEach { rows += EmojiRow.Emoji("u-" + it.hexcode, it) }
    }
    return EmojiPlan(rows, index)
}

@Composable
private fun EmojiSectionHeader(row: EmojiRow.Header, onClearRecents: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(start = 8.dp, end = 4.dp, top = 6.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when {
            row.text != null -> Text(
                row.text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            row.icon != null -> Text(row.icon, style = MaterialTheme.typography.labelLarge)
        }
        if (row.clearable) {
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onClearRecents, modifier = Modifier.size(22.dp)) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

@Composable
private fun EmojiCell(entry: EmojiEntry, onClick: () -> Unit) {
    // 膚色變體（entry.skins）暫時不做長按選色——先確保插入與最近使用是對的，
    // 選色器是另一件事（Discord 是長按彈出，Telegram 是右鍵）
    Box(
        Modifier
            .size(40.dp)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(entry.glyph, style = MaterialTheme.typography.headlineSmall)
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
        columns = GridCells.Adaptive(96.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
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
