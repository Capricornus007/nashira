package io.github.capricornus007.nashira

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/** 小於這個大小沒必要壓（省一趟行程與幾秒等待，代價比好處大）。 */
private const val SendCompressMinBytes = 4 * 1024 * 1024

/** 壓一條影片的等待上限；超過就傳原檔，不把使用者卡在「發送中」。 */
private const val SendCompressTimeoutSec = 240L

internal actual suspend fun compressVideoForSending(picked: PickedFile): PickedFile {
    if (!picked.mimeType.startsWith("video/")) return picked
    if (picked.bytes.size < SendCompressMinBytes) return picked
    return withContext(Dispatchers.IO) { runSendCompress(picked) } ?: picked
}

/**
 * 桌面：ffmpeg 壓成 H.264/AAC 的 mp4（長邊封頂 1280、CRF 28、`+faststart`）。
 * 回 null 代表「這趟別壓」（沒有 ffmpeg、編譯器不支援、逾時、壓完反而更大），
 * 呼叫端照原檔送出。
 */
private fun runSendCompress(picked: PickedFile): PickedFile? = runCatching {
    val src = File.createTempFile("nashira-send-", ".bin").apply {
        writeBytes(picked.bytes)
        deleteOnExit()
    }
    val dst = File.createTempFile("nashira-send-", ".mp4").apply { deleteOnExit() }
    val process = ProcessBuilder(
        "ffmpeg", "-v", "error", "-y", "-i", src.absolutePath,
        "-map", "0:v:0",
        // 長邊封頂 1280；`-2` 是「照比例算、但保證是偶數」——H.264 不吃奇數寬高。
        // 單引號是 ffmpeg 自己的 filter 解析吃的，不是 shell（ProcessBuilder 不過 shell）。
        "-vf", "scale=w='if(gt(iw,ih),min(1280,iw),-2)':h='if(gt(iw,ih),-2,min(1280,ih))'",
        "-c:v", "libx264", "-preset", "veryfast", "-crf", "28", "-pix_fmt", "yuv420p",
        "-c:a", "aac", "-b:a", "96k",
        // moov 搬到檔頭：收到的人不必等整檔就能開播（mp4 的 moov 在檔尾時串流解不出第一格）
        "-movflags", "+faststart",
        dst.absolutePath,
    ).redirectErrorStream(true).withoutLauncherEnv().start()

    // ⚠️ 一定要另外收 stdout／stderr：行程的輸出塞滿管線緩衝就會卡死（這個坑在播放那側踩過）
    val log = CompletableFuture.supplyAsync {
        runCatching { process.inputStream.use { it.readBytes().decodeToString() } }.getOrNull()
    }
    if (!process.waitFor(SendCompressTimeoutSec, TimeUnit.SECONDS)) {
        process.destroyForcibly()
        mediaProbe("發送壓縮：超過 ${SendCompressTimeoutSec}s，改傳原檔")
        return@runCatching null
    }
    val output = runCatching { log.get(3, TimeUnit.SECONDS) }.getOrNull().orEmpty()
    if (process.exitValue() != 0) {
        mediaProbe("發送壓縮失敗 exit=${process.exitValue()} ${output.trim().takeLast(200)}，改傳原檔")
        return@runCatching null
    }
    val compressed = runCatching { dst.readBytes() }.getOrNull()
    runCatching { src.delete() }
    runCatching { dst.delete() }
    if (compressed == null || compressed.isEmpty() || compressed.size >= picked.bytes.size) {
        mediaProbe("發送壓縮：沒有比較小（${picked.bytes.size} → ${compressed?.size ?: 0}），傳原檔")
        return@runCatching null
    }
    mediaProbe("發送壓縮：${picked.bytes.size} → ${compressed.size} 位元組")
    val base = picked.fileName.substringBeforeLast('.').ifBlank { "video" }
    PickedFile(compressed, "video/mp4", "$base.mp4")
}.onFailure { mediaProbe("發送壓縮異常：${it::class.simpleName} ${it.message}，改傳原檔") }.getOrNull()
