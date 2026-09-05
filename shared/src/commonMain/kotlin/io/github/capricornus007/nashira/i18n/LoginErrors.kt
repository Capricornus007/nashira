package io.github.capricornus007.nashira.i18n

/**
 * 把底層異常翻成人話。原則：一眼能懂、告訴用戶能做什麼；
 * 原始訊息拿不出人話時才原樣顯示（並保留偵錯線索）。
 */
fun friendlyError(t: Throwable): String {
    val msg = t.message.orEmpty()
    return when {
        // ktor CIO 在 keep-alive 連線被伺服器關閉時讀到 EOF
        msg.contains("Not enough data available") ->
            "跟伺服器的連線中斷了，請再試一次（若反覆出現請檢查網路）"
        msg.contains("Unexpected JSON token") || msg.contains("JsonDecodingException") ->
            "伺服器回傳了看不懂的資料，請回報這個問題"
        t is java.net.UnknownHostException ->
            "找不到伺服器：檢查 Homeserver 網址與網路"
        t is java.net.ConnectException ->
            "連不上伺服器：檢查網路，或伺服器暫時離線"
        t is java.net.SocketTimeoutException || msg.contains("timeout", ignoreCase = true) ->
            "伺服器回應逾時，請再試一次"
        msg.contains("SSL", ignoreCase = true) || msg.contains("certificate", ignoreCase = true) ->
            "加密連線失敗：伺服器憑證可能有問題"
        msg.contains("M_TOO_LARGE") ->
            "檔案太大，伺服器拒收"
        // matrix.org 實測回應：403 {"errcode":"M_USER_LIMIT_EXCEEDED",
        // "error":"Media upload limit exceeded"} —— 是伺服器的上傳額度，不是帳密也不是網路
        msg.contains("M_USER_LIMIT_EXCEEDED") ->
            "伺服器的媒體上傳額度已用盡，過一段時間再試"
        // M_FORBIDDEN 在登入以外的地方是「伺服器不允許這個操作」——最常見的是
        // matrix.org 的媒體上傳配額。以前一律翻成「帳號或密碼不正確」，發圖失敗時
        // 會顯示成密碼錯誤，誤導得很嚴重。
        msg.contains("M_FORBIDDEN") || msg.contains("403") ->
            "伺服器拒絕了這個操作：權限不足或已超出配額"
        msg.contains("M_LIMIT_EXCEEDED") ->
            "嘗試次數太多，請稍後再試"
        msg.contains("M_UNKNOWN_TOKEN") || msg.contains("M_UNAUTHORIZED") ->
            "登入已過期，請重新登入"
        msg.contains("M_UNSUPPORTED_ROOM_VERSION") ->
            "這個房間的版本不支援"
        else -> msg.ifBlank { t::class.simpleName ?: "未知的錯誤" }
    }
}

/**
 * 登入表單專用：這個情境下 `M_FORBIDDEN` / `M_USER_NOT_FOUND` 真的就是帳密不對。
 * 其他情境一律走 [friendlyError]。
 */
fun friendlyLoginError(t: Throwable): String {
    val msg = t.message.orEmpty()
    if (msg.contains("M_FORBIDDEN") || msg.contains("M_USER_NOT_FOUND")) return "帳號或密碼不正確"
    return friendlyError(t)
}
