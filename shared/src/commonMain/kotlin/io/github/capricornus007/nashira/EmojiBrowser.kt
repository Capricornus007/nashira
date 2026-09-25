package io.github.capricornus007.nashira

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val recents = remember(ui.emojiRecents) { EmojiIndex.byHexcodes(ui.emojiRecents) }
    val plan = remember(query, recents, emoticons, strings) {
        buildEmojiPlan(strings, query, recents, emoticons)
    }

    Column(modifier) {
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
                        // 用過就進「最近使用」；插進輸入列還是加反應由呼叫端決定
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
