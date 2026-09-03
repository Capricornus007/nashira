package io.github.capricornus007.nashira.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Linux 桌面：無桌布取色，回 null（用品牌色或種子覆寫） */
@Composable
actual fun wallpaperSeedColor(enabled: Boolean): Color? = null
