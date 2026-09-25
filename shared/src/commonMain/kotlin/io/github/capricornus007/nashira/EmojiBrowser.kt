package io.github.capricornus007.nashira

import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed as gridItemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.connect2x.trixnity.client.MatrixClient
import io.github.capricornus007.nashira.emoji.EmojiEntry
import io.github.capricornus007.nashira.emoji.EmojiIndex
import io.github.capricornus007.nashira.emoji.EmojiTable
import io.github.capricornus007.nashira.i18n.Strings
import io.github.capricornus007.nashira.matrix.StickerItem
import kotlinx.coroutines.launch

/**
 * 表情瀏覽器的全部：搜尋列＋分類圖示條＋「最近使用／自訂表情／九個 Unicode 分類」
 * 混排的網格。輸入列的表情分頁與訊息的反應選擇器共用它——兩者只差在要不要列
 * MSC2545 的自訂表情，以及點下去做什麼。
 *
 * 自訂表情傳空清單進來就不出現（反應選擇器就是這樣）：Trixnity 5.8.1 的
 * `RelatesTo.Annotation` 只有 (eventId, key)，放不下 MSC2545 反應要的
 * `m.relates_to.url`，給按了卻送不出去的表情比不給更糟。
 *
 * 分類節標用 glyph 不用文字：九個分類×五種語言是 45 個翻譯鍵，而 Discord／
 * Telegram 的分類條本來就只有圖標沒有文字；同一個 glyph 兼作節標與分隔線。
 */
@Composable
internal fun EmojiBrowser(
    strings: Strings,
    client: MatrixClient,
    emoticons: List<StickerItem>,
    onPickEmoji: (EmojiEntry) -> Unit,
    onPickEmoticon: (StickerItem) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val ui = LocalUiState.current
    var query by remember { mutableStateOf("") }
    // 長按（觸控）／右鍵（滑鼠）有膚色的表情 → 網格上方浮出一條膚色列。
    // 刻意不用 Popup 第二層視窗：反應選擇器本身就在 DropdownMenu 裡，再開一層
    // 會搶走下層選單的焦點、選單直接收起，膚色列跟著消失（選不到）。
    var toneSource by remember { mutableStateOf<EmojiEntry?>(null) }
    // Discord 的招牌：滑鼠停在格子上時，下方顯示 :shortcode: 與名稱
    var preview by remember { mutableStateOf<EmojiEntry?>(null) }
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val recents = remember(ui.emojiRecents) { EmojiIndex.byHexcodes(ui.emojiRecents) }
    val plan = remember(query, recents, emoticons, strings) {
        buildEmojiPlan(strings, query, recents, emoticons)
    }
    // 用過就進「最近使用」；插進輸入列還是加反應由呼叫端決定
    val pick: (EmojiEntry) -> Unit = { entry ->
        ui.rememberEmojiUsage(entry.hexcode)
        onPickEmoji(entry)
    }

    Column(modifier) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it; toneSource = null },
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
        toneSource?.let { base ->
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EmojiCell(base, onShowTones = {}, onClick = { toneSource = null; pick(base) })
                base.skins.forEach { (hex, glyph) ->
                    val variant = base.copy(hexcode = hex, glyph = glyph)
                    EmojiCell(variant, onShowTones = {}, onClick = { toneSource = null; pick(variant) })
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { toneSource = null }, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(40.dp),
            state = gridState,
            // 剩下的高度全給網格（下方預覽條是固定高）；用 fillMaxSize 會把
            // 整列高度吃光、預覽條被擠掉
            modifier = Modifier.weight(1f),
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
                    is EmojiRow.Emoji -> EmojiCell(
                        row.entry,
                        onShowTones = { toneSource = row.entry },
                        onClick = {
                            toneSource = null
                            pick(row.entry)
                        },
                        onHover = { preview = row.entry },
                    )
                    is EmojiRow.Emote -> Box(
                        Modifier.size(40.dp).padding(2.dp).clip(RoundedCornerShape(8.dp))
                            .clickable { onPickEmoticon(row.item) },
                    ) {
                        StickerThumb(client, row.item)
                    }
                }
            }
        }
        // 預覽條高度固定：沒有停在任何格子上時留白（不撤掉這一列，否則網格會跳動）。
        // 指針離開整個面板時也不清，保留最後停過的那個。
        Row(
            Modifier.fillMaxWidth().height(26.dp).padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            preview?.let { e ->
                Text(e.glyph, style = MaterialTheme.typography.titleMedium)
                if (e.shortcode.isNotEmpty()) {
                    Text(
                        ":" + e.shortcode + ":",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(
                    e.nameZh.ifEmpty { e.nameEn },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

internal sealed interface EmojiRow {
    val key: String
    /** 橫跨整行的節標。[text] 與 [icon] 二擇一：前者是「最近使用／表情」這類本地化標題，後者是分類 glyph。 */
    data class Header(override val key: String, val icon: String?, val text: String?, val clearable: Boolean) : EmojiRow
    data class Emoji(override val key: String, val entry: EmojiEntry) : EmojiRow
    data class Emote(override val key: String, val item: StickerItem) : EmojiRow
}

internal class EmojiPlan(val rows: List<EmojiRow>, val categoryIndex: Map<Int, Int>)

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
private fun EmojiCell(
    entry: EmojiEntry,
    onShowTones: () -> Unit,
    onClick: () -> Unit,
    onHover: () -> Unit = {},
) {
    // 沒有膚色的表情（絕大多數）長按／右鍵不做事，別讓它看起來壞了
    val hasTones = entry.skins.isNotEmpty()
    val hoverSource = remember { MutableInteractionSource() }
    val hovered by hoverSource.collectIsHoveredAsState()
    LaunchedEffect(hovered) { if (hovered) onHover() }
    Box(
        Modifier
            .size(40.dp)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .hoverable(hoverSource)
            .contextMenuGestures(
                onClick = onClick,
                onContextMenu = { if (hasTones) onShowTones() },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(entry.glyph, style = MaterialTheme.typography.headlineSmall)
    }
}
