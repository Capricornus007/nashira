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
        t is java.net.UnknownHostException || msg.contains("Unable to resolve host") ->
            "找不到伺服器：檢查 Homeserver 網址與網路"
        t is java.net.ConnectException || msg.contains("Failed to connect to") ->
            "連不上伺服器：檢查網路，或伺服器暫時離線"
        t is java.net.SocketTimeoutException || msg.contains("timeout", ignoreCase = true) ->
            "伺服器回應逾時，請再試一次"
        // 登入憑證失效這一路**必須排在「伺服器憑證可能有問題」之前**（#75 的診斷就靠這兩條分開講）：
        // 伺服器那句 "The access token has expired" 同時含 token 與 expired，
        // 排在後面會被憑證那條的 "expired" 搶走，把「你要重新登入」報成「對方伺服器憑證壞了」。
        isCredentialMessage(msg) ->
            "登入憑證已失效，請重新登入"
        // 憑證這一路要放寬：#75 要拿它判斷「待送卡住是不是伺服器憑證的問題」，
        // 而各家 JVM／ktor 丟出來的字串差很多——
        // `SSLHandshakeException`、`CertificateExpiredException`（…not valid yet／has expired）、
        // `SunMSCAPI`／PKIX 的「unable to find valid certification path」、自簽憑證的
        // `CERT_PATH_BUILT_IN_UNTRUSTED_ROOT` 都可能。只配 "SSL"／"certificate" 會漏掉
        // 「PKIX」「validity」這幾個純大寫術語，回落到最後那條把原始英文丟給用戶。
        msg.contains("SSL", ignoreCase = true) ||
            msg.contains("certificate", ignoreCase = true) ||
            msg.contains("PKIX", ignoreCase = true) ||
            msg.contains("CertPath", ignoreCase = true) ||
            msg.contains("TrustAnchor", ignoreCase = true) ||
            msg.contains("handshake", ignoreCase = true) ||
            msg.contains("expired", ignoreCase = true) ||
            msg.contains("not yet valid", ignoreCase = true) ->
            "加密連線失敗：伺服器憑證可能有問題（過期或不受信任）"
        msg.contains("M_TOO_LARGE") ->
            "檔案太大，伺服器拒收"
        // matrix.org 實測回應：403 {"errcode":"M_USER_LIMIT_EXCEEDED",
        // "error":"Media upload limit exceeded"} —— 是伺服器的上傳額度，不是帳密也不是網路
        msg.contains("M_USER_LIMIT_EXCEEDED") ->
            "伺服器的媒體上傳額度已用盡，過一段時間再試"
        // M_FORBIDDEN 在登入以外的地方是「伺服器不允許這個操作」——最常見的是
        // matrix.org 的媒體上傳配額。以前一律翻成「帳號或密碼不正確」，發圖失敗時
        // 會顯示成密碼錯誤，誤導得很嚴重。
        // 但這句翻法本身也會吃人：它把伺服器**自己寫的原因**丟掉了。用戶 2026-10-08
        // 刪自己一則訊息失敗，看到的就只有「權限不足或已超出配額」，無從判斷到底是
        // 功率等級、配額、還是房間版本——所以這裡把原話摳出來附在後面（#90 同一條教訓：
        // 先讓錯誤說真話，再修）。
        // 「403」要用**獨立數字**去配：事件 ID、位元組數裡出現 403 這三個連號很常見，
        // 用 contains("403") 會把別的錯誤翻成「權限不足」（#90 那條「先讓錯誤說真話」的延伸）。
        msg.contains("M_FORBIDDEN") || HTTP_403.containsMatchIn(msg) -> {
            // 有原話就只講原話；摳不到原話時**把原始回應貼出來**，
            // 不要再退回那句我自己編的「權限不足或已超出配額」——用戶 2026-10-08 裝了
            // 0.1.88 再刪一次，看到的還是那句舊話，代表 Trixnity 丟出來的字串裡
            // 根本沒有 `"error"` 欄位可摳。那種情況下唯一誠實的做法是把原文給他看。
            val reason = serverReason(msg)
            if (reason != null) "伺服器拒絕了這個操作（伺服器原話：$reason）"
            else "伺服器拒絕了這個操作（伺服器原始回應：${msg.take(300).ifBlank { "空" }}）"
        }
        msg.contains("M_LIMIT_EXCEEDED") ->
            "嘗試次數太多，請稍後再試"
        msg.contains("M_UNSUPPORTED_ROOM_VERSION") ->
            "這個房間的版本不支援"
        // 桌面 SSO 的 nashira:// 接收端缺席（SsoLogin.desktop.kt 丟的代碼）。
        // 這裡不翻成「登入失敗」：問題在操作系統沒註冊網址處理器，跟帳號無關。
        msg.contains("NASHIRA_SSO_SCHEME_MISSING") ->
            "這個系統找不到 nashira:// 連結的接收端，自動註冊也沒成功。請改用安裝版（deb／rpm／pkg）啟動一次，或手動把 nashira.desktop 設為該網址協定的預設程式"
        else -> msg.ifBlank { t::class.simpleName ?: "未知的錯誤" }
    }
}

/** 獨立的 403（前後不是數字），避免命中事件 ID／位元組數裡的「403」。 */
private val HTTP_403 = Regex("""(^|[^0-9])403([^0-9]|$)""")

/** 獨立的 401，理由同 HTTP_403：光配「401 這三個連號」會誤傷事件 ID 與位元組數。 */
private val HTTP_401 = Regex("""(^|[^0-9])401([^0-9]|$)""")

/**
 * 「這是**登入憑證**的問題，不是網路、也不是對方伺服器的 TLS 憑證」的判定。
 * [friendlyError] 與 #75 的待送診斷共用這一份：兩邊說法不能各講各的
 * （一邊說「連線逾時」、一邊說「你要重新登入」是最壞的那種）。
 */
private fun isCredentialMessage(msg: String): Boolean =
    msg.contains("M_UNKNOWN_TOKEN") || msg.contains("M_UNAUTHORIZED") ||
        msg.contains("soft_logout", ignoreCase = true) ||
        msg.contains("invalid token", ignoreCase = true) ||
        msg.contains("access token", ignoreCase = true) ||
        // 只有「401」這個數不夠：得連著 unauthorized 字樣才算，否則訊息裡隨便一個
        // 位元組數／事件 ID 含 401 就會把用戶趕去重新登入。
        (HTTP_401.containsMatchIn(msg) && msg.contains("unauthorized", ignoreCase = true))

/** [isCredentialMessage] 的例外版：待送診斷（SendHealthProbe）拿它決定要不要叫用戶重登。 */
fun isCredentialFailure(t: Throwable): Boolean = isCredentialMessage(t.message.orEmpty())

/**
 * 從伺服器的原始回應裡摳出它**自己寫的那句原因**（JSON 的 `"error"` 欄）。
 * 摳不到就回 null，讓呼叫端退回一般說法——寧可模糊也別編一個原因出來。
 */
private fun serverReason(raw: String): String? {
    val key = "\"error\""
    val at = raw.indexOf(key)
    if (at < 0) return null
    val colon = raw.indexOf(':', at + key.length)
    if (colon < 0) return null
    val quote = raw.indexOf('"', colon + 1)
    if (quote < 0) return null
    val out = StringBuilder()
    var i = quote + 1
    while (i < raw.length) {
        val c = raw[i]
        when {
            c == '\\' && i + 1 < raw.length -> { out.append(raw[i + 1]); i += 2 }
            c == '"' -> break
            else -> { out.append(c); i++ }
        }
    }
    return out.toString().trim().takeIf { it.isNotEmpty() }
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
