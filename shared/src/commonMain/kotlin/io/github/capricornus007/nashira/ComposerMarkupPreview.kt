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
            ranges += IntRange(run.openStart, run.openEnd)
            ranges += IntRange(run.closeStart, run.closeEnd)
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

/** `**` 必須排在 `*` 前面：先配長的，否則 `**粗**` 會被拆成兩個空斜體。 */
private val ComposerMarkers = listOf(
    MarkupMarker("**", "**", SpanStyle(fontWeight = FontWeight.Bold)),
    MarkupMarker("<u>", "</u>", SpanStyle(textDecoration = TextDecoration.Underline)),
    MarkupMarker("~~", "~~", SpanStyle(textDecoration = TextDecoration.LineThrough)),
    MarkupMarker("*", "*", SpanStyle(fontStyle = FontStyle.Italic)),
)

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
                i = closeAt + marker.close.length
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
