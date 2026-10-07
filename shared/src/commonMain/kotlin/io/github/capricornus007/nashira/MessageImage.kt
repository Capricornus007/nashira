package io.github.capricornus007.nashira

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import de.connect2x.trixnity.client.MatrixClient
import de.connect2x.trixnity.client.media.MediaService
import de.connect2x.trixnity.utils.toByteArray
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import io.github.capricornus007.nashira.matrix.MediaSource

/** 貼圖不畫底、不裁切，尺寸比照 Element／SchildiChat 的行內貼圖。 */
private val StickerMaxWidth = 148.dp

/** 圖片訊息的最大寬度；再寬會把發送者名字擠掉，Discord 也大約是這個比例。 */
private val ImageMaxWidth = 264.dp

/**
 * 時間線裡的圖片／貼圖。未加密房走 mxc 縮圖端點，加密房用事件自帶的金鑰解檔案，
 * 兩者都經 Trixnity 的媒體快取；解出來的位圖按來源鍵記憶，滾動不會重新下載。
 */
@Composable
fun MessageImage(
    client: MatrixClient,
    source: MediaSource,
    width: Int?,
    height: Int?,
    isSticker: Boolean,
    caption: String,
    modifier: Modifier = Modifier,
    mimeType: String? = null,
    /** 點圖開全螢幕檢視器；null（例如被隱藏的佔位）就不吃點擊。 */
    onOpen: (() -> Unit)? = null,
    /** 隱藏時顯示的佔位文案；null 表示未隱藏。 */
    hiddenLabel: String? = null,
) {
    val key = remember(source) { source.cacheKey() }
    var bitmap by remember(key) { mutableStateOf(MediaBitmapCache.get(key)) }
    /** 動畫貼圖的每一格；null 或只有一格＝靜態。 */
    var frames by remember(key) { mutableStateOf(MediaFramesCache.get(key)) }
    // Telegram 橋的動態貼圖是 video/webm：縮圖端點回 400、圖片解碼器吃不下，
    // 要抓原檔抽第一幀（與貼圖面板同一套 decodeVideoFrame）。
    val isVideo = remember(mimeType) { mimeType?.startsWith("video/") == true }
    val maxWidth = if (isSticker) StickerMaxWidth else ImageMaxWidth
    val density = LocalDensity.current
    val boxPx = remember(density, maxWidth) { with(density) { maxWidth.toPx() }.toInt().coerceAtLeast(1) }
    LaunchedEffect(client, key, boxPx) {
        if (bitmap != null) return@LaunchedEffect
        mediaProbe("開始 $key box=$boxPx")
        // ⚠️ 這裡**不能**用 `Dispatchers.Main`：桌面端（skiko）沒有 Main 排程器的提供者，
        // 一碰到就拋「Module with the Main dispatcher is missing」、彈一顆原生錯誤框把
        // 整個 UI 卡死（用戶 2026-10-07 截圖 #85 就是這個，是我這輪新加的程式碼造成的）。
        // 正確做法是把 LaunchedEffect 自己的協程上下文抓下來當回流目標——它本來就在
        // UI 執行緒上。減掉 Job 是必要的：`withContext` 不准換掉協程自己的 Job。
        val uiContext = coroutineContext.minusKey(kotlinx.coroutines.Job)
        // 「重試」這個按鈕已經拿掉（用戶 2026-10-07：「重試是多餘的」）：
        // 抓不到就自己排背退重試，直到成功為止，不把他拉進「要點一下」的迴圈。
        // 間隔 2s → 7s → 12s → 之後固定 17s，不會變成對壞位址的密集轟炸
        //（那條教訓來自頭像：實測 25 秒內對同一個 502 位址打了 40 次）。
        var round = 0
        while (bitmap == null) {
            val outcome = MediaRequests.await(key) {
                loadMediaBitmap(key, boxPx, isVideo, client, source, width) { partial ->
                    // 先讓畫面有東西：日誌實測很多伺服器只給得出 32×32 的預生成縮圖，
                    // 接著要再抓幾秒原檔。Element 的「秒開」感覺一半來自這裡——
                    // 先糊一下、再變清楚，而不是轉十秒圈。
                    kotlinx.coroutines.withContext(uiContext) {
                        if (bitmap == null) bitmap = partial
                    }
                }
            }
            if (outcome.bitmap != null) {
                MediaBitmapCache.put(key, outcome.bitmap!!)
                bitmap = outcome.bitmap
                if (outcome.frames.size > 1) MediaFramesCache.put(key, outcome.frames)
            } else {
                mediaProbe("第 ${round + 1} 輪失敗：${outcome.reason ?: "原因不明"} $key")
                delay(2_000L + round * 5_000L)
                round++
            }
        }
    }

    // 事件裡的長寬只用來保留版位，避免圖片載入後把整條時間線往下推
    val ratio = ratioOf(width, height, bitmap)
    val frame = modifier
        .widthIn(max = maxWidth)
        .then(if (isSticker) Modifier else Modifier.clip(RoundedCornerShape(12.dp)))
    // 動態貼圖：有多格就照每一格自帶的停留時間輪播（Telegram 的貼圖就是動的，
    // 用戶 2026-10-07 點名「為什麼貼紙是靜態的」）。
    val animated = frames?.takeIf { it.size > 1 }
    var frameIndex by remember(key, animated) { mutableStateOf(0) }
    if (animated != null) {
        LaunchedEffect(animated) {
            while (true) {
                delay(animated[frameIndex].durationMs.toLong())
                frameIndex = (frameIndex + 1) % animated.size
            }
        }
    }
    val loaded = animated?.getOrNull(frameIndex)?.bitmap ?: bitmap
    when {
        // 隱藏的圖片：佔位可點擊恢復（Element 的「隱藏」也是可逆的）
        hiddenLabel != null -> Box(
            frame.fillMaxWidth().height(96.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                hiddenLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        loaded != null -> Image(
            bitmap = loaded,
            contentDescription = caption.takeIf { it.isNotBlank() },
            modifier = frame
                .fillMaxWidth()
                .aspectRatio(ratio)
                .then(if (onOpen != null) Modifier.clickable(onClick = onOpen) else Modifier),
            contentScale = if (isSticker) ContentScale.Fit else ContentScale.Crop,
            // 一定要明寫 High：預設是 Low（最近鄰），縮放時直接糊成一團或鋸齒
            //（用戶 2026-09-29 對照 Telegram：「tg 無論點開之前還是點開之後都沒那麼糊」）。
            filterQuality = FilterQuality.High,
        )
        // 貼圖的佔位不畫灰底：多數貼圖有透明背景，灰塊會在載入前一閃，看起來
        // 像是「貼圖壞了」。圖片訊息保留灰底（裁切圓角需要一個可見的版位）。
        else -> if (isSticker) {
            Box(frame.fillMaxWidth().aspectRatio(ratio), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            }
        } else {
            Box(
                frame.fillMaxWidth().aspectRatio(ratio)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            }
        }
    }
}

/** 沒有尺寸資訊時給 4:3，載入後改用真實比例。極端長圖夾在 0.5–2.0 之間，避免一張圖佔滿整頁。 */
private fun ratioOf(width: Int?, height: Int?, bitmap: ImageBitmap?): Float {
    val w = bitmap?.width ?: width
    val h = bitmap?.height ?: height
    if (w == null || h == null || w <= 0 || h <= 0) return 4f / 3f
    return (w.toFloat() / h.toFloat()).coerceIn(0.5f, 2f)
}

/** 縮圖上限 2 MiB：時間線一次可能掛十幾張圖，原圖直接拉會把手機流量與記憶體吃光。 */
internal const val MaxMediaBytes = 2L * 1024 * 1024

/**
 * 縮圖請求框：**與 Element／SchildiChat 相同**的 800×600。
 * Synapse 按尺寸分別快取縮圖，這個組合命中率最高（別人已經生成過了），
 * 自訂比例會讓每張圖都變成「家伺服器現場生成」，慢到 20 秒不回應。
 */
internal const val ThumbnailWidth = 800L
internal const val ThumbnailHeight = 600L

/**
 * 縮圖寬度至少要達到顯示框的這個比例才算「夠清楚」，否則再抓原檔。
 * 直式手機照片塞進 800×600 後寬度約 450，對 528 實體像素的框是 0.85 倍，
 * 與 Element 畫面一致；只有極端長圖才會多跑一趟原檔。
 */
private const val AcceptableThumbnailFraction = 3

/** 中間結果至少要這麼寬才值得貼出來（32×32 那種放大後是一團色塊，不如等原檔）。 */
private const val ProgressiveMinWidth = 160

/**
 * 回過「沒有動態縮圖」的家伺服器名單（程序生命週期內有效）。
 * 對它們再問一次縮圖只是白等 1～4 秒，問過一次就記住。
 */
private val NoThumbnailServers = HashSet<String>()

/** mxc://host/id 裡的 host，當「這臺伺服器有沒有動態縮圖」的鍵。 */
private fun MediaSource.mxcHost(): String = when (this) {
    is MediaSource.Plain -> mxcUrl.substringAfter("mxc://", "").substringBefore("/", "")
    is MediaSource.Encrypted -> file.url.substringAfter("mxc://", "").substringBefore("/", "")
}

/** 退而抓原檔時的上限：原圖普遍 3–6 MiB，卡在同一個 2 MiB 會直接拿不到檔案。 */
internal const val OriginalMaxMediaBytes = 8L * 1024 * 1024

private fun MediaSource.cacheKey(): String = when (this) {
    is MediaSource.Plain -> mxcUrl
    is MediaSource.Encrypted -> file.url
}

/** 進程內位圖快取：時間線來回滾動會反覆掛載同一則圖片訊息，貼圖面板也共用這份。 */
/**
 * 進程內位圖快取。**按位元組上限淘汰，不能只數個數**：64 張全解析度貼圖就能吃掉
 * 兩百多 MB，把 Android 的 256MB 堆積上限撐爆（實測 OutOfMemoryError）。
 */
internal object MediaBitmapCache {
    /** 約 48MB：足夠一個貼圖面板與一屏訊息，離 256MB 上限還有很大餘裕。 */
    private const val MAX_BYTES = 48L * 1024 * 1024
    private val entries = LinkedHashMap<String, ImageBitmap>()
    private var bytes = 0L

    private fun sizeOf(bitmap: ImageBitmap): Long = bitmap.width.toLong() * bitmap.height * 4

    fun get(key: String): ImageBitmap? = entries[key]

    fun put(key: String, bitmap: ImageBitmap) {
        entries.remove(key)?.let { bytes -= sizeOf(it) }
        entries[key] = bitmap
        bytes += sizeOf(bitmap)
        // LinkedHashMap 的插入順序就是淘汰順序（最舊的先走）
        while (bytes > MAX_BYTES && entries.size > 1) {
            val oldest = entries.keys.firstOrNull() ?: break
            entries.remove(oldest)?.let { bytes -= sizeOf(it) }
        }
    }
}

/** 抓原檔位元組（檢視器與「下載」選單共用；32MB 上限擋異常大檔）。 */
internal suspend fun fetchMediaBytes(client: MatrixClient, source: MediaSource): ByteArray? =
    fetchMediaWithError(client, source).first

/**
 * 同上，但把失敗原因一起帶出來。原本失敗只回 null，界面只能寫一句「下載失敗」，
 * 使用者分不清是「來源伺服器掛了」還是「我們抓錯東西」
 * （用戶 2026-10-07 要的就是懸停看到具體原因）。
 *
 * ⚠️ 一定要有逾時：`getMedia` 自己沒有時間上限，來源伺服器不回時這個協程就永久掛著，
 * 而畫面上「還在抓」跟「已經死了」長得一模一樣——用戶截圖那張來自 nichi.co 的圖片
 * 「以前載得出來、現在轉沒完了」就是這個。
 */
internal suspend fun fetchMediaWithError(client: MatrixClient, source: MediaSource): Pair<ByteArray?, Throwable?> =
    kotlinx.coroutines.coroutineScope {
        val service = client.di.get<MediaService>()
        // 走 mediaCallResultWithin 而不是 withTimeoutOrNull：後者遇到不響應取消的阻塞呼叫
        // （DNS、socket 讀取）一樣不會返回，「檢視／下載」那條路就會把按鈕卡死在那裡。
        val outcome = mediaCallResultWithin(MEDIA_TIMEOUT_MS) {
            val response = when (source) {
                is MediaSource.Plain -> service.getMedia(source.mxcUrl, maxSize = 32L * 1024 * 1024)
                is MediaSource.Encrypted -> service.getEncryptedMedia(source.file, maxSize = 32L * 1024 * 1024)
            }
            response.getOrThrow().toByteArray(this@coroutineScope)
        }
        when {
            outcome == null -> null to java.net.SocketTimeoutException(
                "來源伺服器 $((MEDIA_TIMEOUT_MS / 1000)) 秒內沒有回應",
            )
            outcome.isFailure -> null to outcome.exceptionOrNull()
            else -> outcome.getOrNull() to null
        }
    }

/**
 * 媒體載入的總預算（含重試與退避）：慢伺服器要在可預期的時間內認輸，
 * 界面才給得出「重試」。單次逾時不再單獨設定——4 輪各給 20 秒會變成 87 秒。
 *
 * 用毫秒 Long 不用 kotlin.time.Duration：Duration 的比較運算子與 `seconds()`
 * 在這套 commonMain 下要額外 import，實測一口氣踩了三個編譯錯誤。
 */
private const val MediaTotalBudgetMs = 20_000L

/**
 * 抓媒體用的獨立作用域。
 *
 * 存在的唯一理由：`withTimeoutOrNull { 網路請求 }` 在裡面的協程**不肯响应取消**時
 * 一樣不會返回（DNS 查詢、socket 讀取都是這種），界面就永久停在轉圈。
 * 把請求丟到這個作用域、外面只 `await` 它，逾時時放棄 await 即可立刻返回；
 * 那個卡住的協程留在這裡自己結束，不再擋在畫面上。
 */
/**
 * 讀位元組專用的作用域（獨立的 Job）。
 *
 * 存在的理由見 `loadMediaBitmap` 那段：Trixnity 的 `toByteArray(scope)` 會在給它的那個
 * scope 裡完成讀取，所以**不能**把「正在 await 結果的那個協程」交給它，否則自己等自己。
 */
private val MediaReadScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)

private val MediaIoScope = kotlinx.coroutines.CoroutineScope(
    // ⚠️ 必須是 IO，**不能是 Default**。用戶 2026-10-07 反問「爲什麼頭像正常圖片不行」，
    // 而 /tmp/nashira-media.log 連一筆失敗都沒有 → 那條協程既沒成功也沒認輸。
    // 用 Default 會這樣卡死：媒體抓取與**解碼**共用同一個有限線程池，十几張圖同時卡在
    // socket 讀取上就把池子占滿，於是連「要觸發逾時的那個協程」都排不到線程——
    // 逾時本身永遠不會響。IO 是另一組按需擴充的線程，抓取擠不到解碼，逾時也跑得出來。
    kotlinx.coroutines.SupervisorJob() + kotlinx.coroutines.Dispatchers.IO,
)

/** 同上，但把失敗原因一起帶出來（「檢視／下載」那條路與失敗占位的懸停都要寫出人話）。 */
private suspend fun <T> mediaCallResultWithin(timeoutMs: Long, block: suspend () -> T): Result<T>? {
    val task = MediaIoScope.async { runCatching { block() } }
    return try {
        kotlinx.coroutines.withTimeout(timeoutMs) { task.await() }
    } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
        null
    } finally {
        // 逾時、正常返回、以及外面那個 LaunchedEffect 被取消（滾出畫面）三條路都要收乾淨：
        // 滾走的圖片不該繼續占頻寬，否則時間線来回翻時會積一堆沒人在等的下載。
        task.cancel()
    }
}

