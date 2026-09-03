package io.github.capricornus007.nashira
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.ktx.animateColorScheme
import com.materialkolor.rememberDynamicColorScheme
import io.github.capricornus007.nashira.theme.NashiraDarkColors
import io.github.capricornus007.nashira.theme.NashiraLightColors
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
    // 目標配色：動態生成或品牌色
    val target = generated ?: if (dark) NashiraDarkColors else NashiraLightColors
    // 配色補間（切動態顏色/樣式/規格/色系/深淺時整頁平滑過渡，不硬切）
    val spec = tween<androidx.compose.ui.graphics.Color>(durationMillis = 450)
    val transition = updateTransition(targetState = target, label = "colorScheme")
    @androidx.compose.runtime.Composable
    fun androidx.compose.animation.core.Transition<androidx.compose.material3.ColorScheme>.animated(): androidx.compose.material3.ColorScheme =
        androidx.compose.material3.ColorScheme(
            primary = animateColor(transitionSpec = { spec }) { it.primary }.value,
            onPrimary = animateColor(transitionSpec = { spec }) { it.onPrimary }.value,
            primaryContainer = animateColor(transitionSpec = { spec }) { it.primaryContainer }.value,
            onPrimaryContainer = animateColor(transitionSpec = { spec }) { it.onPrimaryContainer }.value,
            inversePrimary = animateColor(transitionSpec = { spec }) { it.inversePrimary }.value,
            secondary = animateColor(transitionSpec = { spec }) { it.secondary }.value,
            onSecondary = animateColor(transitionSpec = { spec }) { it.onSecondary }.value,
            secondaryContainer = animateColor(transitionSpec = { spec }) { it.secondaryContainer }.value,
            onSecondaryContainer = animateColor(transitionSpec = { spec }) { it.onSecondaryContainer }.value,
            tertiary = animateColor(transitionSpec = { spec }) { it.tertiary }.value,
            onTertiary = animateColor(transitionSpec = { spec }) { it.onTertiary }.value,
            tertiaryContainer = animateColor(transitionSpec = { spec }) { it.tertiaryContainer }.value,
            onTertiaryContainer = animateColor(transitionSpec = { spec }) { it.onTertiaryContainer }.value,
            background = animateColor(transitionSpec = { spec }) { it.background }.value,
            onBackground = animateColor(transitionSpec = { spec }) { it.onBackground }.value,
            surface = animateColor(transitionSpec = { spec }) { it.surface }.value,
            surfaceTint = animateColor(transitionSpec = { spec }) { it.surfaceTint }.value,
            onSurface = animateColor(transitionSpec = { spec }) { it.onSurface }.value,
            surfaceVariant = animateColor(transitionSpec = { spec }) { it.surfaceVariant }.value,
            onSurfaceVariant = animateColor(transitionSpec = { spec }) { it.onSurfaceVariant }.value,
            surfaceDim = animateColor(transitionSpec = { spec }) { it.surfaceDim }.value,
            surfaceBright = animateColor(transitionSpec = { spec }) { it.surfaceBright }.value,
            surfaceContainerLowest = animateColor(transitionSpec = { spec }) { it.surfaceContainerLowest }.value,
            surfaceContainerLow = animateColor(transitionSpec = { spec }) { it.surfaceContainerLow }.value,
            surfaceContainer = animateColor(transitionSpec = { spec }) { it.surfaceContainer }.value,
            surfaceContainerHigh = animateColor(transitionSpec = { spec }) { it.surfaceContainerHigh }.value,
            surfaceContainerHighest = animateColor(transitionSpec = { spec }) { it.surfaceContainerHighest }.value,
            inverseSurface = animateColor(transitionSpec = { spec }) { it.inverseSurface }.value,
            inverseOnSurface = animateColor(transitionSpec = { spec }) { it.inverseOnSurface }.value,
            error = animateColor(transitionSpec = { spec }) { it.error }.value,
            onError = animateColor(transitionSpec = { spec }) { it.onError }.value,
            errorContainer = animateColor(transitionSpec = { spec }) { it.errorContainer }.value,
            onErrorContainer = animateColor(transitionSpec = { spec }) { it.onErrorContainer }.value,
            outline = animateColor(transitionSpec = { spec }) { it.outline }.value,
            outlineVariant = animateColor(transitionSpec = { spec }) { it.outlineVariant }.value,
            scrim = animateColor(transitionSpec = { spec }) { it.scrim }.value,
        )
    NashiraTheme(colorScheme = transition.animated()) { HomeScreen(dark = dark) }
}
