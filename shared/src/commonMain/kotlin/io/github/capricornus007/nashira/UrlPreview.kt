package io.github.capricornus007.nashira

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.HttpClientConfig
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.readRemaining
import io.ktor.utils.io.core.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

/** P5-3：連結預覽資料（og meta 提取；暫不含縮圖，控制流量與延遲）。 */
data class UrlPreviewData(
    val url: String,
    val siteName: String?,
    val title: String?,
    val description: String?,
)

/** 進程內快取：同一連結一次會話只抓一次；失敗的連結也記住，避免反覆打。 */
object UrlPreviewCache {
    private val data = java.util.concurrent.ConcurrentHashMap<String, UrlPreviewData?>()
    private val failed = java.util.Collections.newSetFromMap(java.util.concurrent.ConcurrentHashMap<String, Boolean>())

    operator fun get(url: String): UrlPreviewData? = if (failed.contains(url)) null else data[url]

    fun hasFailed(url: String): Boolean = failed.contains(url)

    fun put(url: String, preview: UrlPreviewData?) {
        if (preview == null) failed.add(url) else data[url] = preview
    }
}

private val previewEngine: HttpClientEngine? =
    io.github.capricornus007.nashira.matrix.platformHttpEngine()

private fun previewClientConfig(): HttpClientConfig<*>.() -> Unit = {
    expectSuccess = false
    followRedirects = true
}

private val previewClient by lazy {
    // engine 為 null 時 ktor 自選平台預設引擎
    previewEngine?.let { HttpClient(it, previewClientConfig()) } ?: HttpClient(previewClientConfig())
}

private val URL_REGEX = Regex("""https?://[^\s<>"')\]]+""")
private val META_REGEX = Regex(
    """<meta[^>]+(?:property|name)=["'](?:og:|twitter:)(title|description|site_name)["'][^>]*content=["']([^"']*)["']""",
    RegexOption.IGNORE_CASE,
)
private val TITLE_REGEX = Regex("""<title[^>]*>([^<]*)</title>""", RegexOption.IGNORE_CASE)

// 回覆的引用塊：Matrix 把它包在 <mx-reply>…</mx-reply> 裡，裡面**一定**有一個
// matrix.to 連結（「In reply to …」那顆）。它不是你貼的連結。
private val MX_REPLY_REGEX = Regex("(?is)<mx-reply>.*?</mx-reply>")

/**
 * 取「使用者自己貼進訊息裡」的連結（去重、依出現順序），用來決定要掛幾張 og 預覽卡。
 *
 * 兩件事是踩過的坑：
 * ① 必須先拔掉 `<mx-reply>` 區塊再找。之前直接把整段 formatted_body 丟進 regex，
 *    結果**回覆訊息一律會冒出一張 matrix.org 的預覽卡**，即使訊息文字裡一個連結
 *    都沒有（用戶 2026-09-25 點名：「它這根本就沒發鏈接」）。
 * ② 一則訊息帶多個連結時全部顯示（Element 就是一張連結一張卡），不是只挑第一個。
 *    上限 3 張：橋接機器人常把同一頁的兩三個連結都列出來，再多就蓋掉對話本身。
 */
fun urlsInMessage(formattedBody: String?, plainBody: String): List<String> {
    val haystack = formattedBody?.replace(MX_REPLY_REGEX, "") ?: plainBody
    return URL_REGEX.findAll(haystack)
        .map { it.value.trimEnd(',', '.', ';', ':', '!', '?') }
        .distinct()
        .take(3)
        .toList()
}

/** 抓取並解析 og meta。任何失敗都回 null（呼叫端只顯示空白，不打擾）。 */
suspend fun fetchUrlPreview(url: String): UrlPreviewData? = withContext(Dispatchers.Default) {
    try {
        withTimeout(8_000) {
            val body = previewClient.prepareGet(url) {
                header(HttpHeaders.Accept, "text/html,application/xhtml+xml;q=0.8,*/*;q=0.4")
                header(HttpHeaders.AcceptLanguage, "zh-TW,en;q=0.5")
            }.execute { response ->
                if (!response.status.value.let { it in 200..399 }) return@execute null
                val channel = response.bodyAsChannel()
                // 只讀前 96KB——og meta 都在 <head>，不必下載整頁
                val limited = channel.readRemaining(96 * 1024).readBytes().decodeToString()
                limited
            } ?: return@withTimeout null

            val metas = META_REGEX.findAll(body).associate { it.groupValues[1].lowercase() to it.groupValues[2] }
            val title = metas["title"]?.takeIf { it.isNotBlank() }
                ?: TITLE_REGEX.find(body)?.groupValues?.get(1)?.trim()?.takeIf { it.isNotBlank() }
            if (title == null && metas["description"].isNullOrBlank() && metas["site_name"].isNullOrBlank()) {
                return@withTimeout null
            }
            // 反轉義 HTML 實體。Element 那側是問家伺服器要預覽（preview_url），實體由
            // homeserver 解好；我們自己抓網頁解 og，不解就會把「983222 &ndash; app-portage/…」
            // 原封不動顯示出來（用戶 2026-09-25 拿 Element 截圖對照點名）。
            // 解碼器沿用 ChatScreen 裡那一份（有完整命名實體表＋掃描過快取，不另寫第二份）。
            UrlPreviewData(
                url = url,
                siteName = metas["site_name"]?.let { decodeHtmlEntities(it).trim() }.orEmpty()
                    .takeIf { it.isNotBlank() },
                title = title?.let { decodeHtmlEntities(it).trim() },
                description = metas["description"]?.let { decodeHtmlEntities(it).trim() }.orEmpty()
                    .takeIf { it.isNotBlank() },
            )
        }
    } catch (_: Throwable) {
        null
    }
}
