package io.github.capricornus007.nashira

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import io.github.capricornus007.nashira.i18n.stringsFor
import io.github.capricornus007.nashira.matrix.MatrixSession
import io.github.capricornus007.nashira.theme.ThemeMode
import io.github.capricornus007.nashira.theme.dynamicColorSupported

/** 完整設定入口：帳戶、安全性、外觀、語言與關於。 */
@Composable
fun SettingsScreen(
    session: MatrixSession,
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {
    var accountOpen by remember { mutableStateOf(false) }
    PlatformBackHandler(enabled = accountOpen) { accountOpen = false }
    PlatformBackHandler(enabled = !accountOpen) { onBack() }
    if (accountOpen) {
        SecurityAndAccountScreen(session = session, onBack = { accountOpen = false }, onLogout = onLogout)
    } else {
        SettingsContent(onBack = onBack, onOpenAccount = { accountOpen = true }, hasAccount = true)
    }
}

/** 舊入口保留給未登入的預覽與既有呼叫端。 */
@Composable
fun HomeScreen(dark: Boolean) {
    SettingsContent(onBack = null, onOpenAccount = null, hasAccount = false)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    onBack: (() -> Unit)?,
    onOpenAccount: (() -> Unit)?,
    hasAccount: Boolean,
) {
    val ui = LocalUiState.current
    val strings = stringsFor(ui.language)
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { if (onBack != null) IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = strings.back) } },
                title = { Text(strings.settings) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (hasAccount && onOpenAccount != null) {
                Section(title = strings.account) {
                    SettingRow(
                        icon = Icons.Filled.Lock,
                        title = strings.accountAndSecurity,
                        subtitle = strings.accountAndSecurityHint,
                        onClick = onOpenAccount,
                    )
                }
            }
            Section(title = strings.appearance) {
                DropdownAnchor(
                    label = strings.themeMode,
                    current = when (ui.themeMode) {
                        ThemeMode.FOLLOW_SYSTEM -> strings.followSystem
                        ThemeMode.DARK -> strings.darkTheme
                        ThemeMode.LIGHT -> strings.lightTheme
                    },
                ) { close ->
                    DropdownMenuItem(text = { Text(strings.followSystem) }, onClick = { ui.themeMode = ThemeMode.FOLLOW_SYSTEM; close() })
                    DropdownMenuItem(text = { Text(strings.darkTheme) }, onClick = { ui.themeMode = ThemeMode.DARK; close() })
                    DropdownMenuItem(text = { Text(strings.lightTheme) }, onClick = { ui.themeMode = ThemeMode.LIGHT; close() })
                }

                if (dynamicColorSupported) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(strings.dynamicColor)
                            Text(strings.dynamicColorHint, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = ui.dynamicColor,
                            onCheckedChange = { ui.dynamicColor = it },
                            thumbContent = {
                                Icon(
                                    if (ui.dynamicColor) Icons.Filled.Check else Icons.Filled.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                )
                            },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                checkedIconColor = MaterialTheme.colorScheme.primaryContainer,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                uncheckedIconColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                uncheckedBorderColor = MaterialTheme.colorScheme.outline,
                            ),
                        )
                    }
                    AnimatedVisibility(visible = ui.dynamicColor, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            DropdownAnchor(label = strings.paletteStyle, current = ui.paletteStyle.displayLabel()) { close ->
                                PaletteStyle.entries.forEach { style ->
                                    DropdownMenuItem(text = { Text(style.displayLabel()) }, onClick = { ui.paletteStyle = style; close() })
                                }
                            }
                            DropdownAnchor(
                                label = strings.colorSpec,
                                current = if (ui.specVersion == ColorSpec.SpecVersion.SPEC_2021) strings.specM3 else strings.specExpressive,
                            ) { close ->
                                DropdownMenuItem(text = { Text(strings.specM3) }, onClick = { ui.specVersion = ColorSpec.SpecVersion.SPEC_2021; close() })
                                DropdownMenuItem(text = { Text(strings.specExpressive) }, onClick = { ui.specVersion = ColorSpec.SpecVersion.SPEC_2025; close() })
                            }
                            Text(strings.themeColor, style = MaterialTheme.typography.bodyMedium)
                            AccentPicker(selected = ui.accent, language = ui.language, onSelect = { ui.accent = it })
                        }
                    }
                }

                DropdownAnchor(
                    label = strings.spaceIconMode,
                    current = when (ui.spaceIconMode) {
                        SpaceIconMode.SPACE_AVATAR -> strings.spaceAvatar
                        SpaceIconMode.ROOM_PREVIEWS -> strings.spaceRoomAvatars
                    },
                ) { close ->
                    DropdownMenuItem(text = { Text(strings.spaceAvatar) }, onClick = { ui.spaceIconMode = SpaceIconMode.SPACE_AVATAR; close() })
                    DropdownMenuItem(text = { Text(strings.spaceRoomAvatars) }, onClick = { ui.spaceIconMode = SpaceIconMode.ROOM_PREVIEWS; close() })
                }
            }
            Section(title = strings.language) {
                DropdownAnchor(label = strings.language, current = ui.language.displayName) { close ->
                    io.github.capricornus007.nashira.i18n.AppLanguage.entries.forEach { candidate ->
                        DropdownMenuItem(text = { Text(candidate.displayName) }, onClick = { ui.language = candidate; close() })
                    }
                }
            }
            Section(title = strings.about) {
                LinkRow(strings.version, AppInfo.version, AppInfo.repoUrl)
                LinkRow(strings.engine, AppInfo.engine, AppInfo.engineUrl)
                LinkRow(strings.encryption, AppInfo.crypto, AppInfo.cryptoUrl)
                LinkRow(strings.license, AppInfo.license, AppInfo.licenseUrl)
                LinkRow(strings.sourceCode, AppInfo.repo, AppInfo.repoUrl)
                Spacer(Modifier.size(8.dp))
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(strings.appName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(strings.tagline, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun SettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun DropdownAnchor(label: String, current: String, menuContent: @Composable (close: () -> Unit) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Box {
            OutlinedButton(onClick = { open = true }) { Text(current) }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) { menuContent { open = false } }
        }
    }
}

internal fun PaletteStyle.displayLabel(): String = name.replace(Regex("(?<=[a-z])(?=[A-Z])"), " ")

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainer, shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            content()
        }
    }
}
