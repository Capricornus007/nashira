package io.github.capricornus007.nashira

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.connect2x.trixnity.client.verification.ActiveVerificationState
import de.connect2x.trixnity.client.verification.ActiveSasVerificationMethod
import de.connect2x.trixnity.client.verification.ActiveSasVerificationState
import io.github.capricornus007.nashira.i18n.stringsFor
import io.github.capricornus007.nashira.matrix.MatrixSession
import io.github.capricornus007.nashira.matrix.VerificationRepository
import kotlinx.coroutines.launch

/** Discord 使用者設定中的安全性頁：顯示本機裝置與真正的 Matrix 驗證工作階段。 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityAndAccountScreen(
    session: MatrixSession,
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {
    val strings = stringsFor(LocalUiState.current.language)
    val repository = remember(session) { VerificationRepository(session) }
    val scope = rememberCoroutineScope()
    val activeDevice = repository.activeDeviceVerification.collectAsState().value
    val activeUsers = repository.activeUserVerifications.collectAsState().value
    val activeState = activeDevice?.state?.collectAsState()?.value
    var deviceId by remember(session) { mutableStateOf("") }
    var requestBusy by remember { mutableStateOf(false) }
    var requestMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = strings.back) } },
                title = { Text(strings.account) },
            )
        },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(strings.account, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            AccountValue(strings.accountId, session.client.userId.full)
            AccountValue(strings.deviceId, session.client.deviceId)
            SecurityCard(
                title = strings.security,
                icon = Icons.Filled.Lock,
                body = strings.securityHint,
            )
            Text(strings.deviceVerification, style = MaterialTheme.typography.titleLarge)
            Text(strings.verificationHint, color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedTextField(
                value = deviceId,
                onValueChange = { deviceId = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(strings.targetDeviceId) },
                singleLine = true,
                placeholder = { Text(strings.targetDeviceIdExample) },
            )
            Button(
                enabled = !requestBusy && deviceId.isNotBlank(),
                onClick = {
                    requestBusy = true
                    requestMessage = null
                    scope.launch {
                        repository.requestDeviceVerification(deviceId.trim())
                            .onSuccess { requestMessage = strings.verificationCreated }
                            .onFailure { requestMessage = it.message ?: strings.verificationFailed }
                        requestBusy = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (requestBusy) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                else Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(strings.verifyThisDevice, modifier = Modifier.padding(start = 8.dp))
            }
            requestMessage?.let { Text(it, color = if (it == strings.verificationCreated) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error) }
            VerificationStatusCard(strings, activeState, activeUsers.size, scope)
            Spacer(Modifier.size(8.dp))
            OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text(strings.logoutDevice) }
        }
    }
}

@Composable
private fun AccountValue(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun SecurityCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, body: String) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainer, shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun VerificationStatusCard(
    strings: io.github.capricornus007.nashira.i18n.Strings,
    state: ActiveVerificationState?,
    userCount: Int,
    scope: kotlinx.coroutines.CoroutineScope,
) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainer, shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(if (state is ActiveVerificationState.Done) Icons.Filled.CheckCircle else Icons.Filled.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(strings.verificationState, style = MaterialTheme.typography.titleMedium)
            }
            Text(
                when {
                    state is ActiveVerificationState.Done -> strings.verificationDone
                    state == null && userCount == 0 -> strings.noVerification
                    else -> strings.verificationInProgress.format(state?.javaClass?.simpleName ?: strings.waitingAnotherDevice)
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            when (state) {
                is ActiveVerificationState.TheirRequest -> {
                    Button(onClick = { scope.launch { state.ready() } }, modifier = Modifier.fillMaxWidth()) {
                        Text(strings.acceptVerification)
                    }
                }
                is ActiveVerificationState.Ready -> {
                    Button(
                        onClick = { state.methods.firstOrNull()?.let { method -> scope.launch { state.start(method) } } },
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text(strings.startSasVerification) }
                }
                is ActiveVerificationState.Start -> {
                    val sas = state.method as? ActiveSasVerificationMethod
                    val sasState = sas?.state?.collectAsState()?.value
                    when (sasState) {
                        is ActiveSasVerificationState.TheirSasStart -> {
                            Button(onClick = { scope.launch { sasState.accept() } }, modifier = Modifier.fillMaxWidth()) {
                                Text(strings.acceptSas)
                            }
                        }
                        is ActiveSasVerificationState.ComparisonByUser -> {
                            if (sasState.emojis.isNotEmpty()) {
                                Text(sasState.emojis.joinToString("  ") { it.second }, style = MaterialTheme.typography.headlineSmall)
                            } else {
                                Text(sasState.decimal.joinToString(" "), style = MaterialTheme.typography.headlineSmall)
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { scope.launch { sasState.match() } }, modifier = Modifier.weight(1f)) { Text(strings.match) }
                                OutlinedButton(onClick = { scope.launch { sasState.noMatch() } }, modifier = Modifier.weight(1f)) { Text(strings.noMatch) }
                            }
                        }
                        else -> Unit
                    }
                }
                else -> Unit
            }
        }
    }
}
