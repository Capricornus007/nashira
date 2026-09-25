package io.github.capricornus007.nashira

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextRange
import io.github.capricornus007.nashira.i18n.Strings

/**
 * 輸入框的格式化動作——插入的是 Markdown 標記，發出去時由 [markdownToHtml]
 * 換成 `org.matrix.custom.html`。
 *
 * 項目與順序照 Element Web 的 composer format bar（`Formatting` 列舉：
 * bold／italics／strikethrough／code／quote／insert_link）。它沒有底線鍵、
 * 也沒有清單鍵，我們就不自己加。
 */
enum class ComposerFormat {
    Bold,
    Italics,
    Strikethrough,
    CodeBlock,
    Quote,
    Link,
    ;

    fun label(strings: Strings): String = when (this) {
        Bold -> strings.formatBold
        Italics -> strings.formatItalics
        Strikethrough -> strings.formatStrikethrough
        CodeBlock -> strings.formatCodeBlock
        Quote -> strings.formatQuote
        Link -> strings.formatLink
    }
}

/** 選單裡的六列。`enabled=false`（輸入框是空的）時整排灰色、點了沒反應。 */
@Composable
fun ComposerFormatItems(strings: Strings, enabled: Boolean, onPick: (ComposerFormat) -> Unit) {
    ComposerFormat.entries.forEach { format ->
        ContextMenuItem(format.label(strings), enabled = enabled) { onPick(format) }
    }
}

/** 套用到草稿上。無選取時游標停在標記中間，接著打字就自带格式（Element 同款行為）。 */
fun applyComposerFormat(state: TextFieldState, format: ComposerFormat) {
    when (format) {
        ComposerFormat.Bold -> state.wrapWith("**", "**")
        ComposerFormat.Italics -> state.wrapWith("*", "*")
        ComposerFormat.Strikethrough -> state.wrapWith("~~", "~~")
        ComposerFormat.CodeBlock -> state.wrapWith("```\n", "\n```")
        ComposerFormat.Quote -> state.toggleQuote()
        ComposerFormat.Link -> state.wrapLink()
    }
}

/** 連結：插入 `[文字](https://)` 後把選取停在網址上，接著打字就直接蓋掉它。 */
private fun TextFieldState.wrapLink() {
    val start = selection.min
    val end = selection.max
    val inner = text.substring(start, end)
    val urlStart = start + 1 + inner.length + 2 // `[` + 文字 + `](`
    edit {
        replace(start, end, "[$inner](https://)")
        selection = TextRange(urlStart, urlStart + "https://".length)
    }
}

private fun TextFieldState.wrapWith(prefix: String, suffix: String) {
    val start = selection.min
    val end = selection.max
    val inner = text.substring(start, end)
    edit {
        replace(start, end, prefix + inner + suffix)
        // 選取範圍跟著搬：同一鍵再按一次是換標記，不是把游標丟到尾巴
        selection = TextRange(start + prefix.length, start + prefix.length + inner.length)
    }
}

/**
 * 引用是行首標記，所以整段逐行加 `> `；這段已經全是引用就反向取消
 * （Element 的 quote 鍵也是開關，不是只加不減）。
 */
private fun TextFieldState.toggleQuote() {
    val full = text.toString()
    val from = if (selection.min == 0) 0 else full.lastIndexOf('\n', selection.min - 1) + 1
    // 選到行尾的換行時不要把下一行也拖進來
    val selectedEnd = if (selection.max > from && full[selection.max - 1] == '\n') selection.max - 1 else selection.max
    val blockEnd = full.indexOf('\n', maxOf(from, selectedEnd)).takeIf { it >= 0 } ?: full.length
    val lines = full.substring(from, blockEnd).split('\n')
    val alreadyQuoted = lines.all { it.isBlank() || it.matches(QuotedLine) }
    val replacement = lines.joinToString("\n") { if (alreadyQuoted) it.replace(QuotedLine, "") else "> $it" }
    edit {
        replace(from, blockEnd, replacement)
        selection = TextRange(from, from + replacement.length)
    }
}

private val QuotedLine = Regex("""^>\s?""")
