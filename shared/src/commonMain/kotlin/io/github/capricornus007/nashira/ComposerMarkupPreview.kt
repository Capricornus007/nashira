package io.github.capricornus007.nashira

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

/**
 * 輸入框的**即時格式化預覽**：草稿存的仍是帶標記的原文（`**粗**`、`<u>底</u>`），
 * 顯示時把標記**真的刪掉**、內層套上樣式——所以框裡看到的是「粗體／底線的字的」，
 * 不是 `<u>…</u>` 這種標籤（用戶 2026-10-08 兩度點名要做，2026-10-09 又指出
 * 我回退成顯示標籤是「退化」）。
 *
 * 兩條走過的錯路，都留下實據，別再走：
 * - `VisualTransformation` 這個參數**只存在於舊的 `value/onValueChange` 多載**，
 *   掛到 state 版上編譯直接報「No parameter with name 'visualTransformation' found」。
 * - 「把標記畫成透明」會讓標記**繼續佔排版寬度**：字被無形的 `<u>` 推右，
 *   而選取框照原文範圍畫 ⇒ 框與字錯開（用戶截圖：「正常情況完全不應該跑掉吧？」）。
 *
 * 正解就是現在這版：入口是 state 版的 `outputTransformation`
 *（androidx `compose/foundation/.../text/BasicTextField.kt:176`、`:225`），
 * 接收者 `TextFieldBuffer` 可以 `replace(start, end, "")` **真的移除**標記字元；
 * 它的文件保證游標與選取由 `BasicTextField` 內部換算，所以我們不必自己寫映射表。
 * 順序講究：**先**在原文座標上蓋樣式，**再**從尾端往前刪標記（從後面刪起，前面的索引才不會失效）。
 */
internal val ComposerMarkupPreview = OutputTransformation {
    val source = originalText.toString()
    val found = findMarkupRuns(source)
    // 沒有成對標記就什麼都不動（連刪都不刪，位置自然是原文位置）
    if (found.isNotEmpty()) {
        for (run in found) {
            addStyle(run.style, run.innerStart, run.innerEnd)
        }
        // 刪標記：同一處可能被多層標記包到，合併區間後**從尾端往前刪**
        val ranges = ArrayList<IntRange>()
        for (run in found) {
            // openEnd／closeEnd 是 exclusive 端點（substring 用的那個），IntRange 是包含式：
            // 直接塞進去會「多圈一格」，下面 last+1 再刪就吃掉標記後面那個**真字元**
            //（用戶 2026-10-09 兩張截圖：28 個 1 加底線後只剩 27 個——「就是缺字」）
            ranges += IntRange(run.openStart, run.openEnd - 1)
            ranges += IntRange(run.closeStart, run.closeEnd - 1)
        }
        val merged = mergeRanges(ranges)
        for (i in merged.indices.reversed()) {
            val range = merged[i]
            replace(range.first, range.last + 1, "")
        }
    }
}

/** 一對標記：標記本身的四個端點＋內層範圍與樣式（全部是**原文**座標）。 */
private class MarkupRun(
    val openStart: Int,
    val openEnd: Int,
    val closeStart: Int,
    val closeEnd: Int,
    val innerStart: Int,
    val innerEnd: Int,
    val style: SpanStyle,
)

private class MarkupMarker(val open: String, val close: String, val style: SpanStyle)

/**
 * `***` 一定要排在 `**` 前面，`**` 排在 `*` 前面：一律**先配最長的**。
 *
 * 少了 `***` 這一條，「先底線、再粗體、再斜體」叠出來的 `***字***` 會被拆成
 * 「粗體包住 `*字`」＋「斜體包住 `字`」，刪標記時兩層端點互相錯開，
 * 結果粗體不見、還多吐一個星號在尾巴上（用戶 2026-10-09：「粗體丟了，
 * 再粗體的話就露原形 * 了」）。
 */
private val ComposerMarkers = listOf(
    MarkupMarker("***", "***", SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)),
    MarkupMarker("**", "**", SpanStyle(fontWeight = FontWeight.Bold)),
    MarkupMarker("<u>", "</u>", SpanStyle(textDecoration = TextDecoration.Underline)),
    MarkupMarker("~~", "~~", SpanStyle(textDecoration = TextDecoration.LineThrough)),
    MarkupMarker("*", "*", SpanStyle(fontStyle = FontStyle.Italic)),
)

/**
 * 找出「整段草稿只剩空殼標記」時那些標記的區間；不是整段空殼就回空清單（不動它）。
 *
 * 這是為了清**殘留**：選一段字按底線→`<u>字</u>`，把字刪光，草稿裡就只剩
 * `<u></u>`；`***abc***` 刪掉 `abc` 就剩 `******`。Telegram 的格式是掛在文字
 * 區間上的，區間縮成零點、格式自己就沒了；我們的標記是實心字元，不會跟著消失，
 * 於是輸入框裡留一排星號（用戶 2026-10-09：「刪了測試字符之後還有殘留」）。
 *
 * 為什麼只管「整段都是空殼」這一種：更激進的規則（看見 `<u></u>` 就拿掉，不管
 * 前後有沒有別的字）會把**手打** markdown 打死——手打 `**粗**` 是先敲四個星號
 * 再回去補字的樣子，中途就會被自己刪掉。整段空殼才是「把測試字刪光」那個動作
 * 的獨有形狀，誤傷面最小。
 */
