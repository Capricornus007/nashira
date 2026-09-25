package io.github.capricornus007.nashira

/**
 * 輸入框 Markdown → `org.matrix.custom.html`。
 *
 * 為什麼要有這個：格式化按鈕往文字裡插的是 `**粗體**` 這種標記，而 Matrix 的
 * 訊息有兩個欄位——`body`（純文字，給不認得 HTML 的客戶端）與 `formatted_body`
 * （HTML）。少了這層轉換，發出去的 `**x**` 在別家（含我們自己）就是字面上的星號。
 *
 * 支援的語法刻意**只限**於：
 * - ``` 圍起來的程式碼塊 → `<pre><code>`（圍欄上的語言名不保留：我們不做語法著色，
 *   而 `class` 屬性會被大多數客戶端的淨化器拿掉，留著只會誤導）
 * - 行首 `> ` → `<blockquote>`、`- `/`* `/`+ ` → `<ul><li>`、`1. ` → `<ol><li>`
 * - 行內 `` `code` ``、`**strong**`、`*em*`、`~~del~~`、`[文字](網址)`
 * - `\` 逃脫下一個標記字元
 *
 * **不做** `_底線_`／`__雙底線__`：底線在聊天裡大量出現在檔名與識別符
 * （`user_name_list`），跟著解析會把程式碼般的字串變成斜體，得不償失。
 * 也不做巢疊清單、標題列——那幾樣 Element 的格式化列也沒有。
 *
 * 回 `null` 代表「這則訊息根本沒有標記」，呼叫端就別附 `formatted_body`：
 * 每則訊息都掛 HTML 只會讓橋接器與舊客戶端多一條可能出錯的路徑。
 */
fun markdownToHtml(text: String): String? {
    if (!containsMarkdownMarkup(text)) return null

    val out = StringBuilder()
    val lines = text.split('\n')
    var i = 0
    val paragraph = ArrayList<String>()

    fun flushParagraph() {
        if (paragraph.isEmpty()) return
        out.append(paragraph.joinToString("<br>") { inlineMarkdownToHtml(it) })
        paragraph.clear()
    }

    while (i < lines.size) {
        val line = lines[i]
        val fence = FENCE_LINE.matchEntire(line.trimEnd())
        if (fence != null) {
            flushParagraph()
            val body = StringBuilder()
            i++
            while (i < lines.size && !FENCE_LINE.matches(lines[i].trim())) {
                if (body.isNotEmpty()) body.append('\n')
                body.append(lines[i])
                i++
            }
            if (i < lines.size) i++ // 吃掉結尾圍欄；沒閉合就一路收到最後一行（Element 同款退路）
            if (body.isNotEmpty()) out.append("<pre><code>").append(htmlEscape(body.toString())).append("</code></pre>\n")
            continue
        }
        if (QUOTE_LINE.matches(line.trimStart())) {
            flushParagraph()
            val quoted = ArrayList<String>()
            while (i < lines.size && QUOTE_LINE.matches(lines[i].trimStart())) {
                quoted += QUOTE_LINE.replaceFirst(lines[i].trimStart(), "")
                i++
            }
            out.append("<blockquote>").append(quoted.joinToString("<br>") { inlineMarkdownToHtml(it) }).append("</blockquote>\n")
            continue
        }
        val item = LIST_ITEM.matchEntire(line.trimStart())
        if (item != null) {
            flushParagraph()
            // 群組 1＝項目符號（- * +）、2＝編號（1.）、3＝內容；2 有值就是有序清單
            val ordered = item.groupValues[2].isNotEmpty()
            val items = ArrayList<String>()
            while (i < lines.size) {
                val next = LIST_ITEM.matchEntire(lines[i].trimStart()) ?: break
                if (next.groupValues[2].isNotEmpty() != ordered) break
                items += next.groupValues[3]
                i++
            }
            val tag = if (ordered) "ol" else "ul"
            out.append('<').append(tag).append('>')
            items.forEach { out.append("<li>").append(inlineMarkdownToHtml(it)).append("</li>") }
            out.append("</").append(tag).append(">\n")
            continue
        }
        paragraph += line
        i++
    }
    flushParagraph()

    val html = out.toString().trimEnd('\n')
    // 標記存在卻一個標籤都沒生成（例如只有 `\*\*` 這種被逃脫掉的），就回 null 當純文字發。
    return html.takeIf { it.contains('<') }
}

/** 快速決斷：值不值得跑一趟完整解析。 */
private fun containsMarkdownMarkup(text: String): Boolean =
    text.indexOf('*') >= 0 || text.indexOf('~') >= 0 || text.indexOf('`') >= 0 ||
        text.indexOf('[') >= 0 || text.contains("> ") || text.startsWith(">") ||
        text.contains("\n> ") || text.contains("\n- ") || text.startsWith("- ") ||
        text.contains("\n* ") || text.startsWith("* ") ||
        text.contains("\n+ ") || text.startsWith("+ ") || ORDERED_ITEM_START.matches(text) ||
        text.contains("\n1. ") || text.contains("\\*") || text.contains("\\`") || text.contains("\\~")

