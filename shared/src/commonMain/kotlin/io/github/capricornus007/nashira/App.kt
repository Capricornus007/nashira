package io.github.capricornus007.nashira
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
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
import io.github.capricornus007.nashira.theme.NashiraPureBlackColors
import io.github.capricornus007.nashira.theme.NashiraTheme
import io.github.capricornus007.nashira.theme.ThemeAccent
import io.github.capricornus007.nashira.theme.ThemeMode
import io.github.capricornus007.nashira.theme.dynamicColorSupported
import io.github.capricornus007.nashira.theme.wallpaperSeedColor
import io.github.capricornus007.nashira.i18n.stringsFor

/**
 * 哪一種 Enter 組合送出訊息；其餘的 Enter 一律換行。
 * 預設 Enter 直接送（Telegram／Discord／Element 桌面預設都是這樣）。
 */
enum class SendShortcut { ENTER, CTRL_ENTER, ALT_ENTER, SHIFT_ENTER }

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
    // 動態桌布取色目前會在部分 AMOLED 深色桌布上生成髒棕色；先預設關閉，
    // 使用者手動開啟才使用 Material You。
    var dynamicColor by mutableStateOf(false)

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

    /** 送出鍵；其餘 Enter 組合換行。 */
    var sendShortcut by mutableStateOf(stored.enumOr("sendShortcut", SendShortcut.ENTER))

    /** 純黑（AMOLED）深色變體：背景壓真黑省電；只在深色模式生效。 */
    var pureBlack by mutableStateOf(stored["pureBlack"]?.toBooleanStrictOrNull() ?: false)

    /**
     * 音訊裝置（僅桌面）：javax.sound Mixer 名，null＝系統預設。
     * 變更時同步到 [AudioSelection] 供錄音/播放 actual 讀取。
     */
    var audioInput: String?
        get() = audioInputState
        set(value) {
            audioInputState = value
            AudioSelection.input = value
        }
    var audioOutput: String?
        get() = audioOutputState
        set(value) {
            audioOutputState = value
            AudioSelection.output = value
        }

    private var audioInputState by mutableStateOf(stored["audioInput"]?.takeIf { it.isNotBlank() })
    private var audioOutputState by mutableStateOf(stored["audioOutput"]?.takeIf { it.isNotBlank() })

    /** 語音錄音增益（0-100）；錄音器對 PCM 線性縮放。 */
    var audioInputGain: Int
        get() = audioInputGainState
        set(value) {
            audioInputGainState = value
            AudioSelection.inputGain = value
        }

    /** 播放音量（0-100）；Clip.setVolume / ffplay -volume。 */
    var audioOutputVolume: Int
        get() = audioOutputVolumeState
        set(value) {
            audioOutputVolumeState = value
            AudioSelection.outputVolume = value
        }

    private var audioInputGainState by mutableStateOf(stored["audioInputGain"]?.toIntOrNull() ?: 100)
    private var audioOutputVolumeState by mutableStateOf(stored["audioOutputVolume"]?.toIntOrNull() ?: 100)

    /** 麥克風靜音（底欄麥克風鈕，Discord 語義）：錄音取消。 */
    var audioMicMuted: Boolean
        get() = audioMicMutedState
        set(value) { audioMicMutedState = value; AudioSelection.micMuted = value }

    /** 播放靜音（底欄耳機鈕，Discord 拒聽語義）。 */
    var audioPlaybackMuted: Boolean
        get() = audioPlaybackMutedState
        set(value) { audioPlaybackMutedState = value; AudioSelection.playbackMuted = value }

    private var audioMicMutedState by mutableStateOf(stored["audioMicMuted"]?.toBooleanStrictOrNull() ?: false)
    private var audioPlaybackMutedState by mutableStateOf(stored["audioPlaybackMuted"]?.toBooleanStrictOrNull() ?: false)

    /** 被隱藏的媒體（mxc 網址）。「隱藏圖片」後時間線改畫佔位，點佔位恢復。 */
    var hiddenMedia by mutableStateOf(
        stored["hiddenMedia"]?.split('\n')?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
    )

    /** 最近用過的 Unicode 表情（hexcode，最近在前）。 */
    var emojiRecents by mutableStateOf(
        stored["emojiRecents"]?.split('\n')?.filter { it.isNotBlank() } ?: emptyList()
    )

    /** 用過就頂到最前面；同一個不會出現兩次。上限 48——再長就失去「一眼找回常用」的意義。 */
    fun rememberEmojiUsage(hexcode: String) {
        emojiRecents = (listOf(hexcode) + emojiRecents.filter { it != hexcode }).take(48)
    }

    /**
     * 鍵盤高度（畫素，已扣掉導航列），量到一次就存檔。
     *
     * MoregramX 也是這個做法（它的 key 是 `keyboard_size` + 螢幕方向）：貼圖面板要和
     * 鍵盤等高，靠的是「這個數字」而不是當場量——冷啟動、換聊天室都不用重新量一次。
     * 0 = 還沒量過，呼叫端自己退回預估值。寫入端只送正值進來（量測那側已經先比過
     * baseline > top），所以這裡不用再加防呆 setter。
     */
    var imeHeightPx by mutableIntStateOf(stored["imeHeight"]?.toIntOrNull() ?: 0)

    /** 貼圖面板上次停在哪一頁（true = 表情）。同一個來源：MoregramX 存 `emoji_vp_position`。 */
    var stickerTabEmoji by mutableStateOf(stored["stickerTab"] == "emoji")

    init {
        AudioSelection.input = audioInput
        AudioSelection.output = audioOutput
        AudioSelection.inputGain = audioInputGain
        AudioSelection.outputVolume = audioOutputVolume
        AudioSelection.micMuted = audioMicMuted
        AudioSelection.playbackMuted = audioPlaybackMuted
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
            "membersPanelOpen" to membersPanelOpen.toString(),
            "backgroundSync" to backgroundSync.toString(),
            "sendShortcut" to sendShortcut.name,
            "hiddenMedia" to hiddenMedia.joinToString("\n"),
            "emojiRecents" to emojiRecents.joinToString("\n"),
            "imeHeight" to imeHeightPx.toString(),
            "stickerTab" to if (stickerTabEmoji) "emoji" else "sticker",
            "pureBlack" to pureBlack.toString(),
        ) + mapOf(
            "audioInput" to (audioInput ?: ""),
            "audioOutput" to (audioOutput ?: ""),
            "audioInputGain" to audioInputGain.toString(),
            "audioOutputVolume" to audioOutputVolume.toString(),
            "audioMicMuted" to audioMicMuted.toString(),
            "audioPlaybackMuted" to audioPlaybackMuted.toString(),
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
        else if (dark) {
            if (ui.pureBlack) NashiraPureBlackColors else NashiraDarkColors
        } else NashiraLightColors
    // 配色補間（照 InstallerX：每槽 animateColorAsState(spring()) 物理彈簧曲線）
    val session by MatrixEngine.session.collectAsState()
    val restoring by MatrixEngine.restoring.collectAsState()
    val restoreFailed by MatrixEngine.restoreFailed.collectAsState()
    val loggingIn by MatrixEngine.loggingIn.collectAsState()
    val strings = stringsFor(LocalUiState.current.language)
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
            // 磁碟有憑證時先顯示啟動頁，不再閃一次登入表單；
            // SSO/密碼登入交換期間以「不透明疊加層」蓋住表單——絕不能用
            // when 分支替換 LoginScreen：那會卸載它、取消其
            // rememberCoroutineScope，正在進行的 token 交換被
            // CancellationException 腰斬（token 已消耗但登入無結果、
            // 錯誤也顯示不出來；2026-09-14 桌面 SSO 實測復現，
            // 0ecdac1 引入、之後無任何全新登入被驗證過）。
            restoring -> StartupScreen()
            restoreFailed -> ConnectionRetryScreen()
            else -> {
                Box {
                    LoginScreen(onLoginSuccess = { })
                    if (loggingIn) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface),
                        ) {
                            StartupScreen(message = strings.loggingIn)
                        }
                    }
                }
            }
        }
    }
}

/**
 * 憑證還在但連不上伺服器（網路斷／代理切換／TLS 被攔）：
 * 給「重試」而不是登入表單——後者會讓人誤以為被登出。
 */
@Composable
private fun ConnectionRetryScreen() {
    val strings = stringsFor(LocalUiState.current.language)
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    androidx.compose.foundation.layout.Box(
        Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        androidx.compose.foundation.layout.Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
            Text(
                strings.connectionFailed,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                strings.connectionFailedHint,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp, bottom = 18.dp),
            )
            androidx.compose.material3.Button(onClick = { scope.launch { MatrixEngine.restoreFromDisk() } }) {
                Text(strings.retry)
            }
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