internal fun markupResidueRanges(text: String): List<IntRange> {
    if (text.isEmpty()) return emptyList()
    val dead = BooleanArray(text.length)   // true ＝這個字是空殼標記的一部分
    var changed = true
    while (changed) {
        changed = false
        for (marker in ComposerMarkers) {
            // 單字元標記（`*`、`~`）一律不参与：手打 `**粗**` 的中間態就是兩顆 `*`，
            // 把它當成「空的斜體對」刪掉，等於不讓人用鍵盤打粗體。要收的殘留
            // 至少是 `****`／`<u></u>` 這種兩格以上的殼，單字元那層留給使用者自己處理。
            if (marker.open.length < 2 && !marker.open.startsWith("<")) continue
            var i = 0
            while (i + marker.open.length + marker.close.length <= text.length) {
                if (text.startsWith(marker.open, i)) {
                    // 內層可以不是零寬——只要裡面全是要拿掉的標記，外層也算空殼
                    //（`<u>****</u>` 就是靠這條連 `<u>` 一起收掉）
                    var j = i + marker.open.length
                    while (j < text.length && dead[j]) j++
                    if (text.startsWith(marker.close, j)) {
                        val end = j + marker.close.length
                        var grew = false
                        for (k in i until end) if (!dead[k]) { dead[k] = true; grew = true }
                        if (grew) {
                            changed = true
                            i = end
                            continue
                        }
                    }
                }
                i++
            }
        }
    }
    // 還有活著的字 ＝ 他還在寫別的東西，別亂動
    if (dead.any { !it }) return emptyList()
    return mergeRanges(
        ArrayList<IntRange>().apply {
            var start = -1
            for (k in dead.indices) {
                if (dead[k] && start < 0) start = k
                if (!dead[k] && start >= 0) { add(IntRange(start, k - 1)); start = -1 }
            }
            if (start >= 0) add(IntRange(start, dead.size - 1))
        },
    )
}

/**
 * 找出所有成對標記。規則與 `MarkdownToHtml.isWrappable()` 一致：
 * 同一行內、成對閉合、內層非空且頭尾不是空白；**未閉合一律當普通文字**
 *（否則會變成「編輯時看得到格式、發出去卻是標籤字」）。
 * 圍欄／行首引用／連結是多行或行級結構，這裡刻意不碰。
 */
private fun findMarkupRuns(source: String): List<MarkupRun> {
    val out = ArrayList<MarkupRun>()
    var i = 0
    while (i < source.length) {
        val marker = ComposerMarkers.firstOrNull { source.startsWith(it.open, i) }
        if (marker != null) {
            val innerStart = i + marker.open.length
            val closeAt = indexOfWithin(source, marker.close, innerStart)
            val inner = if (closeAt >= 0) source.substring(innerStart, closeAt) else null
            if (inner != null && inner.canBeFormatted()) {
                out += MarkupRun(
                    openStart = i,
                    openEnd = innerStart,
                    closeStart = closeAt,
                    closeEnd = closeAt + marker.close.length,
                    innerStart = innerStart,
                    innerEnd = closeAt,
                    style = marker.style,
                )
                // 從**內層開頭**繼續掃，不是跳到這一對的結尾之後：
                // `<u>**111**</u>` 這種巢狀，跳外層結尾會讓內層的 `**` 留在畫面上
                //（用戶 2026-10-09 #49：「我一疊加就又現原形了，但確實不丟字了」）。
                // 不會無限迴圈：canBeFormatted 要求內層非空，i 至少前進一格。
                i = innerStart
                continue
            }
        }
        i++
    }
    return out
}

private fun indexOfWithin(source: String, needle: String, from: Int): Int {
    val at = source.indexOf(needle, from)
    return if (at >= 0 && source.substring(from, at).indexOf('\n') < 0) at else -1
}

private fun String.canBeFormatted(): Boolean =
    isNotEmpty() && first() != ' ' && last() != ' ' && indexOf('\n') < 0

/** 合併重疊／相鄰的區間（巢狀標記會共用端點，合併後才不會刪到錯位）。 */
private fun mergeRanges(ranges: List<IntRange>): List<IntRange> {
    if (ranges.isEmpty()) return emptyList()
    val sorted = ranges.sortedBy { it.first }
    val out = ArrayList<IntRange>()
    var current = sorted.first()
    for (next in sorted.drop(1)) {
        current = if (next.first <= current.last + 1) {
            IntRange(current.first, maxOf(current.last, next.last))
        } else {
            out += current
            next
        }
    }
    out += current
    return out
}
