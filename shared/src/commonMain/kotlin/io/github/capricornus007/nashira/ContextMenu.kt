package io.github.capricornus007.nashira

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset

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
                    // 位置要一起帶出去：桌面選單要開在指標處，不然滑鼠在右邊、選單卻從列首彈出
                    val position = event.changes.firstOrNull()?.position ?: Offset.Zero
                    event.changes.forEach { it.consume() }
                    onContextMenu(position)
                }
            }
        }
    }
    // 觸控長按沒有「指標位置」的概念，用 Offset.Unspecified 表示「照預設位置開」
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
    /** 右鍵按下的位置（相對於錨點元件）；Unspecified 表示照 DropdownMenu 預設位置。 */
    anchor: Offset = Offset.Unspecified,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    // DropdownMenu 會自動避開畫面邊界（不夠位就往上／往左翻），所以只要給位移就不會被切掉
    val offset = remember(anchor, density) {
        if (anchor.isSpecified) {
            with(density) { DpOffset(anchor.x.toDp(), anchor.y.toDp()) }
        } else {
            DpOffset.Zero
        }
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        offset = offset,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        shadowElevation = 8.dp,
    ) {
        content()
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
