package io.github.capricornus007.nashira

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.capricornus007.nashira.i18n.stringsFor
import io.github.capricornus007.nashira.theme.NashiraGold
import io.github.capricornus007.nashira.theme.NashiraStarWhite
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip

/**
 * 啟動頁：磁碟有登入憑證、但 MatrixClient 還在開庫時顯示。
 *
 * **形態是「聊天室清單的骨架」而不是品牌啟動畫面**。實機錄影逐幀對照
 * moregramX(Telegram)：它 1.5 秒就畫出應用框架（標題列＋底部欄＋空清單），
 * 2.0 秒填上本機快取的清單，同步狀態只用標題列的一行小字提示，
 * 全程沒有佔滿畫面的轉圈。Nashira 原本擺一張居中品牌圖＋轉圈到開庫結束，
 * 同樣的等待時間會被讀成「還沒開始同步」。骨架跟真清單同構，切換時不跳版。
 */
@Composable
fun StartupScreen() {
    val strings = stringsFor(LocalUiState.current.language)
    Column(modifier = Modifier.fillMaxSize()) {
        // 標題列：跟 ChannelPane 同高同位置，真清單接上時不位移
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            NashiraMark(Modifier.size(24.dp))
            Text(
                strings.allRooms,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )
            Text(
                strings.restoringSession,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        // 搜尋膠囊佔位
        Box(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)
                .height(40.dp).clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        )
        // 清單列佔位：頭像圓 + 兩行長條，尺寸對齊 RoomListItem
        RoomListSkeleton()
        Box(Modifier.fillMaxWidth().weight(1f))
        SweepIndicator()
    }
}

/** 骨架列數：填滿一屏就夠，多畫只是浪費。 */
private const val SkeletonRows = 8

/**
 * 聊天室清單骨架列。啟動頁與「清單還沒到、但已經在同步」的階段共用同一份，
 * 讓整段等待看起來是「內容正在長出來」而不是「空的 + 轉圈」
 *（逐幀對照 Telegram：它全程沒有佔畫面的轉圈）。
 */
@Composable
fun RoomListSkeleton(modifier: Modifier = Modifier, rows: Int = SkeletonRows) {
    Column(modifier) {
        repeat(rows) { index ->
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    Modifier.size(44.dp).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SkeletonBar(width = (150 - (index % 3) * 22).dp, height = 13.dp)
                    SkeletonBar(width = (210 - (index % 4) * 26).dp, height = 11.dp)
                }
            }
        }
    }
}

@Composable
private fun SkeletonBar(width: androidx.compose.ui.unit.Dp, height: androidx.compose.ui.unit.Dp) {
    Box(
        Modifier.width(width).height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
    )
}

/**
 * 品牌標記：N 星圖三線 + 三顆星點 + 右上角金星，跟 launcher icon 同源。
 * 用 Canvas 直接畫，避免為啟動頁引入向量資源與平台差異。
 */
@Composable
private fun NashiraMark(modifier: Modifier) {
    Canvas(modifier) {
        // 圖示元素在 108 格中集中在 29..80，把這個 box 放大填滿畫布，
        // 直接照抄 launcher 座標會只用到中間一小塊，標記看起來過小。
        val src = 54f
        val originX = 27f
        val originY = 20f
        val scale = size.minDimension / src
        fun p(x: Float, y: Float) = Offset((x - originX) * scale, (y - originY) * scale)
        val stroke = 3f * scale

        // 金星光暈。launcher 圖示畫在深藍漸層底上，平塗圓形融得進去；
        // 直接畫在頁面表面時平塗會露出硬邊，所以改用徑向漸層做真正的暈散。
        val haloRadius = 18f * scale
        drawCircle(
            brush = androidx.compose.ui.graphics.Brush.radialGradient(
                colors = listOf(NashiraGold.copy(alpha = 0.16f), Color.Transparent),
                center = p(66.7f, 35.4f),
                radius = haloRadius,
            ),
            radius = haloRadius,
            center = p(66.7f, 35.4f),
        )
        // N 的三條線
        listOf(
            p(44.4f, 39.9f) to p(44.4f, 68.1f),
            p(44.4f, 39.9f) to p(63.6f, 68.1f),
            p(63.6f, 39.9f) to p(63.6f, 68.1f),
        ).forEach { (from, to) ->
            drawLine(color = StarBlue, start = from, end = to, strokeWidth = stroke, cap = StrokeCap.Round)
        }
        // 三顆白星點
        listOf(p(44.4f, 39.9f), p(44.4f, 68.1f), p(63.6f, 68.1f)).forEach {
            drawCircle(color = NashiraStarWhite, radius = 2.6f * scale, center = it)
        }
        // 右上金星（四角星）
        val star = androidx.compose.ui.graphics.Path().apply {
            moveTo(p(66.7f, 23.6f).x, p(66.7f, 23.6f).y)
            lineTo(p(69.7f, 32.4f).x, p(69.7f, 32.4f).y)
            lineTo(p(78.5f, 35.4f).x, p(78.5f, 35.4f).y)
            lineTo(p(69.7f, 38.4f).x, p(69.7f, 38.4f).y)
            lineTo(p(66.7f, 47.2f).x, p(66.7f, 47.2f).y)
            lineTo(p(63.7f, 38.4f).x, p(63.7f, 38.4f).y)
            lineTo(p(54.9f, 35.4f).x, p(54.9f, 35.4f).y)
            lineTo(p(63.7f, 32.4f).x, p(63.7f, 32.4f).y)
            close()
        }
        drawPath(star, NashiraGold)
    }
}

private val StarBlue = Color(0xFFAEC1F5)

/** 底部細線指示：一段金色線段在軌道上來回掠過，比轉圈更安靜。 */
@Composable
private fun SweepIndicator() {
    val transition = rememberInfiniteTransition(label = "startup_sweep")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "sweep",
    )
    val track = MaterialTheme.colorScheme.surfaceContainerHighest
    val head = MaterialTheme.colorScheme.primary
    Canvas(Modifier.size(width = 120.dp, height = 3.dp)) {
        val y = size.height / 2f
        drawLine(track, Offset(0f, y), Offset(size.width, y), strokeWidth = size.height, cap = StrokeCap.Round)
        val segment = size.width * 0.32f
        val start = (size.width - segment) * progress
        drawLine(
            head,
            Offset(start, y),
            Offset(start + segment, y),
            strokeWidth = size.height,
            cap = StrokeCap.Round,
        )
    }
}
