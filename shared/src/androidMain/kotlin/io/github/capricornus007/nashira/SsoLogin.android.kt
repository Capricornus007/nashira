package io.github.capricornus007.nashira

import io.ktor.http.encodeURLParameter

/**
 * Android SSO：開瀏覽器到 homeserver SSO redirect，callback 是 nashira:// deep link。
 * 登入完成後由 MainActivity 的 intent filter 接收 callback 並呼叫 loginWithToken，
 * 所以這裡只負責開瀏覽器，回傳 null（結果由 MainActivity 那邊處理）。
 */
actual suspend fun startSsoLogin(homeserver: String): Result<Unit>? {
    val callback = "${NashiraUri.scheme}://${NashiraUri.ssoHost}/callback"
    openLink("$homeserver/_matrix/client/v3/login/sso/redirect?redirectUrl=${callback.encodeURLParameter()}")
    return null
}
