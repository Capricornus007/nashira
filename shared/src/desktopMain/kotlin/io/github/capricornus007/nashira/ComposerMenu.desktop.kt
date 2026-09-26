package io.github.capricornus007.nashira

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.contextmenu.modifier.appendTextContextMenuComponents
import androidx.compose.ui.Modifier
import io.github.capricornus007.nashira.i18n.Strings

/**
 * 桌面實作：追加到內建選單尾端。自己掛 pointerInput 再開 DropdownMenu 會跟內建那層
 * 疊成兩層、而且內建那層是淺底色（2026-09-25 用戶截圖點名），所以只走這條路。
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
