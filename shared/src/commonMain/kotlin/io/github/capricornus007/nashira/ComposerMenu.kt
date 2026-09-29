package io.github.capricornus007.nashira

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.capricornus007.nashira.i18n.Strings

/**
 * 把格式化項掛進輸入框的右鍵選單。
 *
 * 兩端走的是完全不同的路，所以只能 expect/actual：
 * - Android：用 `MenuProvider` 掛進系統的文字選取工具列（ActionMode），
 *   選取文字時那排按鈕就會多出粗體／斜體／⋯（手機沒有桌面這種右鍵選單）。
 * - 桌面：**清空**框架內建那層選單，改由 [ComposerMenuHost] 自己畫整份
 *   （要放快捷鍵欄與子選單，框架的 `ContextMenuItem` 只有 label/enabled/onClick，裝不下）。
 *
 * 教訓（2026-09-25 用戶截圖點名）：自己再掛一層 pointerInput 開 DropdownMenu，
 * 內建那層照樣彈出來，同一個位置疊兩層選單。
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
 * （用戶 2026-09-28 點名）。這一層管的是**其他**文字框（登入、搜尋、對話框）；
 * 草稿框走 [ComposerMenuHost]。Android 沒有這層（右鍵選單是系統原生），actual 原樣渲染。
 */
expect @Composable fun ProvideComposerContextMenu(content: @Composable () -> Unit)

/**
 * 包住一個輸入框，讓它的右鍵選單改用 Nashira 自己畫的那份（Telegram 樣式：
 * 左標籤、右快捷鍵、分隔線，加上「文字格式」子選單）。
 *
 * 只接管被包住的這個欄位：選單動作要作用在它自己的 [TextFieldState] 上，
 * 而且「有沒有選取」「剪貼簿有沒有東西」這種判斷也只有拿得到 state 的一側算得出來。
 * Android 的選取工具列是系統畫的（走 [appendComposerFormatMenu]），actual 原樣渲染。
 */
@OptIn(ExperimentalFoundationApi::class)
expect @Composable fun ComposerMenuHost(
    state: TextFieldState,
    strings: Strings,
    content: @Composable () -> Unit,
)
