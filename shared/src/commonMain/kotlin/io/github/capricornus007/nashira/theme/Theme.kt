package io.github.capricornus007.nashira.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

/**
 * 品牌深色板。基底是協商定案的 Arcaea 曲目色 #1F1E33（深藍紫），
 * 表面層級全部從它推導——不是 Discord 的冷灰（那套曾在 2026-09-06 被
 * 誤當成「對齊 Discord」引入，破壞了品牌共識，已回歸）。
 */
internal val NashiraDarkColors = darkColorScheme(
    primary = NashiraGold,
    onPrimary = NashiraIndigo,
    primaryContainer = NashiraGoldContainer,
    onPrimaryContainer = NashiraGoldLight,
    secondary = Color(0xFFC6C4E8),
    onSecondary = Color(0xFF2E2C4C),
    secondaryContainer = Color(0xFF45436E),
    onSecondaryContainer = Color(0xFFE2E0FF),
    tertiary = Color(0xFF949CF7),
    onTertiary = Color(0xFF252A55),
    tertiaryContainer = Color(0xFF3A3F63),
    onTertiaryContainer = Color(0xFFE2E5FF),
    background = NashiraDarkBackground,
    onBackground = Color(0xFFE6E5F2),
    surface = NashiraDarkBackground,
    onSurface = Color(0xFFE6E5F2),
    surfaceVariant = Color(0xFF3E3C64),
    onSurfaceVariant = Color(0xFFB9B7D2),
    surfaceContainer = Color(0xFF2A2947),
    surfaceContainerHigh = Color(0xFF33315A),
    surfaceContainerHighest = Color(0xFF3B3963),
    surfaceContainerLow = Color(0xFF242340),
    surfaceContainerLowest = Color(0xFF18172B),
    surfaceDim = Color(0xFF141324),
    surfaceBright = Color(0xFF474575),
    outline = Color(0xFF8D8BAA),
    outlineVariant = Color(0xFF4E4C74),
    // 錯誤紅：M3 預設的 #F2B8B5 在深藍紫上太淡，刪除鈕幾乎看不出來。
    // 換高飽和的珊瑚紅；主題兩個借用色（#1F1E33/#E0E1CC）不動。
    error = Color(0xFFFF6E6E),
    onError = Color(0xFF3F0A0A),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFFFDAD6),
)

/** 品牌淺色板。基底是協商定案的 Arcaea 曲目色 #E0E1CC（米綠）。 */
internal val NashiraLightColors = lightColorScheme(
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
    surfaceContainer = Color(0xFFD9DABF),
    surfaceContainerHigh = Color(0xFFCFD0B3),
    surfaceContainerHighest = Color(0xFFC3C4A8),
    surfaceContainerLow = Color(0xFFE6E7D2),
    surfaceContainerLowest = Color(0xFFEDEEDE),
    surfaceDim = Color(0xFFBBBDA4),
    surfaceBright = Color(0xFFFDFEF3),
    outline = NashiraIndigoDeep,
    outlineVariant = Color(0xFFC8CAB1),
    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF93000A),
)

/**
 * 純黑（AMOLED）變體：深色板把背景/表面壓到真黑，容器層級換成黑階。
 * 以開關提供，不動品牌深色板的 #1F1E33 基底。
 */
internal val NashiraPureBlackColors = NashiraDarkColors.copy(
    background = Color(0xFF000000),
    surface = Color(0xFF000000),
    surfaceContainerLowest = Color(0xFF000000),
    surfaceContainerLow = Color(0xFF0B0A12),
    surfaceContainer = Color(0xFF12111C),
    surfaceContainerHigh = Color(0xFF191826),
    surfaceContainerHighest = Color(0xFF1F1E30),
    surfaceVariant = Color(0xFF262538),
    surfaceDim = Color(0xFF000000),
    surfaceBright = Color(0xFF2E2D42),
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NashiraTheme(
    colorScheme: ColorScheme,
    content: @Composable () -> Unit,
) {
    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        shapes = Shapes(),
        motionScheme = remember { MotionScheme.expressive() },
        typography = Typography(),
    ) {
        // MaterialTheme 不設 LocalContentColor（預設黑）。包一層 Surface 讓沒有顯式
        // 指定顏色的 Text/Icon 拿到 onBackground，深色主題下才不會變成黑字。
        Surface(color = colorScheme.background, contentColor = colorScheme.onBackground, content = content)
    }
}

/** 品牌配色（Arcaea）：非動態取色時的預設來源 */
@Composable
fun NashiraBrandTheme(dark: Boolean, content: @Composable () -> Unit) {
    NashiraTheme(
        colorScheme = if (dark) NashiraDarkColors else NashiraLightColors,
        content = content,
    )
}
