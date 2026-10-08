package io.github.capricornus007.nashira

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import io.github.capricornus007.nashira.matrix.MediaSource
import io.github.capricornus007.nashira.matrix.TokenStorage
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
 * - 未經驗證的舊端點 `/_matrix/media/v3/download/…` 在 matrix.org 與 elv.sh 都是 **404**，
 *   帶憑證的 `/_matrix/client/v1/media/download/…` 未帶憑證回 **401**。
 *   也就是說「把 mxc 換成公開網址丟給播放器」這條捷徑，在這些伺服器上根本走不通。
 * - 但憑證**不能放到 ffmpeg 的命令列**上：`/proc/<pid>/cmdline` 是本機任何程序都讀得到的，
 *   等於把他的長效登入憑證貼在門上。所以憑證只留在這個 JVM 裡，
 *   外面的程序只看到一個沒有秘密的本機網址。
 *
 * 安全邊界：只綁 127.0.0.1；路徑嚴格比對 `<host>/<id>` 字元集，上游位址永遠是
 * 「自己已登入的家伺服器＋固定路徑」，不會變成開放代理（SSRF）。
 *
 * ⚠️ 需要 `jdk.httpserver` 模組（已加進 `desktopApp/build.gradle.kts` 的 `modules()`）。
 */
internal actual fun mediaStreamUrl(source: MediaSource, userId: String?): String? = when (source) {
    is MediaSource.Plain -> mxcParts(source.mxcUrl)?.let { (host, id) -> MediaStreamProxy.endpoint(host, id, userId) }

    // 加密的影片要先解密才有檔，沒有「直接串」這回事
    is MediaSource.Encrypted -> null
}

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

    fun endpoint(host: String, id: String, userId: String?): String? {
        val listen = ensure(userId) ?: return null
        return "http://127.0.0.1:$listen/media/$host/$id"
    }

    @Synchronized
    private fun ensure(userId: String?): Int? {
        server?.let { return port }
        val stored = runCatching { TokenStorage().load() }.getOrNull()
        val token = stored?.accessToken
        val base = stored?.baseUrl?.trimEnd('/')?.takeIf { it.isNotEmpty() }
        if (token == null || base == null || stored == null) {
            mediaProbe("串流代理：拿不到登入憑證或家伺服器位址，退回整檔下載")
            return null
        }
        // 磁碟上只存最後登入的那個帳號的憑證；拿它去播別的帳號的房間會撞 401，
        // 與其讓畫面卡成「轉圈」，不如在這裡就認輸、走整檔下載那條路。
        if (userId != null && stored.userId.isNotBlank() && !stored.userId.equals(userId, ignoreCase = true)) {
            mediaProbe("串流代理：憑證帳號與目前帳號不同，退回整檔下載")
            return null
        }
        return runCatching {
            val created = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
            created.createContext("/media", Relay(base, token))
            // 一條幫浦執行緒跟著一條 ffmpeg 跑到影片結束，數量不可預測 → 快取型＋全 daemon，
            // 才不會因為一個没收干净的連接把整個 JVM 留在世上。
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

    /** 請求原樣轉給家伺服器（含 Range），再照原樣把狀態碼與區間標頭吐回給 ffmpeg。 */
    private class Relay(private val baseUrl: String, private val token: String) : HttpHandler {
        override fun handle(exchange: HttpExchange) {
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
                // 401/403/404 這種「憑證或權限」問題一定要留一行，否則畫面只是轉圈，
                // 誰也不知道是憑證錯、房子錯、還是網路錯（用戶 2026-10-08 點名過的「點了沒反應」）。
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
                // ffmpeg 被殺（暫停、拖動、關掉檢視窗）時會从這裡掉下來，屬正常收尾
                mediaProbe("串流代理中斷：${e::class.simpleName} $path")
            } finally {
                runCatching { connection?.disconnect() }
                exchange.close()
            }
        }
    }
}