/**
 * 「檢視器／另存圖片」那條路的單次上限：它只抓一次、不重試，
 * 所以用得上獨立的逾時，而不是上面那個含退避的總預算。
 */
private const val MEDIA_TIMEOUT_MS = 15_000L

/**
 * 媒體載入的診斷紀錄，寫在 `/tmp/nashira-media.log`。
 *
 * 為什麼要留檔而不是只印 stdout：用戶 2026-10-07 用兩張 ping 截圖打臉了我
 * 「圖拿不到是對方伺服器的 nginx 壞了」的說法（兩個網域都通、70ms）——
 * 我不能再靠猜。每一次失敗的真實例外都寫下來，我自己讀得到，不用麻煩他截圖。
 */
private fun mediaProbe(line: String) {
    runCatching {
        java.io.File("/tmp/nashira-media.log")
            .appendText("${System.currentTimeMillis()} $line\n")
    }
}

/**
 * 一個 mxc 一把請求的共用登记表。
 *
 * 為什麼需要它（用戶 2026-10-07 #72「看起來依舊無限加載」，日誌給的實據）：
 * 逾時本身已經會響了，但時間線是 LazyColumn——訊息滾出畫面再滾回來，掛在它上面的
 * `LaunchedEffect` 會被取消後重頭跑一次，於是同一個網址每七八秒重抓一次、
 * 每次都從零開始算預算，**永遠到不了「認輸」**，畫面就一直是轉圈。
 * 把「重試＋解碼」收成按 mxc 共用的協程之後：第二個畫面是接上同一個請求，
 * 認輸過的網址也會被記住（冷卻期內直接顯示「重試」，不再浪費一次轉圈）。
 *
 * ⚠️ 全部只在主執行緒存取（`LaunchedEffect` 的上下文），所以用普通 HashMap 就夠。
 */
