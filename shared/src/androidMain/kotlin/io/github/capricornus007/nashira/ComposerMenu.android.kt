package io.github.capricornus007.nashira

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.capricornus007.nashira.i18n.Strings

/** Android 沒有桌面那套文字右鍵選單，原樣回傳。 */
actual fun Modifier.appendComposerFormatMenu(
    strings: Strings,
    enabled: Boolean,
    onPick: (ComposerFormat) -> Unit,
): Modifier = this

/** Android 的右鍵選單是系統原生那層，沒有可換的 representation，原樣渲染。 */
@Composable
actual fun ProvideComposerContextMenu(content: @Composable () -> Unit) = content()
