package io.github.capricornus007.nashira

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.connect2x.trixnity.client.MatrixClient
import io.github.capricornus007.nashira.matrix.MediaSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.File
import java.io.RandomAccessFile
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine

/**
 * 全螢幕檢視器裡的**內嵌影片播放**：圖片與影片共用同一個殼，控制列疊在畫面上。
 *
 * 使用者 2026-10-08 的原話：「爲什麼要變成點開 mpv 窗口啊…………我點好幾次它延遲好幾秒
 * 然後點開好幾個窗口，就不能弄的跟 tg discord 那種嗎？點開圖片視頻都是有統一 ui 的內置」。
 *
 * 為什麼是現在這個做法（每一條都量過，別再走回頭路）：
 * - 不捆 FFmpeg 函式庫：bytedeco 那顆 jar **解開 129MB**（libavcodec.so 單檔 33.5MB）。
 * - 不依賴系統 libVLC：別人沒裝就壞，而且 VLC 是 GPL。
 * - 不試 mpv `--wid` 嵌入：實測在 JDK 21 上拿不到 AWT 畫布的 X11 視窗 id（兩種反射都回 -1）。
 * - 走「兩條 ffmpeg 行程」：一條吐定長 BGRA 格、一條吐 PCM 給 JVM 自己的 javax.sound
 *   （這臺機器實測 SourceDataLine 開得起來、寫得進去，9 顆 mixer）。
 *   **兩邊都加 `-re`**，讓 ffmpeg 自己按原速率輸出，所以不需要自己對時鐘；
 *   暫停＝殺行程並記住毫秒，繼續／拖動＝帶 `-ss` 重開。
 * - 像素格式：實測 `ColorType.BGRA_8888` 配 ffmpeg `-pix_fmt bgra`（名字對名字，
 *   不靠「小端到底是 RGBA 還是 ARGB」的記憶——那個猜錯就整片紅藍對調）。
 * - **能串流就先串流**：mp4 的 moov 常在檔尾，喂 `pipe:0` 解不動（實測過），
 *   但餵「可 seek 的檔案或 http URL」就可以。給 URL＝點開立刻播，不用等整檔下載完
 *   （用戶 2026-10-08「憑什麼視頻非得那麼久」）。
 * - 音頻那條會把同一段流量再抓一次：換到的是「不用自己寫對時鐘」，代價寫在這裡，別當它不存在。
 */
