package io.github.capricornus007.nashira

/**
 * 中英文之間自動補一個細空格（pangu 化）。
 *
 * 用戶 2026-10-07 定案：**顯示與複製都要加**、**不留開關、默認啟用、不能關閉**。
 *
 * 為「顯示」和「複製」必須走同一個函式：訊息文字在畫面上是 AnnotatedString，
 * 它的 span（連結、粗體、自訂表情）記的是**字元位置**。若事後對整條字串插字，
 * 所有 span 會一起錯位（點連結會選錯範圍）。所以只能在「逐段 append」的時候插，
 * 也就是呼叫方每送一段文字就順便把上一段的尾字元交回來。
 *
 * 用 U+2009 細空格而不是普通空格：跟 Nagram 一致，視覺上更像排版而非內容；
 * 也因為普通空格會被「已經打過空格」的訊息重複疊加時看起來更髒。
 */

/** 細空格。 */
private const val GAP = '\u2009'

/** CJK 漢字（含擴展 A 與兼容表意文字）＋日文假名。韓文谚文不算：它跟拉丁之間本來就有分詞習慣。 */
private fun Char.isCjk(): Boolean =
    code in 0x4E00..0x9FFF || code in 0x3400..0x4DBF || code in 0xF900..0xFAFF ||
        code in 0x3040..0x30FF

/** 拉丁字母／數字，且不是 CJK。 */
private fun Char.isLatinWord(): Boolean =
    // 上限 0x3000 是關鍵：`isLetterOrDigit()` 把**谚文、全形字母與全形數字**也算成「字母或數字」，
    // 不排掉就會在「한국어中文」中間插空格、把「第１００名」拆開——那些文字自己就有分詞規則。
    // 0x3000 以下剩下的就是 ASCII 與 Latin-1／Latin Extended（é、ñ 這類），正是要插空格的對象。
    this.isLetterOrDigit() && !isCjk() && code < 0x3000

/** 緊貼在拉丁詞後面的半形標點：這些後面若又是漢字，就表示這個拉丁詞是「掛在標點上」的。 */
private const val CLITIC_PUNCTUATION = ",.;:!?)]}"

/**
 * 對一段文字做 pangu 化，並回報最後一個字元讓呼叫方帶進下一段。
 *
 * `prev` 是上一段的最後字元（第一段傳 null）。不帶它就會漏掉
 * `<b>Chrome</b>瀏覽器` 這種被標籤切斷的邊界。
 *
 * 標點不處理：只在「漢字 ↔ 拉丁/數字」之間插，且兩邊已有空白或已經是細空格就不插。
 * 程式碼塊不該經過這裡（呼叫方負責繞開）。
 */
fun pangu(text: String, prev: Char?): Pair<String, Char?> {
    if (text.isEmpty()) return "" to prev
    val out = StringBuilder(text.length + 4)
    val gapAt = ArrayList<Int>(4)
    var last = prev
    for (c in text) {
        val needGap = last != null && last != GAP && c != GAP &&
            c != ' ' && c != '\n' && c != '\t' && last != ' ' && last != '\n' && last != '\t' &&
            ((last.isCjk() && c.isLatinWord()) || (last.isLatinWord() && c.isCjk()))
        if (needGap) {
            gapAt.add(out.length)
            out.append(GAP)
        }
        out.append(c)
        last = c
    }
    // 撤掉「只有左邊有、右邊沒有」的那種：拉丁詞後面**緊貼半形標點**、標點後面又是漢字時，
    // 前面插一個空格會變成「热搜 app,找了」——左右不對稱，看著像打字打錯
    //（用戶 2026-10-07 點名的正是這一個）。這種情況整團留緊，一個空格都不插。
    for (i in gapAt.indices.reversed()) {
        val at = gapAt[i]
        if (runGluedToPunctuation(out, at)) out.deleteCharAt(at)
    }
    return out.toString() to last
}

/** [GAP] 之後那個拉丁詞是否緊貼半形標點、而標點後面又是漢字。 */
private fun runGluedToPunctuation(out: StringBuilder, gapIndex: Int): Boolean {
    var i = gapIndex + 1
    while (i < out.length && out[i].isLatinWord()) i++
    if (i + 1 >= out.length) return false     // 拉丁詞一路到段尾，跨段情況交給下一段的 prev
    return out[i] in CLITIC_PUNCTUATION && out[i + 1].isCjk()
}

/** 整段一次做完（沒有跨段問題的地方用，例如複製到剪貼簿）。 */
fun pangu(text: String): String = pangu(text, null).first
