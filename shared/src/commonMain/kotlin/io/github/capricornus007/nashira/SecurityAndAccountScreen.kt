package io.github.capricornus007.nashira

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import de.connect2x.trixnity.client.verification.ActiveSasVerificationMethod
import de.connect2x.trixnity.client.verification.ActiveSasVerificationState
import de.connect2x.trixnity.client.verification.ActiveVerificationState
import io.github.capricornus007.nashira.i18n.Strings
import io.github.capricornus007.nashira.i18n.stringsFor
import io.github.capricornus007.nashira.matrix.DeviceSession
import io.github.capricornus007.nashira.matrix.MatrixSession
import io.github.capricornus007.nashira.matrix.SelfVerificationOption
import io.github.capricornus007.nashira.matrix.SelfVerificationStatus
import io.github.capricornus007.nashira.matrix.SessionTrust
import io.github.capricornus007.nashira.matrix.SessionLogout
import io.github.capricornus007.nashira.matrix.VerificationRepository
import io.github.capricornus007.nashira.settings.SettingsGroup
import io.github.capricornus007.nashira.settings.SettingsItem
import io.github.capricornus007.nashira.settings.SettingsNavigationItem
import kotlinx.coroutines.launch

/**
 * 帳戶與安全性頁：帳戶資訊、本裝置的自我驗證（復原金鑰／密語／另一台裝置）、
 * 工作階段清單與登出。
 */
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
    val selfStatus by repository.selfVerification.collectAsState(initial = null)
    val activeDevice by repository.activeDeviceVerification.collectAsState()
    val activeState = activeDevice?.state?.collectAsState()?.value
    val bootstrapping by repository.bootstrapRunning.collectAsState()

    var sessions by remember(session) { mutableStateOf<List<DeviceSession>>(emptyList()) }
    var sessionsError by remember { mutableStateOf<String?>(null) }
    var secretPrompt by remember { mutableStateOf<SelfVerificationOption?>(null) }
    var recoveryKeyToShow by remember { mutableStateOf<String?>(null) }
    var busyMessage by remember { mutableStateOf<String?>(null) }
    var actionError by remember { mutableStateOf<String?>(null) }

    suspend fun reloadSessions() {
        repository.sessions()
            .onSuccess { sessions = it; sessionsError = null }
            .onFailure { sessionsError = it.message ?: strings.sessionsLoadFailed }
    }

    LaunchedEffect(repository, selfStatus) { reloadSessions() }

    SettingsScaffold(title = strings.accountAndSecurity, onBack = onBack) {
        SettingsGroup(title = strings.account) {
            item { shape ->
                SettingsItem(
                    shape = shape,
                    icon = Icons.Filled.Person,
                    title = strings.accountId,
                    description = session.client.userId.full,
                )
            }
            item { shape ->
                SettingsItem(
                    shape = shape,
                    icon = Icons.Filled.Lock,
                    title = strings.deviceId,
                    description = session.client.deviceId,
                )
            }
        }

        SelfVerificationSection(
            strings = strings,
            status = selfStatus,
            bootstrapping = bootstrapping,
            onUseSecret = { secretPrompt = it },
            onUseOtherDevice = { option ->
                scope.launch {
                    busyMessage = strings.verificationWaitingOtherDevice
                    repository.startOtherDeviceVerification(option)
                        .onFailure { actionError = it.message ?: strings.verificationFailed }
                    busyMessage = null
                }
            },
            onBootstrap = {
                scope.launch {
                    repository.bootstrapCrossSigning()
                        .onSuccess { recoveryKeyToShow = it }
                        .onFailure { actionError = it.message ?: strings.bootstrapFailed }
                }
            },
        )

        SessionsSection(
            strings = strings,
            sessions = sessions,
            error = sessionsError,
            onRefresh = { scope.launch { reloadSessions() } },
            onVerify = { deviceId ->
                scope.launch {
                    repository.requestDeviceVerification(deviceId)
                        .onFailure { actionError = it.message ?: strings.verificationFailed }
                }
            },
            onLogoutSession = { deviceId ->
                scope.launch {
                    repository.logoutSession(deviceId)
                        .onSuccess { outcome ->
                            when (outcome) {
                                SessionLogout.Done -> reloadSessions()
                                is SessionLogout.OpenAccountManagement -> {
                                    // 委派認證伺服器（matrix.org）只能在帳戶管理頁登出裝置
                                    openLink(outcome.url)
                                    busyMessage = strings.sessionLogoutViaAccountPage
                                }
                            }
                        }
                        .onFailure { actionError = it.message ?: strings.sessionLogoutFailed }
                }
            },
        )

        busyMessage?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
        }
        actionError?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
        }

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(strings.logoutDevice, modifier = Modifier.padding(start = 8.dp))
        }
    }

    secretPrompt?.let { option ->
        SecretPromptDialog(
            strings = strings,
            option = option,
            onDismiss = { secretPrompt = null },
            onConfirm = { secret ->
                secretPrompt = null
                scope.launch {
                    busyMessage = strings.verificationInProgressShort
                    repository.verifyWithSecret(option, secret)
                        .onFailure { actionError = it.message ?: strings.verificationFailed }
                    busyMessage = null
                }
            },
        )
    }

    recoveryKeyToShow?.let { key ->
        RecoveryKeyDialog(strings = strings, recoveryKey = key, onDismiss = { recoveryKeyToShow = null })
    }
}

