package io.github.capricornus007.nashira.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/** 平台動態取色：Android 回傳 Material You 桌布色；其他平台回 null（用品牌色） */
@Composable
expect fun dynamicColorSchemeIfAvailable(dark: Boolean, enabled: Boolean): ColorScheme?
