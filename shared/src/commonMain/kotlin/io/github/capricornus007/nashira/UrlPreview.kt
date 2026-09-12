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

fun firstUrlInText(text: String): String? = URL_REGEX.find(text)?.value?.trimEnd(',', '.', ';', ':', '!', '?')

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
            // 反轉義 HTML 常見實體（不引 HTML 解析器，僅覆蓋預覽會用到的）
            fun unescape(v: String) = v
                .replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">")
                .replace("&quot;", "\"").replace("&#39;", "'").replace("&apos;", "'").trim()
            UrlPreviewData(
                url = url,
                siteName = metas["site_name"]?.let(::unescape)?.takeIf { it.isNotBlank() },
                title = title?.let(::unescape),
                description = metas["description"]?.let(::unescape)?.takeIf { it.isNotBlank() },
            )
        }
    } catch (_: Throwable) {
        null
    }
}