/** 自我驗證區：依帳戶狀態給出可用的驗證方式。 */
@Composable
private fun SelfVerificationSection(
    strings: Strings,
    status: SelfVerificationStatus?,
    bootstrapping: Boolean,
    onUseSecret: (SelfVerificationOption) -> Unit,
    onUseOtherDevice: (SelfVerificationOption.OtherDevice) -> Unit,
    onBootstrap: () -> Unit,
) {
    SettingsGroup(title = strings.deviceVerification) {
        when (status) {
            null -> item { shape ->
                SettingsItem(shape = shape, icon = Icons.Filled.Refresh, title = strings.verificationLoading)
            }
            SelfVerificationStatus.Verified -> item { shape ->
                SettingsItem(
                    shape = shape,
                    icon = Icons.Filled.CheckCircle,
                    title = strings.verificationDone,
                    description = strings.verificationDoneHint,
                )
            }
            SelfVerificationStatus.NeedsBootstrap -> {
                item { shape ->
                    SettingsItem(
                        shape = shape,
                        icon = Icons.Filled.Warning,
                        title = strings.crossSigningMissing,
                        description = strings.crossSigningMissingHint,
                    )
                }
                item { shape ->
                    SettingsItem(
                        shape = shape,
                        icon = Icons.Filled.Lock,
                        title = strings.bootstrapCrossSigning,
                        description = strings.bootstrapCrossSigningHint,
                        enabled = !bootstrapping,
                        onClick = onBootstrap,
                    ) {
                        if (bootstrapping) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    }
                }
            }
            is SelfVerificationStatus.NotReady -> item { shape ->
                SettingsItem(
                    shape = shape,
                    icon = Icons.Filled.Refresh,
                    title = strings.verificationNotReady,
                    description = status.reasons.joinToString(", "),
                )
            }
            is SelfVerificationStatus.NeedsVerification -> {
                item { shape ->
                    SettingsItem(
                        shape = shape,
                        icon = Icons.Filled.Warning,
                        title = strings.deviceUnverified,
                        description = strings.deviceUnverifiedHint,
                    )
                }
                status.options.forEach { option ->
                    item { shape ->
                        when (option) {
                            is SelfVerificationOption.RecoveryKey -> SettingsNavigationItem(
                                shape = shape,
                                icon = Icons.Filled.Lock,
                                title = strings.verifyWithRecoveryKey,
                                description = strings.verifyWithRecoveryKeyHint,
                                onClick = { onUseSecret(option) },
                            )
                            is SelfVerificationOption.Passphrase -> SettingsNavigationItem(
                                shape = shape,
                                icon = Icons.Filled.Lock,
                                title = strings.verifyWithPassphrase,
                                description = strings.verifyWithPassphraseHint,
                                onClick = { onUseSecret(option) },
                            )
                            is SelfVerificationOption.OtherDevice -> SettingsNavigationItem(
                                shape = shape,
                                icon = Icons.Filled.Person,
                                title = strings.verifyWithOtherDevice,
                                description = strings.verifyWithOtherDeviceHint,
                                onClick = { onUseOtherDevice(option) },
                            )
                        }
                    }
                }
            }
        }
    }
}


