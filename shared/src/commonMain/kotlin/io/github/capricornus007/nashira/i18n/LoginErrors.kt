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
        msg.contains("M_FORBIDDEN") || msg.contains("M_USER_NOT_FOUND") || msg.contains("403") ->
            "帳號或密碼不正確"
        msg.contains("M_LIMIT_EXCEEDED") ->
            "嘗試次數太多，請稍後再試"
        msg.contains("M_UNKNOWN_TOKEN") || msg.contains("M_UNAUTHORIZED") ->
            "登入已過期，請重新登入"
        msg.contains("M_UNSUPPORTED_ROOM_VERSION") ->
            "這個房間的版本不支援"
        else -> msg.ifBlank { t::class.simpleName ?: "未知的錯誤" }
    }
}
