package io.github.capricornus007.nashira

import androidx.compose.foundation.DarkDefaultContextMenuRepresentation
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LightDefaultContextMenuRepresentation
import androidx.compose.foundation.LocalContextMenuRepresentation
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.contextmenu.modifier.filterTextContextMenuComponents
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.github.capricornus007.nashira.i18n.Strings

/**
 * 桌面實作。兩件事：
 *
 * 1. [appendComposerFormatMenu] 在這裡變成「把框架內建那層關掉」——格式化項改由
 *    [ComposerMenuHost] 自己畫（要放快捷鍵欄與子選單，框架的 ContextMenuItem 裝不下）。
 * 2. [ComposerMenuHost] 自己收右鍵、自己開選單，外觀照 Telegram 桌面版。
 *
 * 教訓（2026-09-25 用戶截圖點名）：在外面另開一層選單、內建那層照樣彈出來，
 * 同一個位置疊兩層。所以這裡是把內建那層**清空**而不是再蓋一層。
 */
@OptIn(ExperimentalFoundationApi::class)
actual fun Modifier.appendComposerFormatMenu(
    strings: Strings,
    enabled: Boolean,
    onPick: (ComposerFormat) -> Unit,
): Modifier = filterTextContextMenuComponents { false }

/**
 * 把「其他」輸入框（搜尋框、對話框）的右鍵選單從 Swing 換成框架 Compose 繪製的那層。
 *
 * 1.12.0 的真實鏈路（反編譯 foundation-desktop 核過）：`TextContextMenu.Default` 是
 * `BasicTextContextMenu`，渲染時讀 `LocalContextMenuRepresentation`，而桌面端預設值是
 * `JPopupContextMenuRepresentation`——那是一層 Swing `JPopupMenu`，不看 Material 主題
 * （改 UIManager 的 PopupMenu.* 實測無效，用戶 2026-09-29 截圖仍是白底）。
 *
 * 草稿框不經過這裡：它走 [ComposerMenuHost]。
 */
@Composable
actual fun ProvideComposerContextMenu(content: @Composable () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val representation = if (scheme.isDarkSurface()) {
        DarkDefaultContextMenuRepresentation
    } else {
        LightDefaultContextMenuRepresentation
    }
    CompositionLocalProvider(
        LocalContextMenuRepresentation provides representation,
        content = content,
    )
}

/** 以 surface 的亮度判深淺：比再從上面傳一個 dark 旗標可靠，主題換了自動跟著換。 */
private fun androidx.compose.material3.ColorScheme.isDarkSurface(): Boolean = surface.luminance() < 0.5f

/** 列高與欄寬：快捷鍵要右對齊、子選單要對到父列那一條，算法需要確切高度。 */
private val MenuColumnWidth = 196.dp
private val MenuRowHeight = 32.dp
private val MenuSeparatorHeight = 11.dp
private const val MenuDisabledAlpha = 0.38f

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun ComposerMenuHost(
    state: TextFieldState,
    strings: Strings,
    content: @Composable () -> Unit,
) {
    val clipboard = LocalClipboardManager.current
    var pressAt by remember { mutableStateOf<Offset?>(null) }
    Box(
        modifier = Modifier.pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    if (event.type == PointerEventType.Press && event.buttons.isSecondaryPressed) {
                        val change = event.changes.firstOrNull() ?: continue
                        // Initial 通道：祖先比文字欄先拿到事件，吃掉它，內建那層就不會再彈
                        change.consume()
                        pressAt = change.position
                    }
                }
            }
        },
    ) {
        content()
        val at = pressAt
        if (at != null) {
            ComposerMenuPopup(
                items = buildComposerMenu(strings, state, clipboard) { format ->
                    applyComposerFormat(state, format)
                },
                offset = at,
                onDismiss = { pressAt = null },
            )
        }
    }
}

/**
 * 子選單用「同一個彈窗裡再多一欄」畫，不是再開一個 popup：
 * 巢狀 DropdownMenu 的父層會把落在子 popup 的點擊當成「點在窗外」而先關掉自己，
 * 結果點子選單會兩邊一起消失。同一個窗口沒這問題，位置也能自己對齊到父列。
 */
