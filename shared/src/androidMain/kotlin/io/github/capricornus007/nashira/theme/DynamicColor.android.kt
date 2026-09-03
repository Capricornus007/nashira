package io.github.capricornus007.nashira.theme

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource

/** Android 12+：系統桌布種子色（system_accent1_500 資源查詢——比 dynamicColorScheme 輕兩個量級） */
@Composable
actual fun wallpaperSeedColor(enabled: Boolean): Color? {
    if (!enabled || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return null
    val context = LocalContext.current
    return androidx.compose.runtime.remember(context) {
        runCatching { context.resources.getColor(android.R.color.system_accent1_500, null) }
            .getOrNull()?.let { Color(it) }
    }
}
