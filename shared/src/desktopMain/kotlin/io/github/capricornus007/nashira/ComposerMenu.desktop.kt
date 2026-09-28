package io.github.capricornus007.nashira

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.contextmenu.modifier.appendTextContextMenuComponents
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import io.github.capricornus007.nashira.i18n.Strings
import javax.swing.UIManager
import java.awt.Color as AwtColor

/**
 * 桌面實作：追加到內建選單尾端。自己掛 pointerInput 再開 DropdownMenu 會跟內建那層
 * 疊成兩層（2026-09-25 用戶截圖點名），所以格式化項走這條路。
 */
@OptIn(ExperimentalFoundationApi::class)
actual fun Modifier.appendComposerFormatMenu(
    strings: Strings,
    enabled: Boolean,
    onPick: (ComposerFormat) -> Unit,
): Modifier = if (!enabled) {
    this
} else {
    appendTextContextMenuComponents { appendComposerFormatItems(strings, onPick) }
}

/**
 * 輸入框的右鍵選單在桌面端是 Swing 的 JPopupMenu：Compose 1.12 那層的
 * `ProvideBasicTextContextMenu` 與 item 型別都是 internal（反編譯 foundation-desktop
 * 核過），外部換不掉「怎麼畫」，也讀不到清單。但它是在繪製時才讀 UIManager，
 * 所以把這幾組預設值跟著 Material 主題同步過去就能解決白底白字
 * （用戶 2026-09-28 點名）。主題切換時這個 composable 會重跑，顏色跟著變。
 */
@Composable
actual fun ProvideComposerContextMenu(content: @Composable () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    SideEffect {
        applySwingMenuColors(scheme)
    }
    content()
}

private fun applySwingMenuColors(scheme: ColorScheme) {
    val surface = AwtColor(scheme.surfaceContainerHigh.toArgb())
    val text = AwtColor(scheme.onSurface.toArgb())
    val muted = AwtColor(scheme.onSurface.copy(alpha = 0.38f).toArgb())
    val selected = AwtColor(scheme.secondaryContainer.toArgb())
    val selectedText = AwtColor(scheme.onSecondaryContainer.toArgb())
    val divider = AwtColor(scheme.outlineVariant.toArgb())
    UIManager.put("PopupMenu.background", surface)
    UIManager.put("PopupMenu.foreground", text)
    UIManager.put("MenuItem.background", surface)
    UIManager.put("MenuItem.foreground", text)
    UIManager.put("MenuItem.opaque", java.lang.Boolean.TRUE)
    UIManager.put("MenuItem.acceleratorForeground", muted)
    UIManager.put("MenuItem.acceleratorSelectionForeground", muted)
    UIManager.put("MenuItem.disabledForeground", muted)
    UIManager.put("MenuItem.selectionBackground", selected)
    UIManager.put("MenuItem.selectionForeground", selectedText)
    UIManager.put("Separator.foreground", divider)
    UIManager.put("Separator.background", surface)
}
