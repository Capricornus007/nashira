package io.github.capricornus007.nashira

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withTimeoutOrNull
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

/**
 * 桌面單實例＋nashira:// 連結接收器。
 *
 * SSO 用應用內連結（nashira:// scheme，與 Android deep link 同構）而不是
 * localhost loopback HTTP server——loopback 有兩個先天缺陷：
 * 1. 伺服器必須一直佔著 port，逾時一過（使用者在瀏覽器裡猶豫）callback
 *    就打進死埠，loginToken 憑白丟失（2026-09-14 桌面實測復現）；
 * 2. 瀏覽器地址欄會停在 http://127.0.0.1:PORT/callback?loginToken=…，
 *    這個 token URL 對使用者可見還會進瀏覽器歷史。
 *
 * 主實例綁 127.0.0.1:47832 常駐接收；.desktop 的 MimeType=
 * x-scheme-handler/nashira 讓 xdg-open 以 nashira://… 為參數啟動本程式，
 * 次實例 handleLaunch() 偵測到主實例在跑，把 URL 轉發過去後即刻退出——
 * 既完成 SSO 回調也順帶實現單實例（兩個實例共享同一個 Room DB 會互相踩）。
 *
 * token 緩衝：callback 到達但沒有人在 await（流程剛結束/逾時、或使用者
 * 手動重跑），token 存進 pending 槽——下一次 awaitSsoCallback 立即取走，
 * 不會因為時序錯開而白白丟掉伺服器已發出的單次性 token。
 */
object DesktopSingleInstance {
    private const val PORT = 47832
    private const val CONNECT_TIMEOUT_MS = 1500

    /** 任何 nashira:// 連結到達（Main 用來把視窗拉到前面）。 */
    private val linkEvents = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val ssoLinkEvents: SharedFlow<String> = linkEvents

    /** SSO 回調暫存槽（loginToken to baseUrl）。 */
    private var pendingCallback: CompletableDeferred<Pair<String, String>>? = null

    /**
     * main() 的第一件事。
     *
     * @return true＝以主實例身分繼續啟動；false＝已有主實例（URL 已轉發或僅
     *   重複啟動），進程應直接退出。
     */
    fun handleLaunch(args: Array<String>): Boolean {
        val urls = args.filter { it.startsWith("nashira://", ignoreCase = true) }
        if (tryForward(urls)) return false
        startServer()
        return true
    }

    /**
     * 等 SSO 回調登入。pending 槽裡已有 token 時立即返回（見類注釋的緩衝
     * 語義）；[timeoutMillis] 內沒等到回 null（呼叫端顯示逾時錯誤）。
     * scheme 方案下瀏覽器分頁可以停留任意久——app 沒有佔任何資源在等。
     */
    suspend fun awaitSsoCallback(timeoutMillis: Long): Pair<String, String>? {
        val deferred = synchronized(this) {
            pendingCallback ?: CompletableDeferred<Pair<String, String>>().also { pendingCallback = it }
        }
        val result = withTimeoutOrNull(timeoutMillis) { deferred.await() }
        if (result == null) {
            // 逾時：丟棄這個槽。之後到達的 token 會開新槽；殘留的舊槽
            // 沒有任何 awaiter，等它被覆蓋即可。
            synchronized(this) {
                if (pendingCallback === deferred && !deferred.isCompleted) pendingCallback = null
            }
        }
        return result
    }

    /** @return true＝有主實例接了連線（轉發成功或空 ping）。 */
    private fun tryForward(urls: List<String>): Boolean = runCatching {
        Socket().use { socket ->
            socket.connect(
                InetSocketAddress(InetAddress.getLoopbackAddress(), PORT),
                CONNECT_TIMEOUT_MS,
            )
            socket.getOutputStream().apply {
                if (urls.isEmpty()) write("\n".toByteArray()) // 空行＝純喚醒 ping
                else urls.forEach { write((it + "\n").toByteArray()) }
                flush()
            }
            // 給對端一點時間讀完再關；socket.close() 的 FIN 通常已足夠，
            // 但極快的關閉在少數核心上會截斷未讀緩衝。
            Thread.sleep(150)
        }
        true
    }.getOrDefault(false)

    private fun startServer() {
        val server = runCatching {
            ServerSocket(PORT, 50, InetAddress.getLoopbackAddress())
        }.getOrElse {
            // 極罕見：bind 失敗但 connect 也失敗（前實例正在關閉的窗口）。
            // 繼續啟動但收不到 scheme 連結——SSO 走 localhost 回退路徑。
            System.err.println("Nashira: single-instance server bind failed: $it")
            return
        }
        thread(isDaemon = true, name = "nashira-single-instance") {
            while (!server.isClosed) {
                val socket = runCatching { server.accept() }.getOrNull() ?: break
                thread(isDaemon = true, name = "nashira-sso-link") {
                    runCatching { handleConnection(socket) }
                }
            }
        }
    }

    private fun handleConnection(socket: Socket) {
        socket.use { s ->
            s.soTimeout = 3000
            BufferedReader(InputStreamReader(s.getInputStream(), Charsets.UTF_8)).useLines { lines ->
                for (raw in lines) {
                    val url = raw.trim()
                    if (!url.startsWith("nashira://", ignoreCase = true)) continue
                    linkEvents.tryEmit(url)
                    NashiraUri.parseSsoCallback(url)?.let { data ->
                        synchronized(this) {
                            (pendingCallback ?: CompletableDeferred<Pair<String, String>>().also { pendingCallback = it })
                                .complete(data)
                        }
                    }
                }
            }
        }
    }
}
