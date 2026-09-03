package io.github.capricornus007.nashira

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
                var modeMenuOpen by remember { mutableStateOf(false) }
                OutlinedButton(onClick = { modeMenuOpen = true }) {
                    Text(
                        when (ui.themeMode) {
                            ThemeMode.FOLLOW_SYSTEM -> strings.followSystem
                            ThemeMode.DARK -> strings.darkTheme
                            ThemeMode.LIGHT -> strings.lightTheme
                        }
                    )
                }
                DropdownMenu(expanded = modeMenuOpen, onDismissRequest = { modeMenuOpen = false }) {
                    DropdownMenuItem(
                        text = { Text(strings.followSystem) },
                        onClick = { ui.themeMode = ThemeMode.FOLLOW_SYSTEM; modeMenuOpen = false },
                    )
                    DropdownMenuItem(
                        text = { Text(strings.darkTheme) },
                        onClick = { ui.themeMode = ThemeMode.DARK; modeMenuOpen = false },
                    )
                    DropdownMenuItem(
                        text = { Text(strings.lightTheme) },
                        onClick = { ui.themeMode = ThemeMode.LIGHT; modeMenuOpen = false },
                    )
                }
                // 動態顏色（Android 12+ Material You 桌布取色；桌面端自動隱藏）
                if (dynamicColorSupported) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
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
                }
                // 調色盤樣式（Expressive）
                InfoRow(strings.paletteStyle, strings.expressive)
            }
            Section(title = strings.language) {
                var menuOpen by remember { mutableStateOf(false) }
                OutlinedButton(onClick = { menuOpen = true }) {
                    Text(language.displayName)
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    AppLanguage.entries.forEach { candidate ->
                        DropdownMenuItem(
                            text = { Text(candidate.displayName) },
                            onClick = { language = candidate; menuOpen = false },
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
