package io.github.capricornus007.nashira

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.setValue
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme
import io.github.capricornus007.nashira.theme.NashiraBrandTheme
import io.github.capricornus007.nashira.theme.NashiraTheme
import io.github.capricornus007.nashira.theme.ThemeAccent
import io.github.capricornus007.nashira.theme.ThemeMode
import io.github.capricornus007.nashira.theme.wallpaperSeedColor

/** 全域 UI 狀態（設定暫存；P1 接 DataStore 持久化） */
class UiState {
    var themeMode by mutableStateOf(ThemeMode.FOLLOW_SYSTEM)
    var dynamicColor by mutableStateOf(false)

    /** 動態配色旋鈕（Material You）：調色盤樣式 / 顏色規格 / 種子色覆寫 */
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

    // 種子來源優先級：色系覆寫 > 桌布種子（動態取色開）> 品牌配色
    val seed = ui.accent?.color ?: wallpaperSeedColor(ui.dynamicColor)
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