private object MediaRequests {
    private val inFlight = HashMap<String, kotlinx.coroutines.Deferred<MediaLoad>>()

    suspend fun await(key: String, load: suspend () -> MediaLoad): MediaLoad {
        inFlight[key]?.let { running ->
            if (!running.isCompleted) mediaProbe("接上同一個請求 $key")
            else mediaProbe("取回剛完成的那把 $key")
            return running.await()
        }
        // 清掉已經跑完卻沒人取的條目（滾來滾去時會累積，每條都掛著位圖）
        if (inFlight.size > 24) {
            inFlight.filterValues { it.isCompleted }.keys.toList().forEach { inFlight.remove(it) }
        }
        val task = MediaIoScope.async { load() }
        inFlight[key] = task
        return try {
            task.await()
        } finally {
            // 只有真的等到結果才摘掉；被取消（滾出畫面）時留著，下一個畫面才能接上同一個
            if (task.isCompleted && inFlight[key] === task) inFlight.remove(key)
        }
    }
}

/** 一趟載入的結果：成功的位圖、失敗原因、以及（若是動畫）全部格。 */
internal class MediaLoad(
    val bitmap: ImageBitmap?,
    val reason: String? = null,
    val frames: List<DecodedFrame> = emptyList(),
)

/** 動畫格的快取。位元組上限比照 MediaBitmapCache：一格 0.3MB、十格就是一張圖的十倍。 */
internal object MediaFramesCache {
    private const val MAX_BYTES = 24L * 1024 * 1024
    private val entries = LinkedHashMap<String, List<DecodedFrame>>()
    private var bytes = 0L

