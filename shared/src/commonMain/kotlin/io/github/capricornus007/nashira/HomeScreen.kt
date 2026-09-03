package io.github.capricornus007.nashira

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import io.github.capricornus007.nashira.i18n.AppLanguage
import io.github.capricornus007.nashira.i18n.stringsFor
import io.github.capricornus007.nashira.theme.ThemeMode
import io.github.capricornus007.nashira.theme.dynamicColorSupported

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(dark: Boolean) {
    var language by remember { mutableStateOf(AppLanguage.ZH_TW) }
    val strings = stringsFor(language)
    val ui = LocalUiState.current

    Scaffold(
        topBar = { TopAppBar(title = { Text(strings.appName) }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                strings.tagline,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Section(title = strings.appearance) {
                // 主題模式：追隨系統 / 深 / 淺（三段）
                DropdownAnchor(
                    label = strings.themeMode,
                    current = when (ui.themeMode) {
                        ThemeMode.FOLLOW_SYSTEM -> strings.followSystem
                        ThemeMode.DARK -> strings.darkTheme
                        ThemeMode.LIGHT -> strings.lightTheme
                        else -> strings.followSystem
                    },
                ) { close ->
                    DropdownMenuItem(
                        text = { Text(strings.followSystem) },
                        onClick = { ui.themeMode = ThemeMode.FOLLOW_SYSTEM; close() },
                    )
                    DropdownMenuItem(
                        text = { Text(strings.darkTheme) },
                        onClick = { ui.themeMode = ThemeMode.DARK; close() },
                    )
                    DropdownMenuItem(
                        text = { Text(strings.lightTheme) },
                        onClick = { ui.themeMode = ThemeMode.LIGHT; close() },
                    )
                }

                // 動態顏色（Android 12+ Material You；桌面端不顯示）
                if (dynamicColorSupported) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(strings.dynamicColor)
                            Text(
                                strings.dynamicColorHint,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(checked = ui.dynamicColor, onCheckedChange = { ui.dynamicColor = it })
                    }

                    // Material You 配置抽屜：開關切換時收起/放出（expand/shrink 動畫）
                    AnimatedVisibility(
                        visible = ui.dynamicColor,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut(),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            DropdownAnchor(
                                label = strings.paletteStyle,
                                current = ui.paletteStyle.displayLabel(),
                            ) { close ->
                                PaletteStyle.entries.forEach { style ->
                                    DropdownMenuItem(
                                        text = { Text(style.displayLabel()) },
                                        onClick = { ui.paletteStyle = style; close() },
                                    )
                                }
                            }
                            DropdownAnchor(
                                label = strings.colorSpec,
                                current = if (ui.specVersion == ColorSpec.SpecVersion.SPEC_2021) {
                                    strings.specM3
                                } else {
                                    strings.specExpressive
                                },
                            ) { close ->
                                DropdownMenuItem(
                                    text = { Text(strings.specM3) },
                                    onClick = { ui.specVersion = ColorSpec.SpecVersion.SPEC_2021; close() },
                                )
                                DropdownMenuItem(
                                    text = { Text(strings.specExpressive) },
                                    onClick = { ui.specVersion = ColorSpec.SpecVersion.SPEC_2025; close() },
                                )
                            }
                            Text(strings.themeColor, style = MaterialTheme.typography.bodyMedium)
                            AccentPicker(
                                selected = ui.accent,
                                language = language,
                                onSelect = { ui.accent = it },
                            )
                        }
                    }
                }
            }
            Section(title = strings.language) {
                DropdownAnchor(
                    label = strings.language,
                    current = language.displayName,
                ) { close ->
                    AppLanguage.entries.forEach { candidate ->
                        DropdownMenuItem(
                            text = { Text(candidate.displayName) },
                            onClick = { language = candidate; close() },
                        )
                    }
                }
            }
            Section(title = strings.about) {
                LinkRow(strings.version, AppInfo.version, AppInfo.repoUrl)
                LinkRow(strings.engine, AppInfo.engine, AppInfo.engineUrl)
                LinkRow(strings.encryption, AppInfo.crypto, AppInfo.cryptoUrl)
                LinkRow(strings.license, AppInfo.license, AppInfo.licenseUrl)
                LinkRow(strings.sourceCode, AppInfo.repo, AppInfo.repoUrl)
            }
        }
    }
}

/**
 * 錨定式下拉：按鈕與選單同包一個 Box——Compose 的 DropdownMenu 錨定父容器，
 * 不包 Box 會錨到整個 Section 頂部飄走（真機實測 bug）。
 */
@Composable
private fun DropdownAnchor(
    label: String,
    current: String,
    menuContent: @Composable (close: () -> Unit) -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Box {
            OutlinedButton(onClick = { open = true }) {
                Text(current)
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                menuContent { open = false }
            }
        }
    }
}

/** PaletteStyle 顯示名（PascalCase 分詞）：TonalSpot → Tonal Spot */
internal fun PaletteStyle.displayLabel(): String =
    name.replace(Regex("(?<=[a-z])(?=[A-Z])"), " ")

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            content()
        }
    }
}