/** 工作階段清單：本裝置置頂，其他裝置可發起驗證或登出。 */
@Composable
private fun SessionsSection(
    strings: Strings,
    sessions: List<DeviceSession>,
    error: String?,
    onRefresh: () -> Unit,
    onVerify: (String) -> Unit,
    onLogoutSession: (String) -> Unit,
) {
    SettingsGroup(title = strings.sessions) {
        if (error != null) {
            item { shape ->
                SettingsItem(
                    shape = shape,
                    icon = Icons.Filled.Warning,
                    title = strings.sessionsLoadFailed,
                    description = error,
                    onClick = onRefresh,
                )
            }
            return@SettingsGroup
        }
        if (sessions.isEmpty()) {
            item { shape ->
                SettingsItem(shape = shape, icon = Icons.Filled.Refresh, title = strings.verificationLoading)
            }
            return@SettingsGroup
        }
        sessions.forEach { device ->
            item { shape ->
                SessionRow(shape, strings, device, onVerify, onLogoutSession)
            }
        }
    }
}

@Composable
private fun SessionRow(
    shape: androidx.compose.ui.graphics.Shape,
    strings: Strings,
    device: DeviceSession,
    onVerify: (String) -> Unit,
    onLogoutSession: (String) -> Unit,
) {
    var expanded by remember(device.deviceId) { mutableStateOf(false) }
    val trustLabel = when (device.trust) {
        SessionTrust.VERIFIED -> strings.sessionVerified
        SessionTrust.UNVERIFIED -> strings.sessionUnverified
        SessionTrust.BLOCKED -> strings.sessionBlocked
        SessionTrust.UNKNOWN -> strings.sessionUnknown
    }
    val subtitle = buildString {
        append(device.deviceId)
        append(" · ")
        append(trustLabel)
        if (device.isCurrent) {
            append(" · ")
            append(strings.sessionCurrent)
        }
        device.lastSeenIp?.let { append(" · $it") }
    }
    Column {
        SettingsItem(
            shape = shape,
            icon = if (device.trust == SessionTrust.VERIFIED) Icons.Filled.CheckCircle else Icons.Filled.Warning,
            title = device.displayName?.takeIf { it.isNotBlank() } ?: device.deviceId,
            description = subtitle,
            onClick = { expanded = !expanded },
        ) {
            if (device.trust == SessionTrust.VERIFIED) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
        if (expanded && !device.isCurrent) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (device.trust != SessionTrust.VERIFIED) {
                    Button(onClick = { onVerify(device.deviceId) }, modifier = Modifier.weight(1f)) {
                        Text(strings.verifySession)
                    }
                }
                OutlinedButton(onClick = { onLogoutSession(device.deviceId) }, modifier = Modifier.weight(1f)) {
                    Text(strings.logoutSession)
                }
            }
        }
    }
}

/** 輸入復原金鑰或密語的對話框。 */
@Composable
private fun SecretPromptDialog(
    strings: Strings,
    option: SelfVerificationOption,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var secret by remember { mutableStateOf("") }
    // 密語與復原金鑰預設遮蔽，右側眼睛可切明文
    var revealed by remember { mutableStateOf(false) }
    val isPassphrase = option is SelfVerificationOption.Passphrase
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isPassphrase) strings.verifyWithPassphrase else strings.verifyWithRecoveryKey) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    if (isPassphrase) strings.verifyWithPassphraseHint else strings.verifyWithRecoveryKeyHint,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedTextField(
                    value = secret,
                    onValueChange = { secret = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text(if (isPassphrase) strings.passphrase else strings.recoveryKey) },
                    visualTransformation = if (revealed) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        PasswordVisibilityToggle(
                            visible = revealed,
                            contentDescription = if (revealed) strings.hideSecret else strings.showSecret,
                            onToggle = { revealed = !revealed },
                        )
                    },
                )
            }
        },
        confirmButton = {
            TextButton(enabled = secret.isNotBlank(), onClick = { onConfirm(secret) }) {
                Text(strings.verifyThisDevice)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(strings.cancel) } },
    )
}

/** 顯示剛產生的復原金鑰。這是唯一一次能看到它。 */
@Composable
private fun RecoveryKeyDialog(strings: Strings, recoveryKey: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.recoveryKeyCreated) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(strings.recoveryKeyCreatedHint, style = MaterialTheme.typography.bodyMedium)
                Surface(color = MaterialTheme.colorScheme.surfaceContainerHigh, shape = MaterialTheme.shapes.medium) {
                    Text(
                        recoveryKey,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(strings.recoveryKeySaved) } },
    )
}
