package io.github.capricornus007.nashira
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.ktx.animateColorScheme
import com.materialkolor.rememberDynamicColorScheme
import io.github.capricornus007.nashira.i18n.AppLanguage
import io.github.capricornus007.nashira.matrix.MatrixEngine
import io.github.capricornus007.nashira.theme.NashiraDarkColors
import io.github.capricornus007.nashira.theme.NashiraLightColors
import io.github.capricornus007.nashira.theme.NashiraTheme
import io.github.capricornus007.nashira.theme.ThemeAccent
import io.github.capricornus007.nashira.theme.ThemeMode
import io.github.capricornus007.nashira.theme.dynamicColorSupported
import io.github.capricornus007.nashira.theme.wallpaperSeedColor

enum class SpaceIconMode {
    SPACE_AVATAR,
    ROOM_PREVIEWS,
}



/**
 * 全域 UI 狀態。每次變更都寫回磁碟（十來個純量，同步寫沒有負擔），
 * 建構時先從磁碟載入，重啟後設定不再回到預設。
 */
class UiState(private val storage: SettingsStorage = SettingsStorage()) {
    private val stored: Map<String, String> = runCatching { storage.load() }.getOrDefault(emptyMap())
    private var loaded = false

    var language by mutableStateOf(stored.enumOr("language", AppLanguage.ZH_TW))
    var themeMode by mutableStateOf(stored.enumOr("themeMode", ThemeMode.FOLLOW_SYSTEM))
    var dynamicColor by mutableStateOf(stored["dynamicColor"]?.toBooleanStrictOrNull() ?: false)

    /** Material You 配置（僅 Android 顯示；動態顏色開啟時以抽屜動畫展開） */
    var paletteStyle by mutableStateOf(stored.enumOr("paletteStyle", PaletteStyle.Expressive))
    var specVersion by mutableStateOf(stored.enumOr("specVersion", ColorSpec.SpecVersion.SPEC_2025))
    var accent by mutableStateOf(stored["accent"]?.let { name -> ThemeAccent.entries.firstOrNull { it.name == name } })
    var spaceIconMode by mutableStateOf(stored.enumOr("spaceIconMode", SpaceIconMode.ROOM_PREVIEWS))

    /** 聊天室列表與 Space 圖示上的未讀提示（白條／紅圈數字） */
    var showUnreadIndicators by mutableStateOf(stored["showUnreadIndicators"]?.toBooleanStrictOrNull() ?: true)
    /** 聊天室列表第二行的最後一則訊息預覽 */
    var showMessagePreview by mutableStateOf(stored["showMessagePreview"]?.toBooleanStrictOrNull() ?: true)

    /** 貼圖面板貼在輸入列上方（Discord/Telegram 慣例）或下方（面板不推走輸入列） */
    var stickerPanelAbove by mutableStateOf(stored["stickerPanelAbove"]?.toBooleanStrictOrNull() ?: true)

    /**
     * 寬版面的成員欄。預設收起（Discord 與 Element 都不是一進房就展開），
     * 由聊天室標題列的人物圖示切換，並記住上次選擇。
     */
    var membersPanelOpen by mutableStateOf(stored["membersPanelOpen"]?.toBooleanStrictOrNull() ?: false)

    /**
     * Android 背景同步（前台服務）。關掉就只有 app 在前台時收得到訊息。
     * 桌面不看這個值。
     */
    var backgroundSync by mutableStateOf(stored["backgroundSync"]?.toBooleanStrictOrNull() ?: true)

    init {
        loaded = true
    }

    /** 由 App 在每次重組時呼叫；值有變才寫磁碟。 */
    internal fun persist() {
        if (!loaded) return
        val snapshot = mapOf(
            "language" to language.name,
            "themeMode" to themeMode.name,
            "dynamicColor" to dynamicColor.toString(),
            "paletteStyle" to paletteStyle.name,
            "specVersion" to specVersion.name,
            "spaceIconMode" to spaceIconMode.name,
            "showUnreadIndicators" to showUnreadIndicators.toString(),
            "showMessagePreview" to showMessagePreview.toString(),
            "stickerPanelAbove" to stickerPanelAbove.toString(),
            "membersPanelOpen" to membersPanelOpen.toString(),
            "backgroundSync" to backgroundSync.toString(),
        ) + (accent?.let { mapOf("accent" to it.name) } ?: emptyMap())
        if (snapshot == lastPersisted) return
        lastPersisted = snapshot
        runCatching { storage.save(snapshot) }
    }

    private var lastPersisted: Map<String, String>? = null
}

private inline fun <reified E : Enum<E>> Map<String, String>.enumOr(key: String, fallback: E): E =
    this[key]?.let { name -> enumValues<E>().firstOrNull { entry -> entry.name == name } } ?: fallback

val LocalUiState = staticCompositionLocalOf { UiState() }

@Composable
fun App(defaultDark: Boolean? = null) {
    val ui = LocalUiState.current
    val systemDark = defaultDark ?: isSystemInDarkTheme()
    val dark = when (ui.themeMode) {
        ThemeMode.FOLLOW_SYSTEM -> systemDark
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }
    // 任一設定變更就寫回磁碟（persist 內部比對快照，值沒變不落盤）
    ui.persist()

    // 種子來源（對齊 InstallerX Revived）：
    //   動態顏色 ON  → 一律桌布取色（手選色票在這個模式下不參與）
    //   動態顏色 OFF → 手選色票；沒選就是 null，落回品牌 Arcaea 色板（＝色票裡的「預設」）
    // 桌面沒有桌布取色（dynamicColorSupported=false），一律走色票這條。
    val dynamic = dynamicColorSupported && ui.dynamicColor
    val seed = if (dynamic) wallpaperSeedColor(enabled = true) else ui.accent?.color
    val generated = rememberDynamicColorScheme(
        seedColor = seed ?: androidx.compose.ui.graphics.Color(0xFF1F1E33),
        isDark = dark,
        style = ui.paletteStyle,
        specVersion = ui.specVersion,
    )
    // 目標配色：動態開啟且種子存在 → material-kolor 生成；否則手調品牌 Arcaea 色板
    val target = if (seed != null) generated
        else if (dark) NashiraDarkColors else NashiraLightColors
    // 配色補間（照 InstallerX：每槽 animateColorAsState(spring()) 物理彈簧曲線）
    val session by MatrixEngine.session.collectAsState()
    val restoring by MatrixEngine.restoring.collectAsState()
    androidx.compose.runtime.LaunchedEffect(Unit) { MatrixEngine.restoreFromDisk() }
    val animatedScheme = target.animateAsState()

    NashiraTheme(colorScheme = animatedScheme) {
        val current = session
        when {
            current != null -> {
                ChatScreen(
                    session = current,
                    onLogout = { MatrixEngine.logout() },
                )
                // 裝置驗證請求可能在任何畫面到來，所以對話框掛在最外層（蓋住設定頁）
                DeviceVerificationHost(current)
            }
            // 磁碟有憑證時先顯示啟動頁，不再閃一次登入表單
            restoring -> StartupScreen()
            else -> LoginScreen(onLoginSuccess = { })
        }
    }
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
