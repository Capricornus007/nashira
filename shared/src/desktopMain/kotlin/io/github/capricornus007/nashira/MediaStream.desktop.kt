package io.github.capricornus007.nashira

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import de.connect2x.trixnity.client.MatrixClient
import de.connect2x.trixnity.client.store.AuthenticationStore
import io.github.capricornus007.nashira.matrix.MediaSource
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.URI
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * 桌面：**本機媒體代理**。把「要憑證才拿得到的家伺服器媒體」轉成一個 ffmpeg 能直接串的
 * `http://127.0.0.1:<port>/media/<伺服器>/<mediaId>`，於是影片可以邊抓邊播（秒開）。
 *
 * 存在的理由（2026-10-08 實測）：
 * - 未經驗證的舊端點 `/_matrix/media/v3/download/…` 在 matrix.org 是 **404**，
 *   `/_matrix/client/v1/media/download/…` 未帶憑證回 **401**。
 *   也就是說「把 mxc 換成公開網址丟給播放器」這條捷徑，在這類家伺服器上走不通。
 *   （但有些橋站如 t2bot.io 仍開著免驗證下載，實測回 206 且 `accept-ranges: bytes`，
 *   所以「原站網址」仍留作第二個候選——見 `EmbeddedVideoPlayer.desktop.kt`。）
 * - 憑證**不能放到 ffmpeg 的命令列**上：`/proc/<pid>/cmdline` 是本機任何程序都讀得到的。
 *   所以憑證只留在這個 JVM 裡，外面的程序只看到一個沒有秘密的本機網址。
 *
 * 憑證來源是 `client.di.get<AuthenticationStore>()` 裡那筆**活的** auth 資料
 * （實測：DB 內那把與 `session.properties` 那把 sha 不同，DB 才是續期後的活鑰匙）。
 * 第一版圖省事讀磁碟上的 `session.properties`，實測被 matrix.org 打回 401：
 * 那個檔案只在登入當下寫一次，Trixnity 換過 token 之後它就是作廢的舊鑰匙
 * （用戶 2026-10-08「卡在 0:00 不動」的根因）。
 *
 * 安全邊界：只綁 127.0.0.1；路徑嚴格比對 `<host>/<id>` 字元集，上游位址永遠是
 * 「自己已登入的家伺服器＋固定路徑」，不會變成開放代理（SSRF）。
 *
 * ⚠️ 需要 `jdk.httpserver` 模組（已加進 `desktopApp/build.gradle.kts` 的 `modules()`）。
 */
internal actual suspend fun mediaStreamUrl(client: MatrixClient, source: MediaSource): String? {
    if (source !is MediaSource.Plain) return null // 加密的影片要先解密才有檔，沒有「直接串」這回事
    val (host, id) = mxcParts(source.mxcUrl) ?: return null
    val token = liveAccessToken(client) ?: return null
    return MediaStreamProxy.endpoint(host, id, client.baseUrl.toString().trimEnd('/'), token)
}

/**
 * 向**活著的** client 要 access token：`client.di.get<AuthenticationStore>()` 裡那筆
 * `providerData`（Trixnity 自己序列化的登入資料）中的 `accessToken`。
 *
 * 為什麼不用磁碟上那份：`~/.nashira/session.properties` 只在登入當下寫一次
 * （`TokenStorage.save` 全專案只有登入流程兩個呼叫點），Trixnity 續期換掉的 token 不會寫回去，
 * 拿它去要媒體一律 401。實測過：DB 內那把與 `session.properties` 那把 sha 不同。
 *
 * 拿不到就回 null（呼叫端退回整檔下載），但**一定要留一行日誌說為什麼**——
 * 第一版只留一句「沒有可用的登入憑證」，害我多繞了一整輪去猜是帳號問題。
 */
private suspend fun liveAccessToken(client: MatrixClient): String? {
    // ⚠️ Trixnity 5.8.1 **沒有**把 `MatrixClientAuthProviderDataStore` 綁進 Koin——
    // 它是 `MatrixClient.create(...)` 方法本體裡的區域變數（查證：zipgrep 全 jar 只命中
    // `AuthenticationStoreMatrixClientAuthProviderDataStore` 與 `MatrixClientKt`，
    // 後者的 new/binding 都在 create 的區域槽，從未進任何 `module { single ... }`），
    // 所以 `di.get<MatrixClientAuthProviderDataStore>()` 必然拋 NoDefinitionFoundException。
    // 唯一綁得住的 key 是 `AuthenticationStore`（`CreateStoreModuleKt` 有 singleOf）。
    val result = runCatching {
        tokenOf(client.di.get<AuthenticationStore>().getAuthentication()?.providerData)
    }
    result.exceptionOrNull()?.let {
        mediaProbe("串流代理：AuthenticationStore 讀失敗 ${it::class.qualifiedName}，退回整檔下載")
        return null
    }
    val token = result.getOrNull()
    if (token == null) {
        mediaProbe("串流代理：活的憑證是空值，退回整檔下載")
        return null
    }
    mediaProbe("串流代理：取得活的憑證")
    return token
}

/**
 * 從 `Authentication.providerData` 取 `accessToken`。
 *
 * ⚠️ 欄位名是 **`accessToken`（駝峰）**，不是 Matrix 線上的 `access_token`：
 * 那欄存的是 Trixnity 自己的 `ClassicMatrixClientAuthProviderData` 序列化結果，
 * 常數池欄位名就是 baseUrl／accessToken／accessTokenExpiresInMs／refreshToken。
 * 我先前照協定找 `"access_token":"` → 永遠回 null、還不拋例外，
 * 日誌只顯示「都回空值」，多繞了一整輪（2026-10-08 實測）。
 * 另外這層 providerData 是**內層字串**、沒有再跳脫一次，所以不用處理 `\"`。
 */
