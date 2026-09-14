package io.github.capricornus007.nashira

import com.sun.net.httpserver.HttpServer
import io.github.capricornus007.nashira.matrix.MatrixEngine
import io.ktor.http.encodeURLParameter
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import java.net.InetAddress
import java.net.InetSocketAddress

/**
 * Desktop SSO：應用內連結優先。
 *
 * 主路徑（nashira:// scheme）：redirectUrl 是 nashira://sso/callback。瀏覽器
 * 認證完成後跳轉 scheme 連結，xdg-open 依 .desktop 的 MimeType 啟動本程式，
 * 次實例把 URL 經 [DesktopSingleInstance] 的本地 socket 轉發給正在跑的主實例。
 * 沒有 localhost HTTP server——不佔埠、不逾時自殺、地址欄不殘留 token URL。
 *
 * 回退路徑（loopback HTTP）：發行包的 .desktop 沒註冊 scheme 時（例如
 * jpackage 直出的 deb/rpm）才走，僅供兜底；PKGBUILD 安裝的包一律有 MimeType。
 */
actual suspend fun startSsoLogin(homeserver: String): Result<Unit>? {
    if (isSchemeHandlerRegistered()) return startSsoLoginViaScheme(homeserver)
    return startSsoLoginViaLoopback(homeserver)
}

/**
 * xdg-mime 查得到 x-scheme-handler/nashira 才走 scheme 主路徑；查不到（未裝
 * .desktop、xdg-mime 不存在、非 Linux 桌面環境）就回退 loopback。
 */
private fun isSchemeHandlerRegistered(): Boolean = runCatching {
    val process = ProcessBuilder("xdg-mime", "query", "default", "x-scheme-handler/nashira")
        .redirectErrorStream(true)
        .start()
    val output = process.inputStream.bufferedReader().readText().trim()
    process.waitFor()
    output.isNotBlank()
}.getOrDefault(false)

private suspend fun startSsoLoginViaScheme(homeserver: String): Result<Unit>? {
    val callback = "nashira://sso/callback"
    openLink("$homeserver/_matrix/client/v3/login/sso/redirect?redirectUrl=${callback.encodeURLParameter()}")

    // scheme 方案下 app 端零資源佔用，瀏覽器分頁停留多久都行；15 分鐘是
    // 「使用者八成已放棄」的量級，逾時訊息會留在登入頁上。
    val callbackData = DesktopSingleInstance.awaitSsoCallback(timeoutMillis = 15 * 60 * 1000L)
    return if (callbackData != null) {
        MatrixEngine.loginWithToken(homeserver, callbackData.first)
    } else {
        Result.failure(IllegalStateException("SSO login timed out"))
    }
}

/**
 * Loopback 回退（與 2026-09 之前的主路徑完全相同）：臨時 HTTP server 接
 * loginToken，5 分鐘逾時。只在 scheme 未註冊時兜底。
 */
private suspend fun startSsoLoginViaLoopback(homeserver: String): Result<Unit>? {
    val server = HttpServer.create(InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0)
    val port = server.address.port
    val callback = "http://127.0.0.1:$port/callback"

    val loginTokenReceived = CompletableDeferred<String>()
    server.createContext("/callback") { exchange ->
        val loginToken = exchange.requestURI.query
            ?.split("&")
            ?.firstOrNull { it.startsWith("loginToken=") }
            ?.substringAfter("loginToken=")
        val ok = loginToken != null
        if (loginToken != null) loginTokenReceived.complete(loginToken)
        val body = (if (ok) "Login successful. You can close this tab and return to Nashira." else "Missing loginToken.").toByteArray()
        exchange.sendResponseHeaders(if (ok) 200 else 400, body.size.toLong())
        exchange.responseBody.use { it.write(body) }
    }
    server.start()

    openLink("$homeserver/_matrix/client/v3/login/sso/redirect?redirectUrl=${callback.encodeURLParameter()}")

    return try {
        val loginToken = withTimeoutOrNull(5 * 60 * 1000L) { loginTokenReceived.await() }
        if (loginToken != null) {
            MatrixEngine.loginWithToken(homeserver, loginToken)
        } else {
            Result.failure(IllegalStateException("SSO login timed out"))
        }
    } finally {
        server.stop(0)
    }
}