@Composable
actual fun EmbeddedVideoPlayer(
    client: MatrixClient,
    source: MediaSource,
    bytes: ByteArray?,
    poster: ImageBitmap?,
    boxWidth: Dp,
    boxHeight: Dp,
    inline: Boolean,
    playToggleTick: Int,
    modifier: Modifier,
) {
    val density = LocalDensity.current
    val maxW = with(density) { boxWidth.roundToPx() }.coerceAtLeast(64)
    val maxH = with(density) { boxHeight.roundToPx() }.coerceAtLeast(64)

    // ffmpeg 要的輸入：網址（串流）或落地後的暫存檔路徑。这两个是「一次解析、之後不再改」，
    // 所以檢視器那边的下載完成（bytes 從 null 變成整份）不會把正在播的畫面重頭來過。
    var input by remember { mutableStateOf<String?>(null) }
    var info by remember { mutableStateOf<MovieInfo?>(null) }
    var tempFile by remember { mutableStateOf<File?>(null) }

    var frame by remember { mutableStateOf<ImageBitmap?>(null) }
    var playing by remember { mutableStateOf(true) }
    var positionMs by remember { mutableLongStateOf(0L) }
    var resumeMs by remember { mutableLongStateOf(0L) }
    var generation by remember { mutableIntStateOf(0) }
    val durationMs = (info?.durationSec ?: 0L) * 1000L

    // 檢視器容器層分派過來的播放／暫停（Space、Enter）。0 代表「還沒按過」，
    // 不能拿 0 去翻轉，否則一進場就是暫停。
    // 播完那一趟 `playing` 已經被設回 false、起點歸零，所以這裡翻回 true 就是**從頭重放**
    //（用戶 2026-10-08：「放完的視頻也不會正常按照按了空格就重放」）。
    LaunchedEffect(playToggleTick) {
        if (playToggleTick > 0) playing = !playing
    }

    DisposableEffect(tempFile) {
        val stale = tempFile
        onDispose { stale?.let { runCatching { it.delete() } } }
    }

    // ⚠️ 寫 Compose 狀態一定要回到**合成的上下文**：桌面端（skiko）是「按需重畫」，
    // 從 `Dispatchers.IO` 直接寫 `mutableStateOf` 不會把畫面叫醒——實測症狀就是
    // ffmpeg 明明在吐格（同參數在 shell 裡 12 秒 375 格）、聲音也照播，
    // 畫面卻永久停在轉圈與 0:00（用戶 2026-10-08 連回報三次「卡加載」）。
    // 本倉 `MessageImage.kt` 早就有同一條慣例：抓 `coroutineContext.minusKey(Job)` 當回流目標。
    LaunchedEffect(client, source, bytes != null) {
        val uiContext = coroutineContext.minusKey(Job)
        if (input != null) return@LaunchedEffect
        withContext(Dispatchers.IO) {
            // 串流候選依序試（每一個都是「點開就能播」，差別在哪家伺服器讓不讓免驗證下載）：
            // 1) 本機代理：帶著**活著的**憑證，matrix.org 這種把舊的免驗證端點關掉的只有這條
            // 2) 原站的公開下載端點：t2bot.io 這類橋站還開著（實測 206＋accept-ranges: bytes）
            val candidates = buildList {
                mediaStreamUrl(client, source)?.let(::add)
                (source as? MediaSource.Plain)?.let { plain -> mxcToPublicUrl(plain.mxcUrl)?.let(::add) }
            }
            var resolved: Pair<String, MovieInfo>? = null
            // 探測結果按「這條媒體 + 第幾個候選」快取。省掉的是一條**完整的網路往返**：
            // 實測他這條鏈路光是 TTFB 就 ~1 秒，而 ffprobe 與 ffmpeg 各要一條連線。
            // 相簿左右連翻、翻回去看同一條，或同一條看第二次，都不用再等那次探測。
            val probeKeyBase = when (val s = source) {
                is MediaSource.Plain -> s.mxcUrl
                is MediaSource.Encrypted -> s.file.url
            }
            candidates.forEachIndexed { index, candidate ->
                if (resolved != null) return@forEachIndexed
                val probeKey = "$probeKeyBase#$index"
                val probed = MovieProbeCache[probeKey] ?: probeMovie(candidate)?.also { MovieProbeCache[probeKey] = it }
                if (probed != null) resolved = candidate to probed
                else mediaProbe("內嵌播放：串流候選 ${index + 1} 探測不通")
            }
            resolved?.let { (where, meta) ->
                mediaProbe("內嵌播放：走串流（第 ${candidates.indexOf(where) + 1} 個候選）")
                input = where
                info = meta
                return@withContext
            }
            // 行內模式**不退回去抓整份檔**：氣泡那麼小一塊，為了它先拖 10MB 下來，
            // 等於把「點開要看的那一條」的頻寬吃掉——串流不通就留封面，等用戶點開再說。
            if (inline) {
                mediaProbe("內嵌播放：行內串流不通，保留封面")
                return@withContext
            }
            // 兩條串流都不通才退回整份檔：檢視器手上那份（可能還沒抓到、甚至是 0 位元組），
            // 再不通就直接向原站要一份。
            // ⚠️ 第三條退路不能省：0.1.76 拿掉「開啟時預抓整檔」之後，
            // 家伺服器自己存的媒體（代理拿不到憑證、原站又不給免驗證下載，matrix.org 就是這種）
            // 會三條路全空、完全播不出來。用**活著的 client** 抓一份才補得上這個洞
            // （加密房的影片也只有這條走得通）。代價是整份檔會進記憶體，
            // 我們的 heap 上限 384MB——真遇到幾百 MB 的檔會失敗，但不會比現在「播不了」更糟。
            val data = bytes?.takeIf { it.isNotEmpty() }
                ?: (source as? MediaSource.Plain)?.let { downloadBytesFromOrigin(it.mxcUrl) }?.takeIf { it.isNotEmpty() }
                ?: fetchMediaBytesForPlayback(client, source)?.takeIf { it.isNotEmpty() }
            if (data == null) {
                mediaProbe("內嵌播放：串流與整檔都不通 ${source}")
                return@withContext
            }
            val file = writePlayableFile(data) ?: return@withContext
            val probed = probeMovie(file.absolutePath)
            if (probed == null) {
                runCatching { file.delete() }
                mediaProbe("內嵌播放：本檔也解不出來（${data.size} 位元組）")
            } else {
                mediaProbe("內嵌播放：落地本檔播放 ${data.size} 位元組")
                withContext(uiContext) {
                    tempFile = file
                    input = file.absolutePath
                    info = probed
                }
            }
        }
    }

    // 邊抓邊播在他這條管子上一定會卡（影片位元率 570KB/s > 鏈路 150–280KB/s），
    // 所以**同時**把整份檔抓進磁碟（串流寫檔、不進記憶體）；抓完就換成本機檔案、
    // 從同一個位置續播——之後是純本地播放，跟網路再無關係，也就不會再卡。
    // 這是「立刻能開始看」與「看得順」兩個都要的作法，取捨放在這裡而不是丟給用戶選。
    LaunchedEffect(input, source, inline) {
        val uiContext = coroutineContext.minusKey(Job)
        val current = input ?: return@LaunchedEffect
        if (inline) return@LaunchedEffect // 氣泡那側不搶頻寬：要看的不是它
        if (!current.startsWith("http")) return@LaunchedEffect // 已經是本地檔
        val plain = source as? MediaSource.Plain ?: return@LaunchedEffect
        val job = coroutineContext[kotlinx.coroutines.Job]
        val file = withContext(Dispatchers.IO) {
            downloadOriginToFile(plain.mxcUrl, LocalSwitchMaxBytes) { job?.isActive == true }
        } ?: return@LaunchedEffect
        val probed = withContext(Dispatchers.IO) { probeMovie(file.absolutePath) }
        if (probed == null) {
            runCatching { file.delete() }
            return@LaunchedEffect
        }
        mediaProbe("內嵌播放：整檔已落地 ${file.length()} 位元組，改從本機續播")
        val at = positionMs
        withContext(uiContext) {
            resumeMs = at
            info = probed
            tempFile = file
            input = file.absolutePath
        }
    }

    LaunchedEffect(input, info, playing, generation, resumeMs) {
        val uiContext = coroutineContext.minusKey(Job)
        val sourcePath = input ?: return@LaunchedEffect
        val meta = info ?: return@LaunchedEffect
        if (!playing) return@LaunchedEffect
        // 暫停再按繼續時 `resumeMs` 並不會被設（它只給「換檔／拖動」用），
        // 于是每次恢復都從 0 重新起解碼器、再靠音訊時鐘把畫面硬追回來
        //（用戶 2026-10-09：「暫停恢復並不會實時，看着像每次取消暫停都是從頭開始硬跳過去的」）。
        // 讀 positionMs（上一格顯示的位置）才算續播；自然播完那一趟它已被歸零，
        // 所以「播完按空格重放」照樣從頭。
        val startMs = resumeMs.takeIf { it > 0L } ?: positionMs
        val ended = withContext(Dispatchers.IO) {
            if (inline) {
                // 快速滾動時會一口氣經過很多條影片：先等一小拍，被滾走的會被取消、
                // 根本不會起 ffmpeg（行內那條也是要搶你那條不寬的管子的）。
                delay(InlineStartDelayMs)
                if (!isActive) return@withContext false
            }
            val decoder = MovieDecoder.create(
                sourcePath, meta, maxW, maxH, startMs,
                audio = !inline && meta.hasAudio,
                fps = if (inline) InlineFps else PlaybackFps,
            ) ?: return@withContext false
            var done = false
            var firstFrameLogged = false
            // 看門狗要**平行跑**：真正的卡法是 `readFrame()` 永遠不返回（2026-10-08 那次
            // 卡在 ffmpeg 的 `Overwrite? [y/N]` 提示上：行程活著、不出口、也不吐資料），
            // 放在迴圈裡任何一处都輪不到執行。
            val framesRead = java.util.concurrent.atomic.AtomicInteger(0)
            val watchdog = launch {
                delay(NoFrameWatchdogMs)
                if (framesRead.get() == 0) decoder.warnIfNoOutput()
            }
            try {
                var index = 0
                while (true) {
                    if (!decoder.readFrame()) {
                        decoder.reportFailure()
                        done = true
                        break
                    }
                    val frameMs = index * 1000L / PlaybackFps
                    val audioMs = decoder.audioPositionMs()
                    // 只丟「落後音訊超過半秒」的格（那種畫面留著也沒意義，顯示最新那張就好）。
                    // ⚠️ 準則一定是音訊、不是牆鐘：網路比影片位元率慢時，牆鐘永遠領先，
                    // 每一格都會被判成過期格——用戶 2026-10-08 實測回報的
                    // 「只有聲音沒有畫面、進度一直 0:00」就是那樣來的。
                    if (audioMs == null || frameMs >= audioMs - StaleWindowMs) {
                        val shown = decoder.toBitmap()
                        val at = startMs + (audioMs ?: frameMs)
                        withContext(uiContext) {
                            if (shown != null) {
                                frame = shown
                                if (firstFrameLogged.not()) {
                                    firstFrameLogged = true
                                    mediaProbe("內嵌播放：第一格已顯示（第 ${index + 1} 格）")
                                }
                            }
                            positionMs = at
                        }
                    }
                    index++
                    framesRead.incrementAndGet()
                    if (!isActive) break
                }
            } finally {
                watchdog.cancel()
                decoder.close()
            }
            done && isActive
        }
        if (ended) {
            if (inline) {
                // 行內：假裝沒事，從頭再放（Telegram 的行內影片／GIF 就是無限循環）
                resumeMs = 0L
                positionMs = 0L
                generation += 1
            } else {
                // 全螢幕播到檔尾：起點歸零，讓他再點播放鈕能從頭看
                //（停在尾端會一點就立刻又結束）
                playing = false
                resumeMs = 0L
                positionMs = durationMs
            }
        }
    }

    Box(
        modifier
            .fillMaxSize()
            .pointerInput(playing, generation, input, inline) {
                // 全螢幕時點畫面＝播放／暫停（Telegram 桌面端就是這個手勢）。這裡要把事件吃掉，
                // 否則會被外層「點背景關閉」的監聽一起收走——點一下圖就關掉視窗很怪。
                // 行內模式不搶點擊：那一側的點擊語意是「開全螢幕」。
                if (inline) return@pointerInput
                detectTapGestures {
                    if (input == null) return@detectTapGestures
                    if (playing) resumeMs = positionMs
                    playing = !playing
                    generation += 1
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        val shown = frame ?: poster
        if (shown != null) {
            Image(
                bitmap = shown,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
                filterQuality = FilterQuality.High,
            )
        } else {
            CircularProgressIndicator(Modifier.size(32.dp), strokeWidth = 3.dp, color = Color.White)
        }
        if (inline) return@Box
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .widthIn(max = 560.dp)
                .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
                .background(Color.Black.copy(alpha = 0.62f), RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    Modifier
                        .size(30.dp)
                        .clickable {
                            if (playing) resumeMs = positionMs
                            playing = !playing
                            generation += 1
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    if (playing) {
                        // 暫停＝兩條豎棒。Icons.Filled.Pause **不在 material-icons-core 那 40 個裡**
                        // （專案只引 core，擴充集要 30MB），所以自己畫，不為一顆圖示拉整個套件。
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(2) {
                                Box(
                                    Modifier
                                        .size(width = 4.dp, height = 16.dp)
                                        .background(Color.White, RoundedCornerShape(1.dp)),
                                )
                            }
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp),
                        )
                    }
                }
                Text(formatClock(positionMs), color = Color.White, style = MaterialTheme.typography.labelMedium)
                Box(
                    Modifier
                        .weight(1f)
                        .height(18.dp)
                        .pointerInput(durationMs) {
                            detectTapGestures { offset ->
                                if (durationMs <= 0) return@detectTapGestures
                                val fraction = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                                resumeMs = (fraction * durationMs).toLong()
                                positionMs = resumeMs
                                generation += 1
                                if (!playing) playing = true
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(Color.White.copy(alpha = 0.28f), RoundedCornerShape(2.dp)),
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth(
                                    if (durationMs <= 0) 0f
                                    else (positionMs.toFloat() / durationMs).coerceIn(0f, 1f),
                                )
                                .height(4.dp)
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp)),
                        )
                    }
                }
                Text(formatClock(durationMs), color = Color.White, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

/** 解格上限 fps：螢幕本來就 60Hz 上下，30 格夠順，也把 CPU 與複製量減一半（實測這條源是 60fps）。 */
private const val PlaybackFps = 30

/** 第一格等這麼久還是不來，就把 stderr 唸進日誌。 */
private const val NoFrameWatchdogMs = 6_000L

/** 落後音訊超過這個毫秒數的格才丟掉。 */
private const val StaleWindowMs = 500L

/** 行內連播的 fps：氣泡那麼小、又是靜音，15fps 看起來就夠（也把手機以外的 CPU 減一半）。 */
private const val InlineFps = 15

/** 行內連播起播前的等待：快速滾動時被滾走的氣泡不該各起一條 ffmpeg。 */
private const val InlineStartDelayMs = 250L

/** 「順手把整份檔抓下來改成本機播放」的檔案上限：再大的檔等它落地不如就看著串流卡。 */
private const val LocalSwitchMaxBytes = 200L * 1024 * 1024
private const val SampleRate = 48000

private fun formatClock(totalMs: Long): String = "%d:%02d".format(totalMs / 60_000, (totalMs / 1000) % 60)

/**
 * 把原檔**串流寫進磁碟**（不進記憶體：動輒幾十 MB，而我們的 heap 只有 384MB）。
 * 只走原站的公開下載端點——那條不需要憑證；家伺服器那側要憑證，
 * 而代理不給外部程序用（見 `MediaStream.desktop.kt`），抓不到就老實回 null。
 */
private fun downloadOriginToFile(
    mxcUrl: String,
    capBytes: Long,
    shouldContinue: () -> Boolean,
): File? = runCatching {
    val url = mxcToPublicUrl(mxcUrl) ?: return@runCatching null
    val connection = java.net.URL(url).openConnection() as? java.net.HttpURLConnection ?: return@runCatching null
    connection.connectTimeout = 8_000
    connection.readTimeout = 20_000
    connection.instanceFollowRedirects = true
    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) Nashira/1.0")
    if (connection.responseCode !in 200..299) return@runCatching null
    if (connection.contentLengthLong > capBytes) return@runCatching null
    val file = File.createTempFile("nashira-play-", ".bin").apply { deleteOnExit() }
    var total = 0L
    var overflow = false
    var aborted = false
    connection.inputStream.use { input ->
        file.outputStream().buffered(64 * 1024).use { output ->
            val chunk = ByteArray(64 * 1024)
            while (true) {
                val read = input.read(chunk)
                if (read <= 0) break
                total += read
                if (total > capBytes) {
                    overflow = true
                    break
                }
                // ⚠️ 這條是阻塞在 socket 上的，協程取消不會自動中斷它：
                // 用戶關掉檢視窗之後還繼續把幾十 MB 抓完、並在 /tmp 留一個沒人刪的檔。
                // 所以每讀一塊就问一次「還要不要」，不要了就刪掉半成品。
                if (!shouldContinue()) {
                    aborted = true
                    break
                }
                output.write(chunk, 0, read)
            }
        }
    }
    if (aborted || overflow || file.length() == 0L) {
        runCatching { file.delete() }
        null
    } else {
        file
    }
}.getOrNull()

private fun writePlayableFile(bytes: ByteArray): File? = runCatching {
    File.createTempFile("nashira-play-", ".bin").apply { writeBytes(bytes) }
}.getOrNull()

private class MovieInfo(
    val width: Int,
    val height: Int,
    val durationSec: Long,
    val hasAudio: Boolean,
)

/**
 * ffprobe 結果快取（鍵＝媒體來源＋第幾個串流候選）。
 * 只存「尺寸/長度/有沒有音軌」這種跟網址無關的事實——**不存網址**：
 * 本機串流代理那條 URL 每次都換一個 id，存下來下次就是條死路徑。
 * 上限 64 條，滿了整票清掉（媒體快取本來就是可丟的東西，不追求精確淘汰）。
 */
private object MovieProbeCache {
    private val entries = java.util.concurrent.ConcurrentHashMap<String, MovieInfo>()

    operator fun get(key: String): MovieInfo? = entries[key]

    operator fun set(key: String, value: MovieInfo) {
        if (entries.size >= 64) entries.clear()
        entries[key] = value
    }
}

/** 問尺寸與長度：解格要按顯示框縮，長度給進度條與拖動。網址與檔案都走同一條 ffprobe。 */
private fun probeMovie(source: String): MovieInfo? = readCmdOutput(
    timeoutSec = 10,
    command = listOf(
        "ffprobe", "-v", "error",
        "-show_entries", "format=duration:stream=codec_type,width,height",
        "-of", "default=noprint_wrappers=1",
        source,
    ),
)?.let { out ->
    var w = 0
    var h = 0
    var seconds = 0.0
    var audio = false
    out.lineSequence().map { it.trim() }.forEach { line ->
        when {
            line.startsWith("width=") -> w = line.removePrefix("width=").toIntOrNull() ?: w
            line.startsWith("height=") -> h = line.removePrefix("height=").toIntOrNull() ?: h
            line.startsWith("duration=") -> seconds = line.removePrefix("duration=").toDoubleOrNull() ?: seconds
            line.startsWith("codec_type=audio") -> audio = true
        }
    }
    if (w <= 0 || h <= 0) null else MovieInfo(w, h, seconds.toLong(), audio)
}

/**
 * 跑一條命令把 stdout 全收下來；超時或非零出口回 null。
 *
 * ⚠️ 讀 stdout 一定不能在當前執行緒上：`readBytes()` 會一直堵到程序自己結束，
 * 那樣後面的 `waitFor(timeout)` 根本沒機會跑——ffprobe 卡在一個慢連結上時，
 * 整個播放流程就永久轉圈（2026-10-08 檢查自己這段時發現的）。
 */
private fun readCmdOutput(command: List<String>, timeoutSec: Long = 15): String? = runCatching {
    val process = ProcessBuilder(command).redirectErrorStream(false).start()
    val collected = CompletableFuture.supplyAsync {
        runCatching { process.inputStream.use { it.readBytes() } }.getOrNull()
    }
    if (!process.waitFor(timeoutSec, TimeUnit.SECONDS)) {
        process.destroyForcibly()
        mediaProbe("命令超過 ${timeoutSec}s：${command.first()}")
        null
    } else if (process.exitValue() != 0) {
        null
    } else {
        collected.get(2, TimeUnit.SECONDS)?.decodeToString()
    }
}.getOrNull()

/** 兩個輸出共用的前半段：`-re` 讓 ffmpeg 自己按原速率輸出；`-ss` 放 `-i` 前面＝跳著讀，不用從頭解。 */
private fun ffmpegCommand(startMs: Long, source: String): List<String> = listOf(
    // `-y` 是必須的，不是修飾：wav 那路輸出是**檔案**，而我們用 createTempFile 先把它建出來了，
    // ffmpeg 看到輸出檔已存在就會在 stdin 上問「Overwrite? [y/N]」——stdin 不是終端機，
    // 於是整條行程永久卡在那個問題：wav 0 位元組、一格都不吐、畫面進度死在 0:00。
    // （實測證據：/tmp/nashira-play-err-*.log 內容就是
    //   `File '…wav' already exists. Overwrite? [y/N]`，用戶 2026-10-08「結果依舊」的真兇。）
    "ffmpeg", "-v", "error", "-y", "-re",
    "-ss", (startMs / 1000).toString(), "-i", source,
)

/**
 * 一趟播放：**一條** ffmpeg 同時出影與聲。`close()` 一定要呼叫，
 * 否則 ffmpeg 会挂在後面繼續吃 CPU 與頻寬（暫停與關閉都走這裡）。
 *
 * 聲音那路是「第二個輸出＝一個會成長的 wav 暫存檔」，我們尾讀它餵給 SourceDataLine。
 * 這樣同一個檔只抓一次；先前開兩條行程各抓一次，實測等於把他那條 ~150–280KB/s 的
 * 鏈路流量翻倍，影片（570KB/s）直接播成四分之一速。
 */
private class MovieDecoder private constructor(
    source: String,
    info: MovieInfo,
    maxW: Int,
    maxH: Int,
    startMs: Long,
    fps: Int,
    private val errLog: File,
    private val wavFile: File?,
) {
    // 等比縮到顯示框內，且**不放大**（原檔比框小就照原尺寸，白燒 CPU 沒意義）
    private val fit = minOf(maxW.toFloat() / info.width, maxH.toFloat() / info.height, 1f)
    private val outW = (info.width * fit).toInt().coerceAtLeast(16)
    private val outH = (info.height * fit).toInt().coerceAtLeast(16)
    private val frameBytes = outW * outH * 4
    private val buffer = ByteArray(frameBytes)

    private val process = ProcessBuilder(
        ffmpegCommand(startMs, source) + listOf(
            // 只取視訊、fps 封頂：不封頂就是每秒解 60 格、每格还要走三次複製，CPU 白燒一倍
            "-map", "0:v:0", "-an",
            "-vf", "scale=$outW:$outH,fps=$fps",
            "-f", "rawvideo", "-pix_fmt", "bgra", "pipe:1",
        ) + if (wavFile == null) emptyList() else listOf(
            // `-flush_packets 1`：隨寫隨刷。不給的話尾讀那端要等 stdio 緩衝塞滿才看到資料，
            // 聽起來就是聲音比畫面慢一大段。
            "-map", "0:a:0?", "-vn", "-ac", "2", "-ar", "$SampleRate",
            "-flush_packets", "1", "-f", "wav", wavFile.absolutePath,
        ),
    )
        // stderr 一定要有人收：丟給暫存檔而不是管線，否則管線塞滿會把 ffmpeg 一起卡死
        .apply {
            redirectError(errLog)
            // stdin 給 /dev/null：任何需要人回答的提示都要立刻拿到 EOF 變成「看得見的錯誤」，
            // 而不是無聲地永遠卡住（上面那個 `-y` 的教訓）。
            redirectInput(ProcessBuilder.Redirect.from(java.io.File("/dev/null")))
        }
        .start()

    private val input = DataInputStream(BufferedInputStream(process.inputStream, frameBytes))
    private val audioBytes = AtomicLong(0L)

    @Volatile
    private var stopped = false

    @Volatile
    private var audioReady = false

    private val audioThread: Thread? = wavFile?.let { file ->
        Thread {
            val line = openAudioLine()
            if (line == null) {
                mediaProbe("內嵌播放：開不到音訊輸出，這趟只會有畫面")
            } else {
                runCatching {
                    line.start()
                    audioReady = true
                    val raf = RandomAccessFile(file, "r")
                    val dataStart = waitForDataChunk(raf) { !stopped && process.isAlive }
                    if (dataStart >= 0) {
                        raf.seek(dataStart)
                        val payload = ByteArray(8192)
                        while (!stopped) {
                            val available = raf.length() - raf.filePointer
                            if (available <= 0) {
                                if (!process.isAlive) break
                                Thread.sleep(10)
                                continue
                            }
                            val want = minOf(available, payload.size.toLong()).toInt()
                            val read = raf.read(payload, 0, want)
                            if (read <= 0) {
                                Thread.sleep(5)
                                continue
                            }
                            line.write(payload, 0, read)
                            audioBytes.addAndGet(read.toLong())
                        }
                    } else {
                        mediaProbe("內嵌播放：wav 裡找不到 data 區塊，這趟沒聲音")
                    }
                    runCatching { raf.close() }
                }
                runCatching { line.stop() }
                runCatching { line.close() }
                audioReady = false
            }
        }.apply { isDaemon = true; name = "nashira-audio"; start() }
    }

    /** 讀一格到緩衝區；讀不完（到檔尾或行程被殺）回 false。 */
    fun readFrame(): Boolean = runCatching {
        input.readFully(buffer)
        true
    }.getOrDefault(false)

    /** 把剛讀到的那一格變成 Compose 的圖（每格新開一個 Bitmap，裝的是複製進去的像素）。 */
    fun toBitmap(): ImageBitmap? {
        val bitmap = Bitmap()
        return runCatching {
            val ok = bitmap.installPixels(
                ImageInfo(outW, outH, ColorType.BGRA_8888, ColorAlphaType.UNPREMUL),
                buffer,
                outW * 4,
            )
            require(ok)
            val image = Image.makeFromBitmap(bitmap)
            val result = image.toComposeImageBitmap()
            image.close()
            result
        }.getOrNull().also {
            runCatching { bitmap.close() }
        }
    }

    /** 音訊時鐘（毫秒）；沒音軌、或音訊線開不起來時回 null＝改用「解出第幾格」當時鐘。 */
    fun audioPositionMs(): Long? =
        if (wavFile == null || !audioReady) null else audioBytes.get() * 1000L / BytesPerAudioSecond

    /** 第一格迟迟不來時的主動交代：行程還活著卻沒輸出，多半是卡在什麼「等人回話」上。 */
    fun warnIfNoOutput() {
        runCatching {
            val err = runCatching { errLog.readText().trim() }.getOrNull().orEmpty()
            val alive = process.isAlive
            val exit = if (alive) "（還在跑）" else process.exitValue().toString()
            mediaProbe("內嵌播放：${NoFrameWatchdogMs}ms 內一格都沒有；alive=$alive exit=$exit stderr=${err.take(300).ifBlank { "(空)" }}")
        }
    }

    /** 讀到一半斷掉時，把 ffmpeg 的再見話說出來（正常播完是 0 出口，不噯）。 */
    fun reportFailure() {
        runCatching {
            if (!process.isAlive && process.exitValue() != 0) {
                mediaProbe("吐格行程挂了 exit=${process.exitValue()} ${errLog.readText().trim().takeLast(300)}")
            }
        }
    }

    fun close() {
        stopped = true
        runCatching { process.destroy() }
        // 幫浦執行緒可能正堵在 line.write 上（聲音還有半緩衝區沒放完）：
        // 等它一小拍再收，否則會累積一堆「對著已關閉的線寫資料」的執行緒。
        runCatching { audioThread?.join(300) }
        runCatching { errLog.delete() }
        runCatching { wavFile?.delete() }
    }

    companion object {
        // 48000 Hz × 16 位元 × 2 聲道 = 每秒 192000 位元組
        private const val BytesPerAudioSecond = SampleRate.toLong() * 2L * 2L

        /** 找 wav 的 `data` 區塊起點（本體在它後面 8 個位元組）。回 -1＝沒找到。 */
        private fun waitForDataChunk(raf: RandomAccessFile, alive: () -> Boolean): Long {
            val head = ByteArray(4096)
            // 沒有次數上限：串流剛開始時 ffmpeg 要先抓 moov（常在檔尾），
            // 前幾秒連一個音訊封包都寫不出來是正常的（實測 2026-10-08：
            // 原本 300 次×10ms 的上限一到，這趟播放就永久沒聲音）。
            while (alive()) {
                val size = raf.length()
                if (size >= 12) {
                    val take = minOf(size, head.size.toLong()).toInt()
                    raf.seek(0)
                    raf.readFully(head, 0, take)
                    for (i in 0..take - 4) {
                        if (head[i] == 'd'.code.toByte() && head[i + 1] == 'a'.code.toByte() &&
                            head[i + 2] == 't'.code.toByte() && head[i + 3] == 'a'.code.toByte()
                        ) {
                            return i + 8L
                        }
                    }
                }
                Thread.sleep(10)
            }
            return -1L
        }

        /** ffmpeg 不存在、暫存檔開不了……一律回 null，讓上面顯示封面就好，別把整個應用程式弄炸。 */
        fun create(
            source: String,
            info: MovieInfo,
            maxW: Int,
            maxH: Int,
            startMs: Long,
            audio: Boolean,
            fps: Int,
        ): MovieDecoder? = runCatching {
            val errLog = File.createTempFile("nashira-play-err-", ".log").apply { deleteOnExit() }
            val wav = if (audio) {
                File.createTempFile("nashira-play-audio-", ".wav").apply { deleteOnExit() }
            } else {
                null
            }
            MovieDecoder(source, info, maxW, maxH, startMs, fps, errLog, wav)
            }.onFailure { mediaProbe("建解碼器失敗：${it.message}") }.getOrNull()
    }
}

/** 音訊緩衝：預設那個太小（寫幾毫秒就滿、一直阻塞），聽起來就是聲音斷斷續續。 */
private const val AudioLineBytes = 48 * 1024

private fun openAudioLine(): SourceDataLine? = runCatching {
    val format = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, SampleRate.toFloat(), 16, 2, 4, SampleRate.toFloat(), false)
    (AudioSystem.getLine(DataLine.Info(SourceDataLine::class.java, format)) as? SourceDataLine)
        ?.takeIf { line -> runCatching { line.open(format, AudioLineBytes); true }.getOrDefault(false) }
}.getOrNull()
