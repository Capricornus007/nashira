package io.github.capricornus007.nashira

/** 一段 HTML 切出來的結果：`isCode` 為真時這段是要獨立成塊的 `<pre>` 內容。 */
internal typealias HtmlChunk = Pair<Boolean, String>

/**
 * 把 `<pre>…</pre>` 從 HTML 裡挖出來當獨立區塊，其餘留給行內解析器。
 * 先剝 `<mx-reply>`：那是「回覆來源」的複製內容，裡頭的 `<pre>` 若跟著挖出來，
 * 會把本來該隱掉的引用又變回一塊看得見的程式碼。
 *
 * 這幾個函數刻意不沾 Compose：純字串處理，抽出來才能不啟動 UI 就單測。
 */
internal fun splitHtmlCodeBlocks(html: String): List<HtmlChunk> {
    val source = MxReplyBlockRegex.replace(html, "")
    val out = ArrayList<HtmlChunk>()
    var pos = 0
    for (match in PreBlockRegex.findAll(source)) {
        if (match.range.first > pos) out += false to source.substring(pos, match.range.first)
        out += true to match.groupValues[1]
        pos = match.range.last + 1
    }
    val tail = source.substring(pos)
    // 橋接器送來的 HTML 未必閉合（Element 的解析器也是把沒關的 <pre> 一路收到結尾）
    val open = OpenPreRegex.find(tail)
    if (open == null) {
        if (tail.isNotEmpty()) out += false to tail
    } else {
        if (open.range.first > 0) out += false to tail.substring(0, open.range.first)
        out += true to tail.substring(open.range.last + 1)
    }
    return out
}

/** `<pre>` 裡頭通常還包一層 `<code>`；標籤先剝、實體後解，順序不能反。 */
internal fun codeTextOf(innerHTML: String): String =
    decodeHtmlEntities(HTML_TAG_REGEX.replace(innerHTML, "")).removeSuffix("\n")

/**
 * 回覆的引用塊：Matrix 把它包在 `<mx-reply>…</mx-reply>` 裡，內容是「被回覆那則」的
 * 副本（訊息上方另有 ↩ 那一列呈現，所以這段不進內文）。裡面**一定**有一顆
 * matrix.to 連結（「In reply to …」），那不是你貼的連結——連結偵測與 `<pre>`
 * 切段都要先剝掉它。
 */
internal val MxReplyBlockRegex = Regex("(?is)<mx-reply>.*?</mx-reply>")

private val PreBlockRegex = Regex("(?is)<pre[^>]*>(.*?)</pre>")
private val OpenPreRegex = Regex("(?is)<pre[^>]*>")
private val HTML_TAG_REGEX = Regex("(?is)</?[a-z][^>]*>")
