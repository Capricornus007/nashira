package io.github.capricornus007.nashira

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.github.capricornus007.nashira.i18n.stringsFor
import io.github.capricornus007.nashira.matrix.MatrixEngine
import kotlinx.coroutines.launch

/** 登入頁：HS 位址 + 用戶名 + 密碼 */
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val strings = stringsFor(LocalUiState.current.language)
    var baseUrl by remember { mutableStateOf("https://matrix.org") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var passwordRevealed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            strings.appName,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            strings.tagline,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = baseUrl,
            onValueChange = { baseUrl = it },
            label = { Text(strings.homeserver) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
        )
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(strings.loginUsername) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(strings.loginPassword) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordRevealed) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                PasswordVisibilityToggle(
                    visible = passwordRevealed,
                    contentDescription = if (passwordRevealed) strings.hideSecret else strings.showSecret,
                    onToggle = { passwordRevealed = !passwordRevealed },
                )
            },
        )
        error?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Button(
            onClick = {
                if (busy) return@Button
                busy = true
                error = null
                scope.launch {
                    MatrixEngine.login(baseUrl.trim(), username.trim(), password)
                        .onSuccess { onLoginSuccess() }
                        .onFailure { error = io.github.capricornus007.nashira.i18n.friendlyError(it) }
                    busy = false
                }
            },
            enabled = !busy && username.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (busy) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(end = 8.dp).size(16.dp),
                    strokeWidth = 2.dp,
                )
            }
            Text(strings.loginSubmit)
        }
    }
}
