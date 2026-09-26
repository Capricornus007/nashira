package io.github.capricornus007.nashira

import androidx.compose.ui.Modifier
import io.github.capricornus007.nashira.i18n.Strings

/** Android 沒有桌面那套文字右鍵選單，原樣回傳。 */
actual fun Modifier.appendComposerFormatMenu(
    strings: Strings,
    enabled: Boolean,
    onPick: (ComposerFormat) -> Unit,
): Modifier = this
