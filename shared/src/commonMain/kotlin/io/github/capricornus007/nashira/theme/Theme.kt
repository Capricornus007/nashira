package io.github.capricornus007.nashira.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

private val NashiraDarkColors = darkColorScheme(
    primary = NashiraGold,
    onPrimary = NashiraIndigo,
    primaryContainer = NashiraGoldContainer,
    onPrimaryContainer = NashiraGoldLight,
    secondary = NashiraIndigoLight,
    onSecondary = Color(0xFF1A2540),
    secondaryContainer = Color(0xFF363E6B),
    onSecondaryContainer = Color(0xFFDAE2FC),
    tertiary = Color(0xFFB4A7F5),
    onTertiary = Color(0xFF241E46),
    tertiaryContainer = Color(0xFF3F3769),
    onTertiaryContainer = Color(0xFFE5DEFF),
    background = NashiraDarkBackground,
    onBackground = NashiraStarWhite,
    surface = NashiraDarkBackground,
    onSurface = NashiraStarWhite,
    surfaceVariant = Color(0xFF2A2947),
    onSurfaceVariant = Color(0xFFC5CBE8),
    surfaceContainer = Color(0xFF262541),
    surfaceContainerHigh = Color(0xFF302F4D),
    surfaceContainerHighest = Color(0xFF3B3A58),
    surfaceContainerLow = Color(0xFF232238),
    surfaceContainerLowest = Color(0xFF191828),
    surfaceDim = Color(0xFF141322),
    surfaceBright = Color(0xFF3B3A58),
    outline = Color(0xFF8E90B9),
    outlineVariant = Color(0xFF44466F),
)

private val NashiraLightColors = lightColorScheme(
    primary = NashiraGoldDeep,
    onPrimary = NashiraLightBackground,
    primaryContainer = NashiraGold,
    onPrimaryContainer = NashiraIndigo,
    secondary = NashiraIndigoMid,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDAE2FC),
    onSecondaryContainer = Color(0xFF1A2540),
    tertiary = Color(0xFF5B54A2),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE5DEFF),
    onTertiaryContainer = Color(0xFF1D1549),
    background = NashiraLightBackground,
    onBackground = NashiraIndigo,
    surface = NashiraLightBackground,
    onSurface = NashiraIndigo,
    surfaceVariant = Color(0xFFC8CAB1),
    onSurfaceVariant = NashiraIndigoDeep,
    surfaceContainer = Color(0xFFD8DAC1),
    surfaceContainerHigh = Color(0xFFD2D4BB),
    surfaceContainerHighest = Color(0xFFCCCEB5),
    surfaceContainerLow = Color(0xFFDEE0C7),
    surfaceContainerLowest = Color(0xFFE8EAD1),
    surfaceDim = Color(0xFFBBBDA4),
    surfaceBright = Color(0xFFFDFEF3),
    outline = NashiraIndigoDeep,
    outlineVariant = Color(0xFFC8CAB1),
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NashiraTheme(dark: Boolean, content: @Composable () -> Unit) {
    MaterialExpressiveTheme(
        colorScheme = if (dark) NashiraDarkColors else NashiraLightColors,
        shapes = Shapes(),
        motionScheme = remember { MotionScheme.expressive() },
        typography = Typography(),
        content = content,
    )
}
