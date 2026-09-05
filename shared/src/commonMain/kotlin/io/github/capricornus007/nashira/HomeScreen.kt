package io.github.capricornus007.nashira

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import androidx.compose.ui.unit.IntOffset
import io.github.capricornus007.nashira.i18n.AppLanguage
import io.github.capricornus007.nashira.i18n.stringsFor
import io.github.capricornus007.nashira.matrix.MatrixSession
import io.github.capricornus007.nashira.settings.SettingsDropdownItem
import io.github.capricornus007.nashira.settings.SettingsGroup
import io.github.capricornus007.nashira.settings.SettingsItem
import io.github.capricornus007.nashira.settings.SettingsMenuOption
import io.github.capricornus007.nashira.settings.SettingsNavigationItem
import io.github.capricornus007.nashira.settings.SettingsSwitchItem
import io.github.capricornus007.nashira.theme.ThemeMode
import io.github.capricornus007.nashira.theme.dynamicColorSupported

/** 設定的子頁。用單一 enum 表示，返回鍵逐層退回。 */
private enum class SettingsPage { ROOT, ACCOUNT, APPEARANCE, CHAT_LIST, ABOUT }

/** 完整設定入口：帳戶、外觀、聊天室清單、語言與關於。 */
@Composable
fun SettingsScreen(
    session: MatrixSession,
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {
    var page by remember { mutableStateOf(SettingsPage.ROOT) }
    PlatformBackHandler(enabled = page != SettingsPage.ROOT) { page = SettingsPage.ROOT }
    PlatformBackHandler(enabled = page == SettingsPage.ROOT) { onBack() }
    SettingsNavHost(
        page = page,
        onNavigate = { page = it },
        onBack = onBack,
        session = session,
        onLogout = onLogout,
    )
}

/** 未登入時的預覽入口（沒有帳戶區）。 */
@Composable
fun HomeScreen(dark: Boolean) {
    var page by remember { mutableStateOf(SettingsPage.ROOT) }
    PlatformBackHandler(enabled = page != SettingsPage.ROOT) { page = SettingsPage.ROOT }
    SettingsNavHost(page = page, onNavigate = { page = it }, onBack = null, session = null, onLogout = {})
}

/**
 * 設定的子頁導航。子頁從右側滑入、返回時滑出（對齊 InstallerX 與 Discord 的設定子頁）。
 */
@Composable
private fun SettingsNavHost(
    page: SettingsPage,
    onNavigate: (SettingsPage) -> Unit,
    onBack: (() -> Unit)?,
    session: MatrixSession?,
    onLogout: () -> Unit,
) {
    AnimatedContent(
        targetState = page,
        transitionSpec = {
            // 進子頁：新頁從右滑入、舊頁略往左退；返回相反
            val forward = targetState != SettingsPage.ROOT
            if (forward) {
                slideInHorizontally(PageSlide) { it } togetherWith slideOutHorizontally(PageSlide) { -it / 6 }
            } else {
                slideInHorizontally(PageSlide) { -it / 6 } togetherWith slideOutHorizontally(PageSlide) { it }
            }
        },
        label = "settings_pages",
    ) { current ->
        when (current) {
            SettingsPage.ROOT -> SettingsRoot(
                onBack = onBack,
                hasAccount = session != null,
                onNavigate = onNavigate,
            )
            SettingsPage.ACCOUNT -> if (session != null) {
                SecurityAndAccountScreen(
                    session = session,
                    onBack = { onNavigate(SettingsPage.ROOT) },
                    onLogout = onLogout,
                )
            } else {
                SettingsRoot(onBack = onBack, hasAccount = false, onNavigate = onNavigate)
            }
            SettingsPage.APPEARANCE -> AppearancePage { onNavigate(SettingsPage.ROOT) }
            SettingsPage.CHAT_LIST -> ChatListPage { onNavigate(SettingsPage.ROOT) }
            SettingsPage.ABOUT -> AboutPage { onNavigate(SettingsPage.ROOT) }
        }
    }
}

private val PageSlide = tween<IntOffset>(260, easing = FastOutSlowInEasing)

/** 設定頁的共用骨架：可摺疊大標題 + 群組清單。 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScaffold(
    title: String,
    onBack: (() -> Unit)?,
    content: @Composable () -> Unit,
) {
    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(appBarState)
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    }
                },
                title = { Text(title, fontWeight = FontWeight.Bold) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                // 手機全寬；寬窗口（桌面）限寬置中，不再整排貼左
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(Modifier.navigationBarsPadding().widthIn(max = 640.dp)) { content() }
                }
            }
        }
    }
}

@Composable
private fun SettingsRoot(
    onBack: (() -> Unit)?,
    hasAccount: Boolean,
    onNavigate: (SettingsPage) -> Unit,
) {
    val ui = LocalUiState.current
    val strings = stringsFor(ui.language)
    SettingsScaffold(title = strings.settings, onBack = onBack) {
        if (hasAccount) {
            SettingsGroup(title = strings.account) {
                item { shape ->
                    SettingsNavigationItem(
                        shape = shape,
                        icon = Icons.Filled.Lock,
                        title = strings.accountAndSecurity,
                        description = strings.accountAndSecurityHint,
                        onClick = { onNavigate(SettingsPage.ACCOUNT) },
                    )
                }
            }
        }
        SettingsGroup(title = strings.personalization) {
            item { shape ->
                SettingsNavigationItem(
                    shape = shape,
                    icon = Icons.Filled.Star,
                    title = strings.appearance,
                    description = strings.appearanceHint,
                    onClick = { onNavigate(SettingsPage.APPEARANCE) },
                )
            }
            item { shape ->
                SettingsNavigationItem(
                    shape = shape,
                    icon = Icons.AutoMirrored.Filled.List,
                    title = strings.chatList,
                    description = strings.chatListHint,
                    onClick = { onNavigate(SettingsPage.CHAT_LIST) },
                )
            }
            item { shape ->
                SettingsDropdownItem(
                    shape = shape,
                    icon = Icons.Filled.Create,
                    title = strings.language,
                    current = ui.language.displayName,
                ) { close ->
                    AppLanguage.entries.forEach { candidate ->
                        SettingsMenuOption(candidate.displayName, ui.language == candidate) {
                            ui.language = candidate
                            close()
                        }
                    }
                }
            }
        }
        SettingsGroup(title = strings.about) {
            item { shape ->
                SettingsNavigationItem(
                    shape = shape,
                    icon = Icons.Filled.Info,
                    title = strings.about,
                    description = "${strings.appName} ${AppInfo.version}",
                    onClick = { onNavigate(SettingsPage.ABOUT) },
                )
            }
        }
    }
}

@Composable
private fun AppearancePage(onBack: () -> Unit) {
    val ui = LocalUiState.current
    val strings = stringsFor(ui.language)
    SettingsScaffold(title = strings.appearance, onBack = onBack) {
        SettingsGroup(title = strings.themeMode) {
            item { shape ->
                SettingsDropdownItem(
                    shape = shape,
                    icon = Icons.Filled.Settings,
                    title = strings.themeMode,
                    current = when (ui.themeMode) {
                        ThemeMode.FOLLOW_SYSTEM -> strings.followSystem
                        ThemeMode.DARK -> strings.darkTheme
                        ThemeMode.LIGHT -> strings.lightTheme
                    },
                ) { close ->
                    SettingsMenuOption(strings.followSystem, ui.themeMode == ThemeMode.FOLLOW_SYSTEM) {
                        ui.themeMode = ThemeMode.FOLLOW_SYSTEM; close()
                    }
                    SettingsMenuOption(strings.darkTheme, ui.themeMode == ThemeMode.DARK) {
                        ui.themeMode = ThemeMode.DARK; close()
                    }
                    SettingsMenuOption(strings.lightTheme, ui.themeMode == ThemeMode.LIGHT) {
                        ui.themeMode = ThemeMode.LIGHT; close()
                    }
                }
            }
        }
        if (dynamicColorSupported) {
            SettingsGroup(title = strings.themeColor) {
                item { shape ->
                    SettingsSwitchItem(
                        shape = shape,
                        icon = Icons.Filled.Star,
                        title = strings.dynamicColor,
                        description = strings.dynamicColorHint,
                        checked = ui.dynamicColor,
                        onCheckedChange = { ui.dynamicColor = it },
                    )
                }
                item { shape ->
                    SettingsDropdownItem(
                        shape = shape,
                        title = strings.paletteStyle,
                        current = ui.paletteStyle.displayLabel(),
                    ) { close ->
                        PaletteStyle.entries.forEach { style ->
                            SettingsMenuOption(style.displayLabel(), ui.paletteStyle == style) {
                                ui.paletteStyle = style; close()
                            }
                        }
                    }
                }
                item { shape ->
                    SettingsDropdownItem(
                        shape = shape,
                        title = strings.colorSpec,
                        current = if (ui.specVersion == ColorSpec.SpecVersion.SPEC_2021) strings.specM3 else strings.specExpressive,
                    ) { close ->
                        SettingsMenuOption(strings.specM3, ui.specVersion == ColorSpec.SpecVersion.SPEC_2021) {
                            ui.specVersion = ColorSpec.SpecVersion.SPEC_2021; close()
                        }
                        SettingsMenuOption(strings.specExpressive, ui.specVersion == ColorSpec.SpecVersion.SPEC_2025) {
                            ui.specVersion = ColorSpec.SpecVersion.SPEC_2025; close()
                        }
                    }
                }
            }
            // 色票是自由排布的方格，塞進列元件會擠壓；獨立成一塊，跟著動態顏色開關收放
            AnimatedVisibility(
                visible = ui.dynamicColor,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(strings.themeColor, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    AccentPicker(selected = ui.accent, language = ui.language, onSelect = { ui.accent = it })
                }
            }
        }
    }
}

@Composable
private fun ChatListPage(onBack: () -> Unit) {
    val ui = LocalUiState.current
    val strings = stringsFor(ui.language)
    SettingsScaffold(title = strings.chatList, onBack = onBack) {
        SettingsGroup {
            item { shape ->
                SettingsSwitchItem(
                    shape = shape,
                    icon = Icons.Filled.Create,
                    title = strings.spaceIconMode,
                    description = if (ui.spaceIconMode == SpaceIconMode.SPACE_AVATAR) strings.spaceAvatar else strings.spaceRoomAvatars,
                    checked = ui.spaceIconMode == SpaceIconMode.SPACE_AVATAR,
                    onCheckedChange = { enabled ->
                        ui.spaceIconMode = if (enabled) SpaceIconMode.SPACE_AVATAR else SpaceIconMode.ROOM_PREVIEWS
                    },
                )
            }
            item { shape ->
                SettingsSwitchItem(
                    shape = shape,
                    title = strings.unreadIndicators,
                    description = strings.unreadIndicatorsHint,
                    checked = ui.showUnreadIndicators,
                    onCheckedChange = { ui.showUnreadIndicators = it },
                )
            }
            item { shape ->
                SettingsSwitchItem(
                    shape = shape,
                    title = strings.messagePreview,
                    description = strings.messagePreviewHint,
                    checked = ui.showMessagePreview,
                    onCheckedChange = { ui.showMessagePreview = it },
                )
            }
            item { shape ->
                SettingsSwitchItem(
                    shape = shape,
                    title = strings.stickerPanelPosition,
                    description = if (ui.stickerPanelAbove) strings.stickerPanelAbove else strings.stickerPanelBelow,
                    checked = ui.stickerPanelAbove,
                    onCheckedChange = { ui.stickerPanelAbove = it },
                )
            }
        }
    }
}

@Composable
private fun AboutPage(onBack: () -> Unit) {
    val strings = stringsFor(LocalUiState.current.language)
    SettingsScaffold(title = strings.about, onBack = onBack) {
        SettingsGroup {
            item { shape ->
                SettingsItem(
                    shape = shape,
                    icon = Icons.Filled.Info,
                    title = strings.version,
                    description = AppInfo.version,
                    onClick = { openLink(AppInfo.repoUrl) },
                )
            }
            item { shape ->
                SettingsItem(
                    shape = shape,
                    icon = Icons.Filled.Build,
                    title = strings.engine,
                    description = AppInfo.engine,
                    onClick = { openLink(AppInfo.engineUrl) },
                )
            }
            item { shape ->
                SettingsItem(
                    shape = shape,
                    icon = Icons.Filled.Lock,
                    title = strings.encryption,
                    description = AppInfo.crypto,
                    onClick = { openLink(AppInfo.cryptoUrl) },
                )
            }
            item { shape ->
                SettingsItem(
                    shape = shape,
                    icon = Icons.Filled.Info,
                    title = strings.license,
                    description = AppInfo.license,
                    onClick = { openLink(AppInfo.licenseUrl) },
                )
            }
            item { shape ->
                SettingsItem(
                    shape = shape,
                    icon = Icons.Filled.Person,
                    title = strings.sourceCode,
                    description = AppInfo.repo,
                    onClick = { openLink(AppInfo.repoUrl) },
                )
            }
        }
        Column(
            Modifier.fillMaxWidth().padding(top = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(strings.appName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text(strings.tagline, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

internal fun PaletteStyle.displayLabel(): String = name.replace(Regex("(?<=[a-z])(?=[A-Z])"), " ")
