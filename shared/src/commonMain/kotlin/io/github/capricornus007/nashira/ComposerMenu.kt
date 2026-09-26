package io.github.capricornus007.nashira

import androidx.compose.ui.Modifier
import io.github.capricornus007.nashira.i18n.Strings

/**
 * 把格式化項掛進**輸入框自己那個**右鍵選單（同一份清單、同一套主題）。
 *
 * 桌面端靠 `appendTextContextMenuComponents`，那是 skiko 專屬 API，放 commonMain
 * 會讓 Android 目標直接編不過（2026-09-26 實測），所以這裡只宣告 expect。
 * Android 端沒有桌面這套文字選單，actual 原樣回傳。
 */
expect fun Modifier.appendComposerFormatMenu(
    strings: Strings,
    enabled: Boolean,
    onPick: (ComposerFormat) -> Unit,
): Modifier
