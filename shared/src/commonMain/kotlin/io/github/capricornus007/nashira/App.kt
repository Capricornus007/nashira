package io.github.capricornus007.nashira

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import io.github.capricornus007.nashira.theme.NashiraBrandTheme
import io.github.capricornus007.nashira.theme.NashiraTheme
import io.github.capricornus007.nashira.theme.ThemeMode
import io.github.capricornus007.nashira.theme.dynamicColorSchemeIfAvailable

/** 全域 UI 狀態（設定暫存；接下來接持久化層時改為 ViewModel + DataStore） */
class UiState {
    var themeMode by mutableStateOf(ThemeMode.FOLLOW_SYSTEM)
    var dynamicColor by mutableStateOf(false)
}

val LocalUiState = androidx.compose.runtime.staticCompositionLocalOf { UiState() }

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
    val dynamic = dynamicColorSchemeIfAvailable(dark = dark, enabled = ui.dynamicColor)
    when {
        dynamic != null -> NashiraTheme(colorScheme = dynamic) { Content(dark) }
        else -> NashiraBrandTheme(dark = dark) { Content(dark) }
    }
}

@Composable
private fun Content(dark: Boolean) {
    HomeScreen(dark = dark)
}