private fun tokenOf(providerData: String?): String? = providerData
    ?.substringAfter("\"accessToken\":\"", "")
    ?.substringBefore('"')
    ?.takeIf { it.isNotEmpty() }

private fun mxcParts(mxcUrl: String): Pair<String, String>? {
    val rest = mxcUrl.removePrefix("mxc://")
    val host = rest.substringBefore("/", "")
    val id = rest.substringAfter("/", "")
    if (host.isEmpty() || id.isEmpty() || id.contains("/")) return null
    return host to id
}

private object MediaStreamProxy {
    private var server: HttpServer? = null
    private var port: Int = 0

    // 每次要網址時都刷新一組，代理**轉發當下**才讀這兩個欄位：
    // 伺服器只起一次，但帳號／續期後的憑證會變，不能把第一次的憑證凍在裡面。
    @Volatile
    private var baseUrl: String = ""

    @Volatile
    private var token: String = ""

    @Synchronized
    private fun ensure(): Int? {
        server?.let { return port }
        return runCatching {
            val created = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
            created.createContext("/media", Relay())
            // 一條幫浦執行緒跟著一條 ffmpeg 跑到影片結束，數量不可預測 → 快取型＋全 daemon，
            // 才不會因為一个没收干净的連接把整個 JVM 留在世上。
            created.executor = ThreadPoolExecutor(
                0,
                Int.MAX_VALUE,
                30L,
                TimeUnit.SECONDS,
                SynchronousQueue(),
                { runnable -> Thread(runnable, "nashira-stream").apply { isDaemon = true } },
            )
            created.start()
            server = created
            port = created.address.port
            mediaProbe("串流代理已啟動 127.0.0.1:$port")
            port
        }.onFailure { mediaProbe("串流代理啟動失敗：${it::class.simpleName} ${it.message}") }.getOrNull()
    }

    fun endpoint(host: String, id: String, baseUrl: String, token: String): String? {
        this.baseUrl = baseUrl
        this.token = token
        val listen = ensure() ?: return null
        return "http://127.0.0.1:$listen/media/$host/$id"
    }

    /** 請求原樣轉給家伺服器（含 Range），再照原樣把狀態碼與區間標頭吐回給 ffmpeg。 */
    private class Relay : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            val baseUrl = MediaStreamProxy.baseUrl
            val token = MediaStreamProxy.token
            val path = exchange.requestURI.path.removePrefix("/media/")
            if (!path.matches(Regex("[A-Za-z0-9.-]+/[A-Za-z0-9._-]+"))) {
                runCatching { exchange.sendResponseHeaders(400, -1L) }
                exchange.close()
                return
            }
            var connection: HttpURLConnection? = null
            try {
                val opened = URI("$baseUrl/_matrix/client/v1/media/download/$path").toURL()
                    .openConnection() as HttpURLConnection
                connection = opened
                opened.instanceFollowRedirects = true
                opened.connectTimeout = 10_000
                // 0＝不設讀取逾時：ffmpeg 加 `-re` 是按原速率取資料，上游幾秒不動很正常，
                // 設 30 秒會被自己的播放節奏掐死。
                opened.readTimeout = 0
                opened.setRequestProperty("Authorization", "Bearer $token")
                // 別讓上游 gzip：解壓後的 Content-Length 失去意義，ffmpeg 的位址推算會歪
                opened.setRequestProperty("Accept-Encoding", "identity")
                exchange.requestHeaders.getFirst("Range")?.let { opened.setRequestProperty("Range", it) }

                val status = opened.responseCode
                // 401/403 這種「憑證或權限」問題一定要留一行，否則畫面只是轉圈，
                // 誰也不知道是憑證錯、房子錯、還是網路錯（用戶 2026-10-08 #95 實測教訓）。
                if (status !in 200..299) mediaProbe("串流代理：上游回 $status（$path）")
                val body: InputStream? = if (status in 200..399) opened.inputStream else opened.errorStream
                val length = if (status in 200..299) opened.contentLength else -1
                exchange.responseHeaders.set("Accept-Ranges", "bytes")
                opened.getHeaderField("Content-Range")?.let { exchange.responseHeaders.set("Content-Range", it) }
                exchange.responseHeaders.set("Content-Type", opened.contentType ?: "application/octet-stream")
                // sendResponseHeaders 的長度：>0 照實給、0＝未知長度（chunked）、-1＝沒有本體
                exchange.sendResponseHeaders(status, if (body == null) -1L else length.toLong().coerceAtLeast(0L))
                if (body != null) {
                    val output = exchange.responseBody
                    val chunk = ByteArray(64 * 1024)
                    while (true) {
                        val read = body.read(chunk)
                        if (read <= 0) break
                        output.write(chunk, 0, read)
                    }
                    output.flush()
                }
            } catch (e: IOException) {
                // ffmpeg 被殺（暫停、拖動、關掉檢視窗）時會從這裡掉下來，屬正常收尾
                mediaProbe("串流代理中斷：${e::class.simpleName} $path")
            } finally {
                runCatching { connection?.disconnect() }
                exchange.close()
            }
        }
    }
}
