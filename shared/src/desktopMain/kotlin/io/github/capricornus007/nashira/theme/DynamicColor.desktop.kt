package io.github.capricornus007.nashira.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/** 非 Android 平台（Linux 桌面）：無動態取色，固定回退品牌色 */
@Composable
actual fun dynamicColorSchemeIfAvailable(dark: Boolean, enabled: Boolean): ColorScheme? = null
