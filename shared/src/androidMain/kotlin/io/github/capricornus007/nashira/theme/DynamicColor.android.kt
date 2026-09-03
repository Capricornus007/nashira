package io.github.capricornus007.nashira.theme

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/** Android 動態取色（Material You 桌布取色）；API < 31 退回品牌色 */
@Composable
actual fun dynamicColorSchemeIfAvailable(dark: Boolean, enabled: Boolean): ColorScheme? {
    if (!enabled || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return null
    val context = LocalContext.current
    return if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
}