private val FENCE_LINE = Regex("^\\s*```.*$")
private val QUOTE_LINE = Regex("^>\\s?.*$")
private val LIST_ITEM = Regex("^\\s*(?:([-*+])|(\\d+\\.))\\s+(.*)$")
private val ORDERED_ITEM_START = Regex("^\\s*\\d+\\.\\s")

/** 行內標記：一趟掃描，`` ` `` 優先（程式碼裡的其他符號一律不算標記）。 */
private fun inlineMarkdownToHtml(raw: String): String {
    val out = StringBuilder(raw.length + 8)
    var i = 0
    while (i < raw.length) {
        val c = raw[i]
        when {
            c == '\\' && i + 1 < raw.length && raw[i + 1] in ESCAPABLE -> {
                out.append(htmlEscape(raw[i + 1].toString()))
                i += 2
            }
            c == '`' -> {
                val end = raw.indexOf('`', i + 1)
                val content = if (end < 0) null else raw.substring(i + 1, end)
                if (content == null || content.isEmpty()) { out.append('`'); i++ } else {
                    out.append("<code>").append(htmlEscape(content)).append("</code>")
                    i = end + 1
                }
            }
            c == '*' && raw.startsWith("**", i) -> {
                val end = raw.indexOf("**", i + 2)
                val content = if (end < 0) null else raw.substring(i + 2, end)
                if (content == null || !content.isWrappable()) { out.append("**"); i += 2 } else {
                    out.append("<strong>").append(emphasized(content)).append("</strong>")
                    i = end + 2
                }
            }
            c == '*' -> {
                val end = raw.indexOf('*', i + 1)
                val content = if (end < 0) null else raw.substring(i + 1, end)
                if (content == null || !content.isWrappable()) { out.append('*'); i++ } else {
                    out.append("<em>").append(emphasized(content)).append("</em>")
                    i = end + 1
                }
            }
            c == '~' && raw.startsWith("~~", i) -> {
                val end = raw.indexOf("~~", i + 2)
                val content = if (end < 0) null else raw.substring(i + 2, end)
                if (content == null || !content.isWrappable()) { out.append("~~"); i += 2 } else {
                    out.append("<del>").append(emphasized(content)).append("</del>")
                    i = end + 2
                }
            }
            c == '~' -> {
                val end = raw.indexOf('~', i + 1)
                val content = if (end < 0) null else raw.substring(i + 1, end)
                if (content == null || !content.isWrappable()) { out.append('~'); i++ } else {
                    out.append("<del>").append(emphasized(content)).append("</del>")
                    i = end + 1
                }
            }
            c == '[' -> {
                val close = raw.indexOf(']', i + 1)
                val link = if (close >= 0 && close + 1 < raw.length && raw[close + 1] == '(') {
                    raw.indexOf(')', close + 2)
                } else -1
                val label = if (link >= 0) raw.substring(i + 1, close) else null
                val url = if (link >= 0) raw.substring(close + 2, link).trim() else null
                val safe = url?.takeIf { isSafeLinkTarget(it) }
                if (label == null || safe == null) {
                    out.append(htmlEscape(c.toString())); i++
                } else {
                    out.append("<a href=\"").append(htmlAttribute(safe)).append("\">")
                        .append(emphasized(label)).append("</a>")
                    i = link + 1
                }
            }
            else -> {
                out.append(htmlEscape(c.toString()))
                i++
            }
        }
    }
    return out.toString()
}

/** 標記內側的片段：還要繼續吃下一層（`` ` `` 與連結），但不再巢疊 `*`。 */
private fun emphasized(text: String): String = inlineMarkdownToHtml(text)

/**
 * 這段能不能算作強調：空、或頭尾是空白就不算。
 * 少了這個判斷，`2 * 3 * 4` 會被解析成「3」變斜體（commonmark 同樣拒絕這種寫法）。
 */
private fun String.isWrappable(): Boolean = isNotEmpty() && first() != ' ' && last() != ' '

/** HTML 文字節點逃脫（`& < >`；換行不在此處理，呼叫端負責 `<br>`）。 */
internal fun htmlEscape(text: String): String = buildString(text.length + 8) {
    for (c in text) {
        when (c) {
            '&' -> append("&amp;")
            '<' -> append("&lt;")
            '>' -> append("&gt;")
            else -> append(c)
        }
    }
}

private fun htmlAttribute(text: String): String = htmlEscape(text).replace("\"", "&quot;")

/** 只放行會話裡真的點得開的協定；`javascript:` 之類的網址一律不當連結。 */
private val SAFE_LINK_PREFIXES = listOf("https://", "http://", "matrix.to/", "mxc://", "mailto:", "matrix:")

private fun isSafeLinkTarget(url: String): Boolean =
    url.none { it == ' ' || it == '"' || it == '<' || it == '>' || it == '`' } &&
        SAFE_LINK_PREFIXES.any { url.startsWith(it, ignoreCase = true) }

private val ESCAPABLE = charArrayOf('*', '~', '`', '[', ']', '<', '>')
