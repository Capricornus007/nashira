package io.github.capricornus007.nashira

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import de.connect2x.trixnity.client.MatrixClient
import io.github.capricornus007.nashira.matrix.AudioPlayer
import io.github.capricornus007.nashira.matrix.MediaSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * P5-1 時間線裡的語音氣泡：播放／暫停鍵＋時長＋進度條。
 * - 位元組進 [VoiceBytesCache]（mxc 鍵、32MB 上限），來回滾動不重抓
 * - 首次組合即背景抓檔；抓不到顯示時長文字，不給播放鍵
 * - 播放器在離開組合時釋放
 */
@Composable
fun VoiceBubble(
    client: MatrixClient,
    source: MediaSource,
    durationMs: Long?,
    mimeType: String?,
    isOwn: Boolean,
    modifier: Modifier = Modifier,
    fetchFailedLabel: String? = null,
    unsupportedLabel: String? = null,
) {
    val player = remember(source) { AudioPlayer() }
    DisposableEffect(source) {
        onDispose { player.release() }
    }
    var bytes by remember(source) { mutableStateOf(VoiceBytesCache.get(source)) }
    var failed by remember(source) { mutableStateOf(false) }
    var playing by remember(source) { mutableStateOf(false) }
    var position by remember(source) { mutableStateOf(0L) }
    var prepared by remember(source) { mutableStateOf(false) }

    LaunchedEffect(source) {
        if (bytes == null && !failed) {
            val fetched = withContext(Dispatchers.Default) { fetchMediaBytes(client, source) }
            if (fetched == null) failed = true else {
                VoiceBytesCache.put(source, fetched)
                bytes = fetched
            }
        }
    }

    // 播放中的進度輪詢；自然播完（isPlaying 轉 false）回起點
    LaunchedEffect(playing) {
        while (playing) {
            position = player.positionMs().coerceAtLeast(0)
            if (!player.isPlaying()) {
                playing = false
                position = 0
                prepared = false
            }
            delay(200)
        }
    }

    // 事件帶的時長最可靠；播放器備援（ffplay 拿不到，回 -1 就不畫進度）
    val total = durationMs ?: player.durationMs()

    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(
                if (isOwn) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceContainerHigh,
            )
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when {
            failed -> Text(
                (if (bytes == null) fetchFailedLabel else unsupportedLabel) ?: formatVoiceDuration(total),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
            bytes == null -> Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                Text(
                    formatVoiceDuration(total),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            else -> {
                Box(
                    Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isOwn) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.secondaryContainer,
                        )
                        .clickable {
                            if (playing) {
                                player.pause()
                                playing = false
                            } else {
                                val current = bytes ?: return@clickable
                                // 暫停後續播用同一個 player；播完（prepared=false）才重備
                                if (!prepared) {
                                    prepared = player.prepare(current, mimeType)
                                    if (!prepared) {
                                        failed = true
                                        return@clickable
                                    }
                                }
                                player.play()
                                playing = player.isPlaying()
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        if (playing) VoiceIcons.Pause else Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = if (isOwn) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Column(Modifier.padding(start = 6.dp).width(96.dp)) {
                    Text(
                        formatVoiceDuration(if (playing) position else total),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isOwn) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurface,
                    )
                    if (total > 0) {
                        val target = (position.toFloat() / total).coerceIn(0f, 1f)
                        val animated by animateFloatAsState(target, animationSpec = tween(150), label = "voice_pos")
                        LinearProgressIndicator(
                            progress = { animated },
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                            trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                        )
                    }
                }
            }
        }
    }
}

internal fun formatVoiceDuration(ms: Long): String {
    if (ms <= 0) return "0:00"
    val totalSeconds = ms / 1000
    return "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}

/** 語音位元組的進程內快取：上限 32MB（m4a 64kbps 一分鐘約 0.5MB，夠幾十條）。 */
internal object VoiceBytesCache {
    private const val MaxBytes = 32L * 1024 * 1024
    private val lock = Any()
    private val map = LinkedHashMap<String, ByteArray>()

    private fun key(source: MediaSource): String = when (source) {
        is MediaSource.Plain -> source.mxcUrl
        is MediaSource.Encrypted -> source.file.url
    }

    fun get(source: MediaSource): ByteArray? = synchronized(lock) { map[key(source)] }

    fun put(source: MediaSource, bytes: ByteArray) {
        synchronized(lock) {
            map[key(source)] = bytes
            var size = map.values.sumOf { it.size.toLong() }
            val iter = map.entries.iterator()
            while (size > MaxBytes && iter.hasNext()) {
                size -= iter.next().value.size.toLong()
                iter.remove()
            }
        }
    }
}
