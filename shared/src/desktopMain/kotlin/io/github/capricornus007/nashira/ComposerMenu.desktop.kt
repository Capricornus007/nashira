package io.github.capricornus007.nashira

import androidx.compose.foundation.DarkDefaultContextMenuRepresentation
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LightDefaultContextMenuRepresentation
import androidx.compose.foundation.LocalContextMenuRepresentation
import androidx.compose.foundation.text.contextmenu.modifier.appendTextContextMenuComponents
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import io.github.capricornus007.nashira.i18n.Strings

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
 * 把文字框右鍵選單從 Swing 換成框架自己那套 Compose 繪製的選單。
 *
 * 1.12.0 的真實鏈路（反編譯 foundation-desktop 核過，不是猜的）：
 * `TextContextMenu.Default` 是 `BasicTextContextMenu`，但它渲染時讀
 * `LocalContextMenuRepresentation`，而桌面端的預設值是
 * `JPopupContextMenuRepresentation` —— 那是一層 Swing `JPopupMenu`。
 * 上一版改 `UIManager` 的 `PopupMenu.*` / `MenuItem.*` 預設值**實測無效**
 * （用戶 2026-09-29 截圖仍是白底），因為那層選單的配色不是由這些 key 決定。
 * 換成 `Light/DarkDefaultContextMenuRepresentation` 之後選單回到 Compose 渲染管線，
 * 深淺色跟主題走、中文字體也跟主窗一致（托盤選單當初就是同一個理由換掉的）。
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

/** 以 surface 的亮度判深淺：這比再從上面傳一個 dark 旗標可靠，主題換了自動跟著換。 */
private fun ColorScheme.isDarkSurface(): Boolean = surface.luminance() < 0.5f
