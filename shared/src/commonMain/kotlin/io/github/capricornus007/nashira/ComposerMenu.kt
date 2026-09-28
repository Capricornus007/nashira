package io.github.capricornus007.nashira

import androidx.compose.runtime.Composable
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

/**
 * 換掉輸入框右鍵選單的「画法」。
 *
 * 桌面端預設那層是 Swing 的 JPopupMenu：它不看 Material 主題，深色底下就是白底白字
 * （用戶 2026-09-28 點名）。Compose 1.12 起可以用 LocalTextContextMenuDropdownProvider
 * 換成自己畫的選單，這裡把整個 App 包一層，讓所有輸入框（含登入、搜尋）共用同一套配色。
 * Android 沒有這層（右鍵選單是系統原生），actual 原樣渲染。
 */
expect @Composable fun ProvideComposerContextMenu(content: @Composable () -> Unit)
