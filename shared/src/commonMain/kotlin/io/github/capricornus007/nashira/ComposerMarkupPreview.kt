package io.github.capricornus007.nashira

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

/**
 * 輸入框的**即時格式化預覽**：草稿存的仍是帶標記的原文（`**粗**`、`<u>底</u>`），
 * 顯示時讓標記「看不見」、內層套上真正的樣式——Telegram 的輸入框就是這個樣子
 *（用戶 2026-10-08 兩度點名：「這種可視化是做不了嗎？」「可視化依舊沒做好」）。
 *
 * 關鍵取捨：**不刪標記，把它畫成透明**。
 * 這樣輸出字串與原文**等長**，視覺索引＝原文索引，游標、拖藍、退格、全選複製
 * 全都不需要換算（框架也不用猜）；一旦改成「吃掉標記」，就得自己維護對照表，
 * 錯一格就是「看得到底線、點一下刪錯字」。密碼框能把整串換成圓點也是靠等長這件事。
 * 代價：用方向鍵橫越標記時游標會在看不見的字元上停一兩下——Telegram 同樣如此。
 *
 * 入口是 state 版輸入框的 `outputTransformation`
 *（androidx `compose/foundation/.../text/BasicTextField.kt:176`、`:225`）。
 * ⚠️ **不是** `visualTransformation`——那個參數只屬於舊的 `value/onValueChange` 多載，
 * 掛到這個多載上編譯直接報「No parameter with name 'visualTransformation' found」（實測踩過）。
 */
internal val ComposerMarkupPreview = OutputTransformation {
    // 接收者是 TextFieldBuffer：`originalText` 就是草稿目前的原文，直接在它上面蓋樣式。
    // 官方註解兩件事對我們最關鍵：游標與選取由 BasicTextField 自己管，
    // 而且「這個 buffer 裡改選取沒有作用」——所以我們只准動視覺，不動內容。
    markRanges(originalText.toString(), 0, length, this)
}

/** 只認我們自己的格式鈕會插的四對標記；`**` 一定要排在 `*` 前面（先配長的）。 */
private class MarkupMarker(val open: String, val close: String, val style: SpanStyle)

private val ComposerMarkers = listOf(
    MarkupMarker("**", "**", SpanStyle(fontWeight = FontWeight.Bold)),
    MarkupMarker("<u>", "</u>", SpanStyle(textDecoration = TextDecoration.Underline)),
    MarkupMarker("~~", "~~", SpanStyle(textDecoration = TextDecoration.LineThrough)),
    MarkupMarker("*", "*", SpanStyle(fontStyle = FontStyle.Italic)),
)

/** 標記字元本身：顏色透明，但仍佔位（等長的原則就在這裡守住）。 */
private val InvisibleMarker = SpanStyle(color = Color.Transparent)

/** 草稿有沒有我們那四對標記；沒有就別構造換算器。 */
internal fun draftHasComposerMarkup(text: String): Boolean =
    text.indexOf('*') >= 0 || text.indexOf('~') >= 0 || text.contains("<u>")

/**
 * 在 [from, to) 裡找成對標記：標記本身蓋透明、內層蓋樣式，然後**遞迴進內層**
 *（`<u>**粗的底線**</u>` 這種巢狀才會兩層都對）。
 *
 * 判定跟 `MarkdownToHtml.isWrappable()` 同一條規矩：未閉合、跨行、內層為空或頭尾是空白
 * 一律**當普通文字**——否則會變成「編輯時看得到底線、發出去卻是標籤字」，那比不顯示更糟。
 * 圍欄、行首引用、連結是多行／行級結構，這裡刻意不碰。
 */
private fun markRanges(source: String, from: Int, to: Int, buffer: TextFieldBuffer) {
    var i = from
    while (i < to) {
        val marker = ComposerMarkers.firstOrNull { source.startsWith(it.open, i) }
        if (marker != null) {
            val innerStart = i + marker.open.length
            val closeAt = indexOfWithin(source, marker.close, innerStart, to)
            val inner = if (closeAt >= 0) source.substring(innerStart, closeAt) else null
            if (inner != null && inner.canBeFormatted()) {
                buffer.addStyle(InvisibleMarker, i, innerStart)
                buffer.addStyle(InvisibleMarker, closeAt, closeAt + marker.close.length)
                buffer.addStyle(marker.style, innerStart, closeAt)
                markRanges(source, innerStart, closeAt, buffer)
                i = closeAt + marker.close.length
                continue
            }
        }
        i++
    }
}

private fun indexOfWithin(source: String, needle: String, from: Int, to: Int): Int {
    val at = source.indexOf(needle, from)
    return if (at >= 0 && at + needle.length <= to) at else -1
}

private fun String.canBeFormatted(): Boolean =
    isNotEmpty() && first() != ' ' && last() != ' ' && indexOf('\n') < 0
