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
    /** 失敗占位要寫「重試」，所以要把語系傳進來（本组件原本不碰字串）。 */
    strings: io.github.capricornus007.nashira.i18n.Strings,
    modifier: Modifier = Modifier,
    mimeType: String? = null,
    /** 點圖開全螢幕檢視器；null（例如被隱藏的佔位）就不吃點擊。 */
    onOpen: (() -> Unit)? = null,
    /** 隱藏時顯示的佔位文案；null 表示未隱藏。 */
    hiddenLabel: String? = null,
) {
    val key = remember(source) { source.cacheKey() }
    var bitmap by remember(key) { mutableStateOf(MediaBitmapCache.get(key)) }
    var failed by remember(key) { mutableStateOf(false) }
    /** 失敗的**原因**。只寫「重試」的話，他下次截圖我還是分不清是逾時、404 還是解不了碼。 */
    var failReason by remember(key) { mutableStateOf<String?>(null) }
    // 失敗要能重試：原本只能顯示檔名，使用者除了往上下翻讓它重新進畫面以外無路可走。
    // 靠這個計數當 LaunchedEffect 的鍵，點一下就重跑一輪。
    var reloadTick by remember(key) { mutableStateOf(0) }
    // Telegram 橋的動態貼圖是 video/webm：縮圖端點回 400、圖片解碼器吃不下，
    // 要抓原檔抽第一幀（與貼圖面板同一套 decodeVideoFrame）。
    val isVideo = remember(mimeType) { mimeType?.startsWith("video/") == true }
    val maxWidth = if (isSticker) StickerMaxWidth else ImageMaxWidth
    // 向伺服器要縮圖的尺寸要按「實際上要畫多大」算：264dp 在 2 倍縮放下是 528 實體像素，
    // 固定要 800 的「長邊」對直式照片來說寬度只剩三四百像素，擺進 528 的框就是放大 → 必糊
    //（用戶 2026-09-30 截圖 #42 對照 #41：同一張圖，全螢幕清楚、時間線糊）。
    val density = LocalDensity.current
    val boxPx = remember(density, maxWidth) { with(density) { maxWidth.toPx() }.toInt().coerceAtLeast(1) }
    LaunchedEffect(client, key, boxPx, reloadTick) {
        if (bitmap != null) return@LaunchedEffect
        // 連「開始」都記：用戶 2026-10-07 反問「爲什麼頭像加載正常圖片就加載不了」，
        // 而 /tmp/nashira-media.log 完全沒有失敗紀錄——代表這條協程**根本沒走到認輸那步**。
        // 若是每次重組都重來（key 不安定），這裡就會出現一堆重複「開始」行，一眼可辨。
        mediaProbe("開始 $key box=$boxPx")
        var reason: String? = null
        var best: ImageBitmap? = null
        // 整個載入一個**總預算**，不是每輪各給一個逾時（4 輪各 20 秒會變成 87 秒才認輸）。
        val deadlineMs = System.currentTimeMillis() + MediaTotalBudgetMs
        // 啟動初期伺服器版本還沒讀進來，請求會走舊版媒體端點被 404，所以失敗要重試幾次
        for (attempt in 0 until MediaFetchAttempts) {
            if (attempt > 0) delay(MediaRetryDelayMillis * attempt)
            val leftMs = deadlineMs - System.currentTimeMillis()
            if (leftMs <= 0L) { reason = "累計超過 ${MediaTotalBudgetMs / 1000} 秒"; break }
            val small = best?.let { it.width < boxPx } == true
            // 事件本身就寫了原圖尺寸時，比顯示框還小的圖**不必先問縮圖**：縮圖端點最多也只
            // 給到原圖大小，白跑一趟（日誌實測 kimiblock.top 對 528 的請求回 32×32）。
            val declaredSmall = width != null && width in 1 until boxPx
            val usedOriginal = isVideo || attempt > 0 || small || declaredSmall
            // ⚠️ 兩條鐵律，都是日誌實錘換來的：
            // 1) 逾時一律走 mediaCallResultWithin（擲到獨立作用域、只 await 它）。
            //    `withTimeoutOrNull { 請求 }` 要等裡面的協程真的收到取消才返回，
            //    卡在 DNS／socket 這種不可中斷呼叫時形同虛設。
            // 2) 這段**不能包在 withContext(Dispatchers.Default) 裡**。上一版就是：
            //    抓取與解碼共用 Default 那個有限池，十几張圖一起卡在讀取上時，
            //    連「要觸發逾時的協程」都排不到線程——日誌實測同一個 URL 卡了 48 秒、
            //    既沒成功也沒失敗（用戶 #70「依舊」）。現在只有**解碼**進 Default。
            val outcome = mediaCallResultWithin(leftMs) {
                val fetched = client.di.get<MediaService>().let { service ->
                    when (source) {
                        is MediaSource.Plain ->
                            if (usedOriginal) {
                                // 影片沒有縮圖端點；縮圖不夠大（或第一輪失敗）就改抓原檔本機降採樣
                                service.getMedia(source.mxcUrl, maxSize = OriginalMaxMediaBytes)
                            } else {
                                // 高度給 3 倍寬：scale 是「塞進這個框」，框不夠高會讓直式圖的寬度被壓掉
                                service.getThumbnail(
                                    source.mxcUrl,
                                    boxPx.toLong(),
                                    boxPx.toLong() * ThumbnailHeightFactor,
                                    maxSize = MaxMediaBytes,
                                )
                            }
                        is MediaSource.Encrypted -> service.getEncryptedMedia(source.file, maxSize = OriginalMaxMediaBytes)
                    }
                }
                // 讓例外走到 runCatching 裡，原因才留得下來。
                // ⚠️ 這裡的 `this` 是 mediaCallResultWithin 裡面那個**工作協程自己的作用域**
                // （IO 那組線程）。Trixnity 的 toByteArray 照著交給它的 context 讀位元組，
                // 遞外層那個進去（上一版遞的是 Default）等於前面那條鐵律白寫。
                fetched.getOrThrow().toByteArray(this)
            }
            val bytes = when {
                outcome == null -> {
                    reason = "來源伺服器 $((leftMs / 1000).coerceAtLeast(0)) 秒內沒有回應"
                    mediaProbe("$reason（第 ${attempt + 1} 次）$key")
                    continue
                }
                outcome.isFailure -> {
                    val t = outcome.exceptionOrNull()
                    reason = "${t?.javaClass?.simpleName} ${t?.message ?: ""}".trim()
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
            if (usedOriginal || frame.width >= boxPx) break
        }
        val decoded = best
        if (decoded != null) {
            MediaBitmapCache.put(key, decoded)
            bitmap = decoded
        } else if (bitmap == null) {
            failed = true
            failReason = reason
            mediaProbe("最終失敗：${reason ?: "原因不明"} $key")
        }
    }

    // 事件裡的長寬只用來保留版位，避免圖片載入後把整條時間線往下推
    val ratio = ratioOf(width, height, bitmap)
    val frame = modifier
        .widthIn(max = maxWidth)
        .then(if (isSticker) Modifier else Modifier.clip(RoundedCornerShape(12.dp)))
    val loaded = bitmap
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
        // 載入失敗就退回檔名，至少看得出這裡本來有東西；點一下重跑一輪。
        // 懸停給**原因**（也寫進 /tmp/nashira-media.log）：只知道「失敗」決定不了下一步
        // 是該重試、該換伺服器、還是這張圖根本解不了。
        failed -> HoverTooltip(text = failReason) {
            Text(
                "${caption.ifBlank { "🖼" }} · ${strings.retry}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable {
                    failed = false
                    failReason = null
                    reloadTick++
                },
            )
        }
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
 * 縮圖請求框的高度倍數：伺服器的 `method=scale` 是「把圖塞進這個框」，
 * 框給 800×800 時一張直式照片的**寬度**會被壓到 400 以下，擺進 528 實體像素的
 * 顯示框就得放大 → 糊。高度給 3 倍寬，直式圖才能保住整條寬度。
 */
internal const val ThumbnailHeightFactor = 3L

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
private const val MediaTotalBudgetMs = 10_000L

/**
 * 抓媒體用的獨立作用域。
 *
 * 存在的唯一理由：`withTimeoutOrNull { 網路請求 }` 在裡面的協程**不肯响应取消**時
 * 一樣不會返回（DNS 查詢、socket 讀取都是這種），界面就永久停在轉圈。
 * 把請求丟到這個作用域、外面只 `await` 它，逾時時放棄 await 即可立刻返回；
 * 那個卡住的協程留在這裡自己結束，不再擋在畫面上。
 */
private val MediaIoScope = kotlinx.coroutines.CoroutineScope(
    // ⚠️ 必須是 IO，**不能是 Default**。用戶 2026-10-07 反問「爲什麼頭像正常圖片不行」，
    // 而 /tmp/nashira-media.log 連一筆失敗都沒有 → 那條協程既沒成功也沒認輸。
    // 用 Default 會這樣卡死：媒體抓取與**解碼**共用同一個有限線程池，十几張圖同時卡在
    // socket 讀取上就把池子占滿，於是連「要觸發逾時的那個協程」都排不到線程——
    // 逾時本身永遠不會響。IO 是另一組按需擴充的線程，抓取擠不到解碼，逾時也跑得出來。
    kotlinx.coroutines.SupervisorJob() + kotlinx.coroutines.Dispatchers.IO,
)

/** 同上，但把失敗原因一起帶出來（「檢視／下載」那條路與失敗占位的懸停都要寫出人話）。 */
private suspend fun <T> mediaCallResultWithin(timeoutMs: Long, block: suspend kotlinx.coroutines.CoroutineScope.() -> T): Result<T>? {
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