    private fun sizeOf(frames: List<DecodedFrame>): Long =
        frames.sumOf { it.bitmap.width.toLong() * it.bitmap.height * 4 }

    fun get(key: String): List<DecodedFrame>? = entries[key]

    fun put(key: String, frames: List<DecodedFrame>) {
        entries.remove(key)?.let { bytes -= sizeOf(it) }
        // 一張動畫動輒幾十格，超過上限就整組不收（退回顯示第一格的靜態畫面）
        if (sizeOf(frames) > MAX_BYTES / 4) return
        entries[key] = frames
        bytes += sizeOf(frames)
        while (bytes > MAX_BYTES && entries.size > 1) {
            val oldest = entries.keys.firstOrNull() ?: break
            entries.remove(oldest)?.let { bytes -= sizeOf(it) }
        }
    }
}

/**
 * 抓一張圖：問縮圖 → 不夠就抓原檔 → 解碼。回「位圖 + 失敗原因」。
 *
 * ⚠️ 兩條鐵律，都是日誌實錘換來的：
 * 1) 逾時一律走 mediaCallResultWithin（擲到獨立作用域、只 await 它）。
 *    `withTimeoutOrNull { 請求 }` 要等裡面的協程真的收到取消才返回，
 *    卡在 DNS／socket 這種不可中斷呼叫時形同虛設。
 * 2) 整個函式**不能包在 withContext(Dispatchers.Default) 裡**：抓取與解碼共用那個有限池時，
 *    十几張圖一起卡在讀取上，連「要觸發逾時的協程」都排不到線程（實測同一個 URL 卡 48 秒、
 *    既沒成功也沒失敗）。現在只有**解碼**進 Default。
 */
