package io.github.capricornus007.nashira

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import io.github.capricornus007.nashira.i18n.Strings

/**
 * Android 端把手機的文字選取工具列（ActionMode）當成「右鍵選單」：用 `MenuProvider`
 * 把格式化項掛進 AndroidComposeView 的 menuHost，選取文字時那排按鈕就會多出
 * 粗體／斜體／…，放不下時系統自己收成 ⋮ 溢出——這正是 moregramX 的形態
 *（用戶 2026-09-29 四張對照圖：TG 是「剪下 複製 貼上 粗體 斜體 ⋮」，
 * 我們當時只有「剪下 複製 貼上」）。
 *
 * 舊版這裡是 `= this` 空實作，註解還寫「手機端也走同一份清單」——那句是錯的，
 * 手機端當時根本沒掛任何东西。
 */
actual fun Modifier.appendComposerFormatMenu(
    strings: Strings,
    enabled: Boolean,
    onPick: (ComposerFormat) -> Unit,
): Modifier = composed {
    val host = LocalView.current as? MenuHost
    DisposableEffect(host, enabled, strings, onPick) {
        if (host == null || !enabled) {
            onDispose { }
        } else {
            val provider = object : MenuProvider {
                override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
                    ComposerFormat.entries.forEachIndexed { index, format ->
                        menu.add(Menu.NONE, index, index, format.label(strings))
                    }
                }

                override fun onMenuItemSelected(item: MenuItem): Boolean {
                    val format = ComposerFormat.entries.getOrNull(item.itemId) ?: return false
                    onPick(format)
                    return true
                }
            }
            host.addMenuProvider(provider)
            onDispose { host.removeMenuProvider(provider) }
        }
    }
    this
}

/** Android 的右鍵選單是系統原生那層，沒有可換的 representation，原樣渲染。 */
@Composable
actual fun ProvideComposerContextMenu(content: @Composable () -> Unit) = content()

/**
 * 手機沒有「包住輸入框換掉右鍵選單」這層：選取工具列是系統畫的，格式化項
 * 已經由上面的 MenuProvider 掛進去了，這裡原樣渲染。
 * （桌面那份 TG 樣式的選單見 ComposerMenu.desktop.kt。）
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
actual fun ComposerMenuHost(
    state: androidx.compose.foundation.text.input.TextFieldState,
    strings: Strings,
    content: @Composable () -> Unit,
) = content()
