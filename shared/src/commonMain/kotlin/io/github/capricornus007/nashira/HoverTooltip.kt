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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

/**
 * 懸停時在元素下方浮出一行說明。
 *
 * 桌面版 Compose 1.12 **沒有** tooltip API（把 ui-desktop 的 jar 解開 grep `tooltipText`
 * 查無此符號），得自己畫。**不要改用 `Popup`**：Popup 的 offset 走的是另一套座標系，
 * 實測同一個錨點會飄到視窗另一邊（用戶 2026-09-30：「爲什麼它這個懸浮提示也要錯位？」）。
 * 畫在同一個 Box 裡就永遠貼著錨點，也不受窗口縮放／密度換算影響。
 */
@Composable
fun HoverTooltip(
    text: String?,
    modifier: Modifier = Modifier,
    maxLines: Int = 2,
    content: @Composable () -> Unit,
) {
    if (text.isNullOrBlank()) {
        Box(modifier) { content() }
        return
    }
    val source = remember(text) { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    // zIndex：本 Box 在父層（訊息列的文字欄）裡要蓋住後面的兄弟節點，否則說明條會被
    // 訊息本體那一段畫過去
    Box(modifier.zIndex(4f).hoverable(source)) {
        content()
        if (hovered) {
            Box(
                Modifier
                    .align(Alignment.BottomStart)
                    // `align(BottomStart)` 是把說明的**下緣**對到元素下緣，於是它往上蓋住
                    // 元素自己（用戶 2026-10-07 截圖 #41/#42：「跳到原始訊息」那條提示壓在
                    // 引用列上、看不清被蓋住的內容）。往下推「自己那個高度」才真正落到下方。
                    // 用 graphicsLayer 而不是 offset＋量尺寸：尺寸在繪製時才拿得到，
                    // 這樣第一幀就在對的位置，不會先閃一下重疊。
                    .graphicsLayer { translationY = size.height + 6.dp.toPx() }
                    .background(MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(8.dp))
                    .padding(horizontal = 9.dp, vertical = 5.dp),
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
}

/** 沒有懸停說明時，直接沿用原來的容器。 */
internal val NoTooltipColor: Color = Color.Transparent
