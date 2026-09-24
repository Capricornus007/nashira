package io.github.capricornus007.nashira.emoji

/**
 * 一個 Unicode 分類。[icon] 是 tab 與節標用的 glyph——**故意不帶分類名稱**：
 * 九個分類×六種語言＝54 個翻譯鍵，而 Discord/Telegram 的分類條本來就只有圖標
 * 沒有文字，用 glyph 當節標同時解決了「本地化」與「分隔線」兩件事。
 */
data class EmojiCategory(val group: Int, val icon: String)

/**
 * 表情表的查詢與分類入口。所有計算都是記憶體線性掃描（1914 筆），
 * 沒必要建倒排索引；呼叫端把結果 `remember` 住就好。
 */
object EmojiIndex {

    /** 分類 → 代表 glyph（Unicode 官方分類的頭一個表情，一眼認得出來）。 */
    private val ICONS = mapOf(
        0 to "😀", // smileys & emotion
        1 to "👋", // people & body
        3 to "🐍", // animals & nature
        4 to "🍔", // food & drink
        5 to "✈️", // travel & places
        6 to "⚽", // activities
        7 to "💡", // objects
        8 to "❤️", // symbols
        9 to "🏁", // flags
    )

    /** 出錯也不讓 UI 空白的後備 glyph。 */
    private const val FALLBACK_ICON = "❓"

    val categories: List<EmojiCategory> by lazy {
        EmojiTable.all.map { it.group }.distinct().sorted().map { group ->
            EmojiCategory(group, ICONS[group] ?: EmojiTable.byGroup[group]?.firstOrNull()?.glyph ?: FALLBACK_ICON)
        }
    }

    private val byHexcode: Map<String, EmojiEntry> by lazy {
        // 同一 hexcode 理論上唯一；真的重複時留第一筆
        EmojiTable.all.associateBy { it.hexcode }
    }

    fun byHexcode(hex: String): EmojiEntry? = byHexcode[hex]

    /** 依存進設定的順序（最近在前）還原成條目，查不到的舊值直接丟掉。 */
    fun byHexcodes(hexes: List<String>): List<EmojiEntry> =
        hexes.mapNotNull { byHexcode[it] }

    /**
     * tokens 是空白分隔的詞串（裡面也有 `heart-eyes` 這種複合詞）。拉丁詞要求
     * 出現位置的**左鄰不是字母**：右邊允許前綴。
     * ·「cat」不再命中 "intoxi**cat**ed"／"compli**cat**ed"（實測踩到過，搜尋品質
     *   直接崩壞——左鄰是 i／c 都是字母，擋掉）
     * ·「eyes」仍能命中 "heart-eyes"（左鄰是連字號，放行）
     * CJK 沒有空格可切，維持子串比對——搜「心」命中「傷心」是對的行為。
     */
    private fun hasWord(hay: String, word: String): Boolean {
        val latin = word.isNotEmpty() && word.all {
            it.code < 128 && (it.isLetterOrDigit() || it == '-' || it == '_')
        }
        if (!latin) return hay.contains(word)
        var from = 0
        while (true) {
            val i = hay.indexOf(word, from)
            if (i < 0) return false
            if (i == 0 || !hay[i - 1].isLetter()) return true
            from = i + 1
        }
    }

    /**
     * 搜尋：空白分詞後**每個詞都要命中**（AND）。
     *
     * tokens 裡混了英文名、英文 tags、`:shortcode:`、繁中名稱與繁中標籤，
     * 所以「笑臉」「smile」「heart」「thumbs up」都能中。
     *
     * 排序：名稱完全相同 → 名稱前綴 → 其他命中；同分維持表內順序
     * （表已按分類＋Unicode 順序排，stable sort 剛好保留）。
     */
    fun search(query: String, limit: Int = 200): List<EmojiEntry> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return emptyList()
        val lower = trimmed.lowercase()
        val words = lower.split(' ', '\t', '\n').filter { it.isNotBlank() }
        if (words.isEmpty()) return emptyList()
        val first = words.first()
        val scored = ArrayList<Pair<Int, EmojiEntry>>()
        for (entry in EmojiTable.all) {
            val hay = entry.tokens
            if (words.all { hasWord(hay, it) }) {
                val score = when {
                    entry.nameEn.equals(trimmed, ignoreCase = true) || entry.nameZh == trimmed -> 0
                    entry.nameEn.lowercase().startsWith(first) || entry.nameZh.startsWith(trimmed) -> 1
                    else -> 2
                }
                scored += score to entry
            }
        }
        return scored.sortedBy { it.first }.asSequence().map { it.second }.take(limit).toList()
    }
}