@Composable
private fun ComposerMenuPopup(
    items: List<ComposerMenuItem>,
    offset: Offset,
    onDismiss: () -> Unit,
) {
    val density = LocalDensity.current
    var openSubmenu by remember { mutableStateOf<String?>(null) }
    val submenu = items.firstOrNull { it.label == openSubmenu && it.submenu.isNotEmpty() }
    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismiss,
        offset = with(density) { DpOffset(offset.x.toDp(), offset.y.toDp()) },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 8.dp,
    ) {
        Row {
            MenuColumn(
                rows = items,
                openSubmenuLabel = openSubmenu,
                onSubmenu = { label -> openSubmenu = if (openSubmenu == label) null else label },
                onPick = { item ->
                    item.onClick()
                    onDismiss()
                },
            )
            if (submenu != null) {
                VerticalDivider(
                    modifier = Modifier.height(columnHeight(items)),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
                MenuColumn(
                    rows = submenu.submenu,
                    openSubmenuLabel = null,
                    onSubmenu = {},
                    onPick = { item ->
                        item.onClick()
                        onDismiss()
                    },
                    topPadding = submenuTop(items, submenu.submenu),
                )
            }
        }
    }
}

private fun rowHeight(row: ComposerMenuItem): Dp =
    if (row.isSeparator) MenuSeparatorHeight else MenuRowHeight

private fun columnHeight(rows: List<ComposerMenuItem>): Dp = rows.fold(0.dp) { acc, row -> acc + rowHeight(row) }

/** 子選單欄往下沉到父列那一條（跟 Telegram 一樣貼著點的那項），長出底部就往上收。 */
private fun submenuTop(rows: List<ComposerMenuItem>, submenu: List<ComposerMenuItem>): Dp {
    val index = rows.indexOfFirst { it.submenu.isNotEmpty() && it.submenu === submenu }
    if (index <= 0) return 0.dp
    val above = rows.subList(0, index).fold(0.dp) { acc, row -> acc + rowHeight(row) }
    val maxTop = (columnHeight(rows) - columnHeight(submenu)).coerceAtLeast(0.dp)
    return above.coerceAtMost(maxTop)
}

@Composable
private fun MenuColumn(
    rows: List<ComposerMenuItem>,
    openSubmenuLabel: String?,
    onSubmenu: (String) -> Unit,
    onPick: (ComposerMenuItem) -> Unit,
    topPadding: Dp = 0.dp,
) {
    Column(modifier = Modifier.width(MenuColumnWidth).padding(top = topPadding)) {
        rows.forEach { row ->
            if (row.isSeparator) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 5.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            } else {
                MenuRow(
                    row = row,
                    highlighted = row.submenu.isNotEmpty() && row.label == openSubmenuLabel,
                    onClick = {
                        if (row.submenu.isNotEmpty()) onSubmenu(row.label) else onPick(row)
                    },
                    onHover = { if (row.submenu.isNotEmpty()) onSubmenu(row.label) },
                )
            }
        }
    }
}

@Composable
private fun MenuRow(
    row: ComposerMenuItem,
    highlighted: Boolean,
    onClick: () -> Unit,
    onHover: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val contentColor = when {
        !row.enabled -> colorScheme.onSurface.copy(alpha = MenuDisabledAlpha)
        row.destructive -> colorScheme.error
        else -> colorScheme.onSurface
    }
    val hoverSource = remember { MutableInteractionSource() }
    LaunchedEffect(hoverSource) {
        hoverSource.interactions.collect { interaction ->
            if (interaction is HoverInteraction.Enter) onHover()
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(MenuRowHeight)
            .hoverable(hoverSource)
            .background(
                if (highlighted) colorScheme.surfaceVariant else Color.Transparent
            )
            .clickable(enabled = row.enabled, onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = row.label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
            maxLines = 1,
        )
        if (row.shortcut != null) {
            Text(
                text = row.shortcut,
                style = MaterialTheme.typography.labelSmall,
                color = if (row.enabled) colorScheme.outline else contentColor,
                maxLines = 1,
            )
        }
        if (row.submenu.isNotEmpty()) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = colorScheme.outline,
            )
        }
    }
}
