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

/**
 * 啟動頁：磁碟有登入憑證時顯示。
 *
 * 形態參考 SchildiChat 的啟動畫面（居中品牌標記 + 底部細進度條），但用自己的品牌語彙：
 * 品牌圖示是 N 星圖，載入指示是一條金色細線在底部來回掠過，而不是轉圈。
 */
@Composable
fun StartupScreen() {
    val strings = stringsFor(LocalUiState.current.language)
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NashiraMark(Modifier.size(96.dp))
        Text(
            strings.appName,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
        )
        Text(
            strings.restoringSession,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SweepIndicator()
    }
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
