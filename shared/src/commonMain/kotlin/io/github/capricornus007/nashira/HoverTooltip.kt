package io.github.capricornus007.nashira

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlin.math.roundToInt

/**
 * 懸停時在元素下方浮出一行說明。
 *
 * 桌面版 Compose 1.12 **沒有** tooltip API（把 ui-desktop 的 jar 解開 grep `tooltipText`
 * 查無此符號），所以自己用 Popup 畫。用途：把「下載失敗」這種一律同一句話的標示，
 * 補上真正的失敗原因（用戶 2026-09-30：「那你就做個懸浮顯示具體原因」）。
 */
@Composable
fun HoverTooltip(
    text: String?,
    modifier: Modifier = Modifier,
    maxLines: Int = 3,
    content: @Composable () -> Unit,
) {
    if (text.isNullOrBlank()) {
        Box(modifier) { content() }
        return
    }
    val source = remember(text) { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val density = LocalDensity.current
    // Popup 的 offset 是「相對整個窗口」的像素座標，所以要先量元素在窗口裡的位置
    var topLeft by remember(text) { mutableStateOf(Offset.Zero) }
    var heightPx by remember(text) { mutableStateOf(0f) }
    Box(
        modifier
            .onGloballyPositioned { node ->
                val bounds = node.boundsInWindow()
                topLeft = bounds.topLeft
                heightPx = bounds.height
            }
            .hoverable(source),
        contentAlignment = Alignment.Center,
    ) { content() }
    if (!hovered) return
    val gap = with(density) { 10.dp.toPx() }
    Popup(
        alignment = Alignment.TopStart,
        offset = IntOffset((topLeft.x + gap).roundToInt(), (topLeft.y + heightPx + 4f).roundToInt()),
        properties = PopupProperties(focusable = false),
    ) {
        Box(
            Modifier
                .background(MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(8.dp))
                .padding(horizontal = 9.dp, vertical = 6.dp),
        ) {
            Text(
                text,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.inverseOnSurface,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
