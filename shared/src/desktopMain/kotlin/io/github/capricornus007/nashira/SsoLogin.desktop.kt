package io.github.capricornus007.nashira

import com.sun.net.httpserver.HttpServer
import io.github.capricornus007.nashira.matrix.MatrixEngine
import io.ktor.http.encodeURLParameter
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import java.net.InetAddress
import java.net.InetSocketAddress

/**
 * Desktop SSO：起 localhost loopback HTTP server（只綁 127.0.0.1，不對外），
 * 開瀏覽器到 homeserver SSO redirect（redirectUrl 指向本機 callback），
 * 等瀏覽器帶 loginToken 回來後呼叫 loginWithToken 交換。
 *
 * 5 分鐘逾時：使用者可能在瀏覽器裡猶豫或關掉分頁，不能讓 server 永久佔著 port。
 */
actual suspend fun startSsoLogin(homeserver: String): Result<Unit>? {
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