private suspend fun loadMediaBitmap(
    key: String,
    boxPx: Int,
    isVideo: Boolean,
    client: MatrixClient,
    source: MediaSource,
    declaredWidth: Int?,
    /** 手上有可用的一張（還不夠清楚）時先給畫面，別讓人對著轉圈器等十幾秒。 */
    onProgress: suspend (ImageBitmap) -> Unit = {},
): MediaLoad {
    var best: ImageBitmap? = null
    var reason: String? = null
    var animatedFrames: List<DecodedFrame> = emptyList()
    val deadlineMs = System.currentTimeMillis() + MediaTotalBudgetMs
    // 啟動初期伺服器版本還沒讀進來，請求會走舊版媒體端點被 404，所以失敗要重試幾次
    for (attempt in 0 until MediaFetchAttempts) {
        if (attempt > 0) delay(MediaRetryDelayMillis * attempt)
        val leftMs = deadlineMs - System.currentTimeMillis()
        if (leftMs <= 0L) { reason = "累計超過 ${MediaTotalBudgetMs / 1000} 秒"; break }
        val small = best?.let { it.width < boxPx } == true
        // 事件本身就寫了原圖尺寸時，比顯示框還小的圖**不必先問縮圖**：縮圖端點最多也只
        // 給到原圖大小，白跑一趟（日誌實測 kimiblock.top 對 528 的請求回 32×32）。
        val declaredSmall = declaredWidth != null && declaredWidth in 1 until boxPx
        val acceptableWidth = boxPx / AcceptableThumbnailFraction
        // 同一個家伺服器只要回過一次「沒有動態縮圖」，之後它的圖**直接抓原檔**：
        // 先問縮圖必定白跑一趟（日誌實測那一趟 1～4 秒，拿回來的是 400 或一張 32×32）。
        val knownNoThumbnail = source.mxcHost() in NoThumbnailServers
        val usedOriginal = isVideo || attempt > 0 || small || declaredSmall || knownNoThumbnail
        val outcome = mediaCallResultWithin(leftMs) {
            val fetched = client.di.get<MediaService>().let { service ->
                when (source) {
                    is MediaSource.Plain ->
                        if (usedOriginal) {
                            // 影片沒有縮圖端點；縮圖不夠大（或第一輪失敗）就改抓原檔本機降採樣
                            service.getMedia(source.mxcUrl, maxSize = OriginalMaxMediaBytes)
                        } else {
                            // ⚠️ 尺寸要跟 Element／SchildiChat **一模一样**（800×600 scale），
                            // 不是按我們的顯示框算。Synapse 是「按尺寸分別快取」縮圖的：
                            // 800×600 這種常用組合早就被前幾個客戶端生成過、直接命中快取；
                            // 我們之前要 528×1584（為了直式圖不糊自己發明的比例）
                            // **每個都是冷檔**，家伺服器得先跟來源伺服器要原圖再生成，
                            // 實測動輒 20 秒不回應——這就是「別人秒開、我們轉圈」的原因
                            //（用戶 2026-10-07 #77「就必須加載出來，而且秒開秒出」）。
                            service.getThumbnail(
                                source.mxcUrl,
                                ThumbnailWidth,
                                ThumbnailHeight,
                                maxSize = MaxMediaBytes,
                            )
                        }
                    is MediaSource.Encrypted -> service.getEncryptedMedia(source.file, maxSize = OriginalMaxMediaBytes)
                }
            }
            // 讓例外走到 runCatching 裡，原因才留得下來。
            //
            // ⚠️⚠️ `toByteArray` 一定要遞**另一個**作用域進去，不能遞「正在等這個結果的
            // 那個協程自己的」作用域。Trixnity 會在拿到的那個 scope 裡跑讀取，
            // 遞自己的 scope 等於自己等自己：實測 0.1.54 起**每一張**圖都在 20 秒準時
            // 逾時、連 `mxc://matrix.org/…`（自己家的圖、沒有聯邦、沒有冷檔）都一樣，
            // 而且那 20 秒內 `ss -tnp` 看不到任何新連線與位元組變動——請求根本沒讀完。
            // 0.1.52 是 `toByteArray(this@withContext)`（外層那個 scope）所以正常。
            fetched.getOrThrow().toByteArray(MediaReadScope)
        }
        val bytes = when {
            outcome == null -> {
                reason = "來源伺服器 ${(leftMs / 1000).coerceAtLeast(0L)} 秒內沒有回應"
                mediaProbe("$reason（第 ${attempt + 1} 次）$key")
                continue
            }
            outcome.isFailure -> {
                val t = outcome.exceptionOrNull()
                reason = "${t?.javaClass?.simpleName} ${t?.message ?: ""}".trim()
                // Synapse 關掉動態縮圖時的原文是「Cannot find any thumbnails for the
                // requested media … Dynamic thumbnails are disabled on this server」。
                if (!usedOriginal && reason.contains("thumbnail", ignoreCase = true)) {
                    NoThumbnailServers.add(source.mxcHost())
                    mediaProbe("記下：${source.mxcHost()} 沒有動態縮圖，之後直接抓原檔")
                }
                mediaProbe("第 ${attempt + 1} 次：$reason $key")
                continue
            }
            else -> outcome.getOrNull()
        }
        // 解碼留給 Default：Skia 沒有解碼期降採樣，一張 2560 的原圖是「先整張解開、再縮」，
        // 在 UI 執行緒上解 6–8 MB 的 JPEG 會讓時間線黏（用戶 2026-09-30 點名）。
        val frame = if (bytes != null) {
            withContext(kotlinx.coroutines.Dispatchers.Default) {
                if (isVideo) decodeVideoFrame(bytes, maxDimension = boxPx * 2)
                else decodeImageBitmap(bytes, maxDimension = boxPx * 2)
            }
        } else null
        if (frame == null) {
            reason = "拿到 ${bytes?.size ?: 0} 位元組但解不了碼"
            mediaProbe("第 ${attempt + 1} 次：$reason $key")
            continue
        }
        best = frame
        mediaProbe("成功 $key 第 ${attempt + 1} 次 ${bytes?.size ?: 0}B → ${frame.width}x${frame.height}")
        // **抓過原檔就到此為止**：上一版只比「寬度夠不夠 boxPx」，小圖（橫幅、貼紙、
        // 低解析照片）明明已拿到完整原檔還被判不合格，同一個 3295B／309x59 被抓了四遍。
        if (usedOriginal || frame.width >= acceptableWidth) break
        // 還想再抓一張更好的：先把這張交出去，畫面至少不是空白加轉圈。
        // 但**糊到不能看的不算**：這些伺服器只預生成 32×32，把它放大成五百像素的
        // 一團色塊比轉圈更難看（用戶 2026-10-07 #86 對照 #88「不能直接這張嗎」）。
        if (frame.width >= ProgressiveMinWidth) onProgress(frame)
    }
    return MediaLoad(best, reason ?: if (best == null) "抓完了但沒有可用的一張" else null, animatedFrames)
}
