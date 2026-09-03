package io.github.capricornus007.nashira
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
    // （wallpaperSeedColor 內部已 remember 緩存；accent 是純值）
    val seed = if (ui.dynamicColor) {
        ui.accent?.color ?: wallpaperSeedColor(enabled = true)
    } else {
        null
    }
    val generated = rememberDynamicColorScheme(
        seedColor = seed ?: androidx.compose.ui.graphics.Color(0xFF1F1E33),
        isDark = dark,
        style = ui.paletteStyle,
        specVersion = ui.specVersion,
    )
    // 目標配色：動態生成或品牌色
    val target = generated ?: if (dark) NashiraDarkColors else NashiraLightColors
    // 配色補間（照 InstallerX：每槽 animateColorAsState(spring()) 物理彈簧曲線）
    NashiraTheme(colorScheme = target.animateAsState()) { HomeScreen(dark = dark) }
}

/** ColorScheme 逐槽 spring 補間（照 InstallerX ThemeExt.animateAsState，含 M3 fixed roles） */
@androidx.compose.runtime.Composable
private fun androidx.compose.material3.ColorScheme.animateAsState(): androidx.compose.material3.ColorScheme {
    val spec = androidx.compose.animation.core.tween<androidx.compose.ui.graphics.Color>(
        durationMillis = 350,
        easing = androidx.compose.animation.core.FastOutSlowInEasing,
    )

    @androidx.compose.runtime.Composable
    fun anim(color: androidx.compose.ui.graphics.Color) =
        androidx.compose.animation.animateColorAsState(
            targetValue = color,
            animationSpec = spec,
            label = "theme_color",
        ).value

    return androidx.compose.material3.ColorScheme(
        primary = anim(primary), onPrimary = anim(onPrimary),
        primaryContainer = anim(primaryContainer), onPrimaryContainer = anim(onPrimaryContainer),
        inversePrimary = anim(inversePrimary),
        secondary = anim(secondary), onSecondary = anim(onSecondary),
        secondaryContainer = anim(secondaryContainer), onSecondaryContainer = anim(onSecondaryContainer),
        tertiary = anim(tertiary), onTertiary = anim(onTertiary),
        tertiaryContainer = anim(tertiaryContainer), onTertiaryContainer = anim(onTertiaryContainer),
        background = anim(background), onBackground = anim(onBackground),
        surface = anim(surface), onSurface = anim(onSurface),
        surfaceVariant = anim(surfaceVariant), onSurfaceVariant = anim(onSurfaceVariant),
        surfaceTint = anim(surfaceTint),
        inverseSurface = anim(inverseSurface), inverseOnSurface = anim(inverseOnSurface),
        error = anim(error), onError = anim(onError),
        errorContainer = anim(errorContainer), onErrorContainer = anim(onErrorContainer),
        outline = anim(outline), outlineVariant = anim(outlineVariant),
        scrim = anim(scrim),
        surfaceBright = anim(surfaceBright), surfaceDim = anim(surfaceDim),
        surfaceContainer = anim(surfaceContainer),
        surfaceContainerHigh = anim(surfaceContainerHigh),
        surfaceContainerHighest = anim(surfaceContainerHighest),
        surfaceContainerLow = anim(surfaceContainerLow),
        surfaceContainerLowest = anim(surfaceContainerLowest),
        primaryFixed = anim(primaryFixed), primaryFixedDim = anim(primaryFixedDim),
        onPrimaryFixed = anim(onPrimaryFixed), onPrimaryFixedVariant = anim(onPrimaryFixedVariant),
        secondaryFixed = anim(secondaryFixed), secondaryFixedDim = anim(secondaryFixedDim),
        onSecondaryFixed = anim(onSecondaryFixed), onSecondaryFixedVariant = anim(onSecondaryFixedVariant),
        tertiaryFixed = anim(tertiaryFixed), tertiaryFixedDim = anim(tertiaryFixedDim),
        onTertiaryFixed = anim(onTertiaryFixed), onTertiaryFixedVariant = anim(onTertiaryFixedVariant),
    )
}
