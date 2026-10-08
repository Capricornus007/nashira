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
import kotlinx.coroutines.isActive
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

    DisposableEffect(tempFile) {
        val stale = tempFile
        onDispose { stale?.let { runCatching { it.delete() } } }
    }

    LaunchedEffect(client, source, bytes != null) {
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
            candidates.forEachIndexed { index, candidate ->
                if (resolved != null) return@forEachIndexed
                val probed = probeMovie(candidate)
                if (probed != null) resolved = candidate to probed
                else mediaProbe("內嵌播放：串流候選 ${index + 1} 探測不通")
            }
            resolved?.let { (where, meta) ->
                mediaProbe("內嵌播放：走串流（第 ${candidates.indexOf(where) + 1} 個候選）")
                input = where
                info = meta
                return@withContext
            }
            // 兩條串流都不通才退回整份檔：檢視器手上那份（可能還沒抓到、甚至是 0 位元組），
            // 再不通就直接向原站要一份。
            val data = bytes?.takeIf { it.isNotEmpty() }
                ?: (source as? MediaSource.Plain)?.let { downloadBytesFromOrigin(it.mxcUrl) }?.takeIf { it.isNotEmpty() }
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
                tempFile = file
                input = file.absolutePath
                info = probed
            }
        }
    }

    LaunchedEffect(input, info, playing, generation, resumeMs) {
        val sourcePath = input ?: return@LaunchedEffect
        val meta = info ?: return@LaunchedEffect
        if (!playing) return@LaunchedEffect
        val startMs = resumeMs
        val ended = withContext(Dispatchers.IO) {
            val decoder = MovieDecoder.create(sourcePath, meta, maxW, maxH, startMs)
                ?: return@withContext false
            var done = false
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
                        decoder.toBitmap()?.let { frame = it }
                        positionMs = startMs + (audioMs ?: frameMs)
                    }
                    index++
                    if (!isActive) break
                }
            } finally {
                decoder.close()
            }
            done && isActive
        }
        if (ended) {
            // 播到檔尾：起點歸零，讓他再點播放鈕能從頭看（停在尾端會一點就立刻又結束）
            playing = false
            resumeMs = 0L
            positionMs = durationMs
        }
    }

    Box(
        modifier
            .fillMaxSize()
            .pointerInput(playing, generation, input) {
                // 點畫面＝播放／暫停（Telegram 桌面端就是這個手勢）。這裡要把事件吃掉，
                // 否則會被外層「點背景關閉」的監聽一起收走——點一下圖就關掉視窗很怪。
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

/** 落後音訊超過這個毫秒數的格才丟掉。 */
private const val StaleWindowMs = 500L
private const val SampleRate = 48000

private fun formatClock(totalMs: Long): String = "%d:%02d".format(totalMs / 60_000, (totalMs / 1000) % 60)

private fun writePlayableFile(bytes: ByteArray): File? = runCatching {
    File.createTempFile("nashira-play-", ".bin").apply { writeBytes(bytes) }
}.getOrNull()

private class MovieInfo(
    val width: Int,
    val height: Int,
    val durationSec: Long,
    val hasAudio: Boolean,
)

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
    "ffmpeg", "-v", "error", "-re",
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
            "-vf", "scale=$outW:$outH,fps=$PlaybackFps",
            "-f", "rawvideo", "-pix_fmt", "bgra", "pipe:1",
        ) + if (wavFile == null) emptyList() else listOf(
            // `-flush_packets 1`：隨寫隨刷。不給的話尾讀那端要等 stdio 緩衝塞滿才看到資料，
            // 聽起來就是聲音比畫面慢一大段。
            "-map", "0:a:0?", "-vn", "-ac", "2", "-ar", "$SampleRate",
            "-flush_packets", "1", "-f", "wav", wavFile.absolutePath,
        ),
    ).redirectError(errLog).start()

    // stderr 一定要有人收：丟給暫存檔而不是管線，否則管線塞滿會把 ffmpeg 一起卡死
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
                    val dataStart = waitForDataChunk(raf)
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
        private fun waitForDataChunk(raf: RandomAccessFile): Long {
            val head = ByteArray(4096)
            var tries = 0
            while (tries < 300) {
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
                tries++
                Thread.sleep(10)
            }
            return -1L
        }

        /** ffmpeg 不存在、暫存檔開不了……一律回 null，讓上面顯示封面就好，別把整個應用程式弄炸。 */
        fun create(source: String, info: MovieInfo, maxW: Int, maxH: Int, startMs: Long): MovieDecoder? =
            runCatching {
                val errLog = File.createTempFile("nashira-play-err-", ".log").apply { deleteOnExit() }
                val wav = if (info.hasAudio) {
                    File.createTempFile("nashira-play-audio-", ".wav").apply { deleteOnExit() }
                } else {
                    null
                }
                MovieDecoder(source, info, maxW, maxH, startMs, errLog, wav)
            }.onFailure { mediaProbe("建解碼器失敗：${it.message}") }.getOrNull()
    }
}

private fun openAudioLine(): SourceDataLine? = runCatching {
    val format = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, SampleRate.toFloat(), 16, 2, 4, SampleRate.toFloat(), false)
    (AudioSystem.getLine(DataLine.Info(SourceDataLine::class.java, format)) as? SourceDataLine)
        ?.takeIf { line -> runCatching { line.open(format); true }.getOrDefault(false) }
}.getOrNull()
