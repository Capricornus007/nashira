package io.github.capricornus007.nashira

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.contextmenu.builder.TextContextMenuBuilderScope
import androidx.compose.foundation.text.contextmenu.builder.item
import androidx.compose.foundation.text.input.TextFieldState
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

/**
 * 把六項格式化追加到**文字欄自己那個**右鍵選單尾端（系統項與自訂項同清單、同主題）。
 *
 * 教訓（2026-09-25 用戶截圖）：第一版是自己掛 `pointerInput` 收右鍵、再開一個
 * `DropdownMenu`，結果桌面 BasicTextField 內建的選單（剪下／複製／貼上／全部選取）
 * 照樣彈出來——同一個位置疊兩層選單，而且內建那層是淺底色，在深色主題裡特別突兀。
 * 正解是 `Modifier.appendTextContextMenuComponents`：只有一個選單，且手機端的
 * 文字工具列（TextToolbar）也走同一份清單，不用另外做入口。
 */
@OptIn(ExperimentalFoundationApi::class)
fun TextContextMenuBuilderScope.appendComposerFormatItems(strings: Strings, enabled: Boolean, onPick: (ComposerFormat) -> Unit) {
    separator()
    ComposerFormat.entries.forEach { format ->
        // 參數一律用位置傳：順序是 (key, label, enabled, leadingIcon, onClick)
        item("nashira.format.${format.name}", format.label(strings), enabled, null) { onPick(format) }
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
