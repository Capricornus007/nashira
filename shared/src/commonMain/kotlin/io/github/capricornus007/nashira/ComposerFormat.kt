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
 * bold／italics／strikethrough／code／quote／insert_link）。
 *
 * **底線是這輪補上的例外**（用戶 2026-10-07 拿 Telegram 的右鍵選單點名「你要不仔細看看」）：
 * Element 的舊格式條確實沒有底線鍵，但「底線」本身不是自創——
 * spec 的建議放行清單裡就有 `u`；Element Web 的 WYSIWYG 模式發的就是原生 `<u>`
 * （matrix-rich-text-editor `container_node.rs:1083` 那句註解：
 * “Underline format is absent from Markdown. Let's use raw HTML.”）；
 * 渲染端 Element Web（sanitize-html 清單含 `u`）、Element X（`TAG_U -> TextDecoration.Underline`）、
 * Nheko、FluffyChat 全部吃得下，我們自己的 `htmlToRichText` 也早就認 `u`。
 * ⚠️ 千萬別改成 `__文字__`：CommonMark 把雙下劃線當**粗體**（Element 的測試檔
 * `Markdown-test.ts:186` 就寫著 `__not__` → `<strong>not</strong>`）。
 */
enum class ComposerFormat {
    Bold,
    Italics,
    Strikethrough,
    Underline,
    CodeBlock,
    Quote,
    Link,
    ;

    fun label(strings: Strings): String = when (this) {
        Bold -> strings.formatBold
        Italics -> strings.formatItalics
        Strikethrough -> strings.formatStrikethrough
        Underline -> strings.formatUnderline
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
fun TextContextMenuBuilderScope.appendComposerFormatItems(
    strings: Strings,
    onPick: (ComposerFormat) -> Unit,
) {
    separator()
    ComposerFormat.entries.forEach { format ->
        // 真實簽名是 item(key, label, leadingIcon = 0, onClick)，沒有 enabled 這一欄
        // （反編譯 foundation-desktop-1.12.0 核過）；「空時灰色」做不到，改成空時整組不出現。
        item("nashira.format.${format.name}", format.label(strings)) { onPick(format) }
    }
}

/** 套用到草稿上。無選取時游標停在標記中間，接著打字就自带格式（Element 同款行為）。 */
fun applyComposerFormat(state: TextFieldState, format: ComposerFormat) {
    when (format) {
        // 粗體與斜體共用同一個 `*`，不能各套各的 wrapWith（見 toggleEmphasis）
        ComposerFormat.Bold -> state.toggleEmphasis(bold = true)
        ComposerFormat.Italics -> state.toggleEmphasis(bold = false)
        ComposerFormat.Strikethrough -> state.wrapWith("~~", "~~")
        // 底線沒有 Markdown 寫法，插的是原生 `<u>`／`</u>`（與 Element WYSIWYG 同一產出）；
        // 送出時 `markdownToHtml` 原樣放行，`body` 那側會把標籤去掉（見 stripMarkupForBody）。
        ComposerFormat.Underline -> state.wrapWith("<u>", "</u>")
        ComposerFormat.CodeBlock -> state.wrapWith("```\n", "\n```")
        ComposerFormat.Quote -> state.toggleQuote()
        ComposerFormat.Link -> state.wrapLink()
    }
}

/**
 * 粗體／斜體的開關。
 *
 * 為什麼不能沿用 `wrapWith`：兩個格式共用 `*` 這個字元，`wrapWith` 只看選取外面
 * 一格，於是「底線→粗體→斜體」的第三步會把 `**` 的右半顆當成自己的斜體標記，
 * 判定「已經是斜體了」而**把粗體拆掉**（用戶 2026-10-09：「在我先底線再粗體
 * 再斜體的時候粗體丟了，再粗體的話就露原形 * 了」）。
 *
 * 正解是把兩側的 `*` 連續長度當成「目前有哪些格式」來讀，開關請求的那一項，
 * 再照新的組合寫回：1＝斜體、2＝粗體、3＝粗體＋斜體、0＝都沒有。
 * 這樣 `**字**` 按斜體得到 `***字***`（粗體留著），再按一次斜體退回 `**字**`，
 * 而 `***字***` 按粗體會得到 `*字*`——跟 Telegram 的行為一致。
 */
private fun TextFieldState.toggleEmphasis(bold: Boolean) {
    val start = selection.min
    val end = selection.max
    val full = text.toString()
    var runStart = start
    while (runStart > 0 && full[runStart - 1] == '*') runStart--
    var runEnd = end
    while (runEnd < full.length && full[runEnd] == '*') runEnd++
    val run = start - runStart
    val marker = if (bold) "**" else "*"
    // 兩側不對稱、或多到 4 顆以上（那是他自己打的星號，不是我們疊出來的格式）：
    // 老實包一層，不拆任何東西
    if (run != runEnd - end || run > 3) {
        wrapWith(marker, marker)
        return
    }
    var hasBold = run == 2 || run == 3
    var hasItalic = run == 1 || run == 3
    if (bold) hasBold = !hasBold else hasItalic = !hasItalic
    val stars = when {
        hasBold && hasItalic -> 3
        hasBold -> 2
        hasItalic -> 1
        else -> 0
    }
    val inner = full.substring(start, end)
    edit {
        replace(runStart, runEnd, "*".repeat(stars) + inner + "*".repeat(stars))
        val from = runStart + stars
        selection = TextRange(from, from + inner.length)
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
    val full = text.toString()
    val inner = full.substring(start, end)
    // 再按一次是**取消**，不是繼續疊（用戶 2026-10-08 按了五下底線，
    // 框裡變成 `<u><u><u><u><u>你好</u></u></u></u></u>`）。
    // 判別條件：選取範圍正好被同一對標記夾著（套完之後選取就停在標記內側，
    // 所以「同一處再按一次」一定命中這裡）。Telegram／Element 的格式鈕都是開關。
    if (start >= prefix.length && end + suffix.length <= full.length &&
        full.substring(start - prefix.length, start) == prefix &&
        full.substring(end, end + suffix.length) == suffix
    ) {
        edit {
            replace(start - prefix.length, end + suffix.length, inner)
            selection = TextRange(start - prefix.length, start - prefix.length + inner.length)
        }
        return
    }
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
