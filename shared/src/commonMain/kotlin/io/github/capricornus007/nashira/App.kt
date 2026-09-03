package io.github.capricornus007.nashira

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.setValue
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme
import io.github.capricornus007.nashira.theme.NashiraBrandTheme
import io.github.capricornus007.nashira.theme.NashiraTheme
import io.github.capricornus007.nashira.theme.ThemeAccent
import io.github.capricornus007.nashira.theme.ThemeMode
import io.github.capricornus007.nashira.theme.wallpaperSeedColor

/** 全域 UI 狀態（記憶體態；P1 接 DataStore 持久化） */
class UiState {
    var themeMode by mutableStateOf(ThemeMode.FOLLOW_SYSTEM)
    var dynamicColor by mutableStateOf(false)

    /** Material You 配置（僅 Android 顯示；動態顏色開啟時以抽屜動畫展開） */
    var paletteStyle by mutableStateOf(PaletteStyle.Expressive)
    var specVersion by mutableStateOf(ColorSpec.SpecVersion.SPEC_2025)
    var accent by mutableStateOf<ThemeAccent?>(null)
}

val LocalUiState = staticCompositionLocalOf { UiState() }

@Composable
fun App(defaultDark: Boolean? = null) {
    val ui = LocalUiState.current
    val systemDark = defaultDark ?: isSystemInDarkTheme()
    val dark = when (ui.themeMode) {
        ThemeMode.FOLLOW_SYSTEM -> systemDark
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        else -> systemDark
    }

    // 種子來源（僅動態顏色開啟時）：色系覆寫 > 桌布種子；關閉＝品牌 Arcaea
    val seed = if (ui.dynamicColor) {
        ui.accent?.color ?: wallpaperSeedColor(enabled = true)
    } else {
        null
    }
    val generated = seed?.let {
        rememberDynamicColorScheme(
            seedColor = it,
            isDark = dark,
            style = ui.paletteStyle,
            specVersion = ui.specVersion,
        )
    }
    if (generated != null) {
        NashiraTheme(colorScheme = generated) { HomeScreen(dark = dark) }
    } else {
        NashiraBrandTheme(dark = dark) { HomeScreen(dark = dark) }
    }
}
