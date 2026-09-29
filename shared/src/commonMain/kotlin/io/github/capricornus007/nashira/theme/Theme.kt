package io.github.capricornus007.nashira.theme

import androidx.compose.material3.ColorScheme
import io.github.capricornus007.nashira.ProvideComposerContextMenu
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
    primary = NashiraStarBlue,
    onPrimary = NashiraIndigo,
    primaryContainer = NashiraStarBlueContainer,
    onPrimaryContainer = NashiraStarBlueLight,
    secondary = Color(0xFFC6C4E8),
    onSecondary = Color(0xFF2E2C4C),
    secondaryContainer = Color(0xFF45436E),
    onSecondaryContainer = Color(0xFFE2E0FF),
    tertiary = NashiraSkyBlue,
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
    // 強調色與深色／純黑同一族（星群藍），淺色用深一階的 #3E4AA8 才壓得住米綠底。
    // 容器 #A2AFF8：壓深字 6.9:1、對米綠底 1.58:1（原本給 #C7CEFF 只有 1.15，等於看不出來）。
    primary = NashiraStarBlueDeep,
    onPrimary = NashiraLightBackground,
    primaryContainer = NashiraStarBlueContainerLight,
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
    // 正文退一階（#23283F → #343A57）：米綠底上對比從 10.9:1 換到 8.4:1，
    // 還是 AAA，但不再是「潑墨」那種硬黑（用戶 2026-09-29 點名黑字在淺色下醜）
    onBackground = NashiraIndigoSoft,
    surface = NashiraLightBackground,
    onSurface = NashiraIndigoSoft,
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
 * 純黑（AMOLED）變體：把**主色**（品牌基底 #1F1E33 那層背景）壓成純黑，表面層級一律中性灰階（R=G=B）。
 *
 * 兩件事都是用戶 2026-09-29 點名後改的：
 * 1. 不沿用品牌深藍紫那套帶藍 cast 的容器色——#12111C 壓在 #000000 上，藍味會被放大成「一層髒髒的灰」。
 * 2. 台階壓得很淺（#0E/#15/#1D）：聊天室清單那整片用的是 surfaceContainerHigh，
 *    原本給到 #1C1C1C，實測被他點名「我說太亮的是聊天室列表」。
 *
 * 強調色（設定頁上的「主題顏色」）不動——省電省在大面積背景，不在那幾千個點的顏色。
 */
internal val NashiraPureBlackColors = NashiraDarkColors.copy(
    background = Color(0xFF000000),
    onBackground = Color(0xFFEAEAEA),
    surface = Color(0xFF000000),
    onSurface = Color(0xFFEAEAEA),
    surfaceContainer = Color(0xFF0E0E0E),
    surfaceContainerHigh = Color(0xFF151515),
    surfaceContainerHighest = Color(0xFF1D1D1D),
    surfaceContainerLow = Color(0xFF070707),
    surfaceContainerLowest = Color(0xFF000000),
    surfaceDim = Color(0xFF000000),
    surfaceBright = Color(0xFF2A2A2A),
    surfaceVariant = Color(0xFF202020),
    onSurfaceVariant = Color(0xFFB3B3B3),
    outline = Color(0xFF8A8A8A),
    outlineVariant = Color(0xFF3A3A3A),
)

/**
 * 字重約定（用戶 2026-09-29 兩輪點名「字體粗細你都調調吧」）。
 *
 * **只用三档，而且刻意不用 Medium 當主力**：這套 UI 的內容量以中文為主，
 * 而中文字體通常只有 400 與 700 兩個真字面，500/600 是渲染器「合成加粗」出來的，
 * 筆畫會糊成一團（#121 那張清單就是這樣）。層級改由**尺寸與顏色**承擔。
 *
 *   Bold 700   ＝ 頁面／房間標題、**未讀**的清單標題、Markdown 的粗體、未讀徽章數字
 *   Medium 500 ＝ 只用在短英文／名字：分組小標、訊息發送者名、頭像字母、關於頁數值
 *   Normal 400 ＝ 內文、說明文字、時間戳、讀過的清單標題、設定列標題
 *
 * 設定列標題不另加粗：它已經是 `titleMedium`（比說明的 `bodyMedium` 大一號）且用 onSurface，
 * 說明用 onSurfaceVariant —— 再看顏色與尺寸就分得出來，加粗只會糊。
 */
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
        // 外面再包一層：輸入框的右鍵選單在桌面端預設是 Swing 画的（不吃主題），
        // 換成 Compose 画的那層後才跟著配色走（見 ProvideComposerContextMenu）。
        ProvideComposerContextMenu {
            Surface(color = colorScheme.background, contentColor = colorScheme.onBackground, content = content)
        }
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
