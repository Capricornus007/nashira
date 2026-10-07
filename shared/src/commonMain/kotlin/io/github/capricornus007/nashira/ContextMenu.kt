package io.github.capricornus007.nashira

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import kotlin.math.roundToInt

/**
 * 長按（觸控）與右鍵（滑鼠）都能開的內容選單觸發器。
 *
 * 兩條路都要：`combinedClickable` 的 onLongClick 只在觸控與長按滑鼠左鍵時發，
 * 桌面使用者按的是右鍵；反過來 `isSecondaryPressed` 在觸控上永遠不成立。
 * 右鍵事件在 Main pass 就消費掉，避免同一下又觸發列的一般點擊。
 */
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.contextMenuGestures(
    onClick: (() -> Unit)? = null,
    onContextMenu: (Offset) -> Unit,
): Modifier = this
    .pointerInput(onContextMenu) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Main)
                if (event.type == PointerEventType.Press && event.buttons.isSecondaryPressed) {
                    // 位置要一起帶出去：桌面選單要開在指標處，不然滑鼠在右邊、選單卻從列首彈出。
                    // 這裡給的是「距本節點頂端」的局部座標，**不要再做任何修正**：
                    // 換算成視窗絕對座標、以及「放不下就往回挪」全部由 ContextMenuSurface 負責。
                    val position = event.changes.firstOrNull()?.position ?: Offset.Zero
                    event.changes.forEach { it.consume() }
                    onContextMenu(position)
                }
            }
        }
    }
    // 觸控長按沒有「指標位置」的概念，用 Offset.Unspecified 表示「開在列下方」（選單自己算）
    .combinedClickable(
        onClick = { onClick?.invoke() },
        onLongClick = { onContextMenu(Offset.Unspecified) },
    )

/** 選單的一列；`destructive` 用錯誤色（離開房間、刪除訊息這類）。 */
@Composable
fun ContextMenuItem(
    label: String,
    destructive: Boolean = false,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Text(
                label,
                color = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            )
        },
        onClick = onClick,
    )
}

/** 統一外觀的內容選單容器：比卡片高一階的底色 + 陰影，才不會跟列表融在一起。 */
@Composable
fun ContextMenuSurface(
    expanded: Boolean,
    onDismiss: () -> Unit,
    /** 右鍵按下的位置，相對於「掛這個選單的那個容器」；Unspecified 表示開在容器下方。 */
    anchor: Offset = Offset.Unspecified,
    content: @Composable () -> Unit,
) {
    if (!expanded) return
    Popup(
        // Popup 的 anchor 就是呼叫端最近的父節點（各處都是那個包著列的 Box），
        // 所以 provider 收到的 anchorBounds 已經是容器在視窗裡的位置，不必自己量。
        popupPositionProvider = remember(anchor) { PointerPopupPositionProvider(anchor) },
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true, usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 3.dp,
            shadowElevation = 8.dp,
        ) {
            Column(Modifier.widthIn(min = 180.dp)) { content() }
        }
    }
}

/**
 * 選單開在指標處，並保證整塊留在視窗內。
 *
 * 為什麼不用 `DropdownMenu` 的 `offset`（2026-10-07 換掉的）：它先按「錨點左下角」定位，
 * **下方空間不夠時會自動往上翻**，而我給的 offset 在翻轉之後照樣往下加 →
 * 靠上的列看起來剛好、靠底的列整塊飛掉，用戶截圖裡兩處症狀其實是同一件事。
 * 自己算絕對座標就沒有「翻轉後語意改變」這個坑，橫向越界也一起夾住。
 */
private class PointerPopupPositionProvider(
    private val anchor: Offset,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val left = if (anchor.isSpecified) anchorBounds.left + anchor.x.roundToInt() else anchorBounds.left
        val top = if (anchor.isSpecified) anchorBounds.top + anchor.y.roundToInt() else anchorBounds.bottom
        // 先夾右下界再夾 0：選單比視窗還大時（極窄視窗）退回左上角，不會跑到負座標
        val maxX = (windowSize.width - popupContentSize.width).coerceAtLeast(0)
        val maxY = (windowSize.height - popupContentSize.height).coerceAtLeast(0)
        return IntOffset(left.coerceAtMost(maxX), top.coerceAtMost(maxY))
    }
}

/** 邀請對話框：輸入 Matrix ID。格式明顯不對就不讓按確定，省一次伺服器往返。 */
@Composable
fun InviteDialog(
    strings: io.github.capricornus007.nashira.i18n.Strings,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var input by remember { mutableStateOf("") }
    val valid = input.startsWith("@") && input.contains(':')
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.actionInvite) },
        text = {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it.trim() },
                singleLine = true,
                label = { Text(strings.inviteHint) },
            )
        },
        confirmButton = {
            TextButton(enabled = valid, onClick = { onConfirm(input) }) { Text(strings.actionInvite) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(strings.cancel) } },
    )
}
