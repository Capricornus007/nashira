package io.github.capricornus007.nashira.theme

import android.os.Build
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/** Android 12+：取系統動態配色的 primary 當種子（= 桌布 Material You 種子） */
@Composable
actual fun wallpaperSeedColor(enabled: Boolean): Color? {
    if (!enabled || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return null
    return dynamicLightColorScheme(LocalContext.current).primary
}
