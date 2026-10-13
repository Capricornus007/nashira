package io.github.capricornus007.nashira

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle

/**
 * 搜尋結果預覽的**位置計算**（與渲染無關，所以能單獨探針驗）。
 *
 * 回傳 `Pair(顯示用的文字, 命中位置)`：文字是原訊息把換行摺成空白後、圍繞第一個命中截出來
 * 的一段，前後被截掉時各補一個 `…`；`second` 那些區間是**已經換算到這段文字上**的偏移，
 * 直接拿去套高亮即可。
 *
 * 為什麼各步要這樣做：
 * - **不截前 60 字**（`bodyPreview` 那麼截）：搜尋的意義就是「我看到命中在哪」，
 *   命中在第 300 字時前 60 字是空的，列表整列都是沒有線索的開場白。
 * - **換行摺成空白**：一列只顯示一兩行，但命中可能在第二行，只取 first line 會整筆找不到。
 * - **不 pangu**：偏移是在原文上算的，插了細空格就對不上（#20 同一個障碍）；
 *   預覽列本來就只有一行，電報那邊也是原文。
 */
internal fun searchPreviewSlice(
    text: String,
    term: String,
    window: Int = SearchPreviewWindow,
    lead: Int = SearchPreviewLead,
): Pair<String, List<IntRange>> {
    val flat = text.replace('\n', ' ').replace('\r', ' ')
    val needle = term.trim()
    val matches = if (needle.isEmpty()) emptyList() else allOccurrences(flat, needle)
    // 沒命中（媒體類型名、或搜尋詞已改掉）就老老實實顯示開頭，不硬湊視窗
    val start = ((matches.firstOrNull()?.first ?: 0) - lead).coerceIn(0, flat.length)
    val end = (start + window).coerceAtMost(flat.length)
    val body = flat.substring(start, end)
    val shown = buildString {
        if (start > 0) append('…')
        append(body)
        if (end < flat.length) append('…')
    }
    val shift = if (start > 0) 1 else 0
    // 命中剛好被視窗右緣切到時，只標到看得見的那半、且**不含**結尾的 `…`（不然會-highlight-到省略號）
    val visible = body.length + shift
    val hits = matches.mapNotNull { range ->
        if (range.last < start || range.first >= end) return@mapNotNull null
        (range.first - start + shift) until (range.last + 1 - start + shift).coerceAtMost(visible)
    }
    return shown to hits
}

/** 最多顯示多少字。比電報的「在對話中搜尋」（約 80～100 字）略寬，因為我們這一列是整幅寬。 */
internal const val SearchPreviewWindow = 140

/** 第一個命中前面保留多少字，讓它不至於貼在左緣。 */
internal const val SearchPreviewLead = 40

/**
 * 把 [searchPreviewSlice] 的結果套上高亮，組成一列要顯示的 [AnnotatedString]。
 * [highlight] 由呼叫端從主題取，這裡不碰 MaterialTheme（才能維持純函式）。
 */
internal fun searchResultPreview(text: String, term: String, highlight: SpanStyle): AnnotatedString {
    val (shown, hits) = searchPreviewSlice(text, term)
    return AnnotatedString.Builder().apply {
        var cursor = 0
        for (range in hits) {
            if (range.first > cursor) append(shown.substring(cursor, range.first))
            withStyle(highlight) { append(shown.substring(range.first, range.last + 1)) }
            cursor = range.last + 1
        }
        if (cursor < shown.length) append(shown.substring(cursor))
    }.toAnnotatedString()
}

/** 大小寫不敏感的**全部**出現位置（由左往右、不重疊）。 */
private fun allOccurrences(haystack: String, needle: String): List<IntRange> {
    val out = mutableListOf<IntRange>()
    var from = 0
    while (true) {
        val at = haystack.indexOf(needle, from, ignoreCase = true)
        if (at < 0) return out
        out += at until at + needle.length
        from = at + needle.length
    }
}
