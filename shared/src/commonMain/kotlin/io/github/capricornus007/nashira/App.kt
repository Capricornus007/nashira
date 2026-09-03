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
import io.github.capricornus007.nashira.theme.NashiraTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(defaultDark: Boolean = true) {
    var dark by remember { mutableStateOf(defaultDark) }
    var language by remember { mutableStateOf(AppLanguage.ZH_TW) }
    val strings = stringsFor(language)

    NashiraTheme(dark = dark) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(strings.appName) })
            },
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
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = dark,
                            onClick = { dark = true },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        ) { Text(strings.darkTheme) }
                        SegmentedButton(
                            selected = !dark,
                            onClick = { dark = false },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        ) { Text(strings.lightTheme) }
                    }
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
                                onClick = {
                                    language = candidate
                                    menuOpen = false
                                },
                            )
                        }
                    }
                }
                Section(title = strings.about) {
                    AboutRow(strings.version, AppInfo.version)
                    AboutRow(strings.engine, AppInfo.engine)
                    AboutRow(strings.encryption, AppInfo.crypto)
                    AboutRow(strings.license, AppInfo.license)
                    AboutRow(strings.sourceCode, AppInfo.sourceUrl)
                }
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

@Composable
private fun AboutRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, color = MaterialTheme.colorScheme.onSurface)
    }
}
