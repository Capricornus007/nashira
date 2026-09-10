package io.github.capricornus007.nashira

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip

/**
 * 啟動頁：磁碟有登入憑證、但 MatrixClient 還在開庫時顯示。
 *
 * **形態對照真機冷啟動錄屏（2026-09-07）**：Telegram 與 Discord 都是
 * 「全屏 splash——品牌標記 + 置中轉圈 + 一行小字」，載入指示在正中央
 * （y50%），不是中上也不是中下。骨架清單形態曾在此用過，實測被評
 * 「太醜」；骨架只留在 ChannelPane（清單已出現、還在同步的階段）。
 */
@Composable
fun StartupScreen(message: String? = null) {
    val strings = stringsFor(LocalUiState.current.language)
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            NashiraMark(Modifier.size(72.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                strokeWidth = 3.dp,
                color = NashiraGold,
            )
            Text(
                message ?: strings.restoringSession,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
/** 骨架列數：填滿一屏就夠，多畫只是浪費。 */
private const val SkeletonRows = 8

/**
 * 聊天室清單骨架列。只用在 ChannelPane「清單尚未到、但已經在同步」的階段；
 * StartupScreen 本身是全屏置中 splash，不再渲染這份骨架。
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

