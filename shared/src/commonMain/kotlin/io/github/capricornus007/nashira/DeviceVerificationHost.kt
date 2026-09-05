package io.github.capricornus007.nashira

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.connect2x.trixnity.client.verification
import de.connect2x.trixnity.client.verification.ActiveSasVerificationMethod
import de.connect2x.trixnity.client.verification.ActiveSasVerificationState
import de.connect2x.trixnity.client.verification.ActiveVerificationState
import io.github.capricornus007.nashira.i18n.Strings
import io.github.capricornus007.nashira.i18n.stringsFor
import io.github.capricornus007.nashira.matrix.MatrixSession
import kotlinx.coroutines.launch

/**
 * 裝置驗證的唯一 UI 入口，掛在登入後的最外層。
 *
 * 為什麼要掛在根：`m.key.verification.request` 可能在任何時候從別的裝置送來
 * （Element 按「驗證此工作階段」就是這條）。以前只有「帳戶與安全性」頁在畫這個狀態機，
 * 使用者沒剛好停在那一頁就完全看不到請求，對方就一直等到超時。
 *
 * 進來的請求與自己發起的請求走同一個 `activeDeviceVerification`，所以這個對話框
 * 同時是「我發起、等對方」與「對方發起、等我」的介面。
 */
@Composable
fun DeviceVerificationHost(session: MatrixSession) {
    val strings = stringsFor(LocalUiState.current.language)
    val scope = rememberCoroutineScope()
    val verification by session.client.verification.activeDeviceVerification.collectAsState()
    val active = verification ?: return
    val state by active.state.collectAsState()

    // 使用者按過「關閉」就不要在同一次驗證裡反覆彈回來；換一次驗證（換 transactionId）重置
    var dismissedTransaction by rememberSaveable { mutableStateOf<String?>(null) }
    if (dismissedTransaction == active.transactionId) return

    val finished = state is ActiveVerificationState.Done || state is ActiveVerificationState.Cancel
    AlertDialog(
        onDismissRequest = {
            // 進行中不讓點外面關掉：誤觸會把對方卡在等待
            if (finished) dismissedTransaction = active.transactionId
        },
        title = {
            Text(
                when {
                    state is ActiveVerificationState.Done -> strings.verificationDone
                    state is ActiveVerificationState.Cancel -> strings.verificationCancelled
                    state is ActiveVerificationState.TheirRequest -> strings.verificationIncoming
                    else -> strings.verificationInProgressShort
                },
            )
        },
        text = { VerificationBody(strings, state, scope) },
        confirmButton = {
            when {
                finished -> TextButton(onClick = { dismissedTransaction = active.transactionId }) {
                    Text(strings.cancel)
                }
                else -> TextButton(
                    onClick = {
                        scope.launch { active.cancel("user cancelled") }
                        dismissedTransaction = active.transactionId
                    },
                ) { Text(strings.cancelVerification) }
            }
        },
    )
}

/** 依 Trixnity 狀態機給出下一步；每一步只有一個明確動作。 */
@Composable
private fun VerificationBody(
    strings: Strings,
    state: ActiveVerificationState,
    scope: kotlinx.coroutines.CoroutineScope,
) {
    val sasState = (state as? ActiveVerificationState.Start)
        ?.let { it.method as? ActiveSasVerificationMethod }
        ?.state?.collectAsState()?.value
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        when (state) {
            is ActiveVerificationState.TheirRequest -> {
                Text(strings.verificationIncomingHint, style = MaterialTheme.typography.bodyMedium)
                Button(onClick = { scope.launch { state.ready() } }, modifier = Modifier.fillMaxWidth()) {
                    Text(strings.acceptVerification)
                }
            }
            is ActiveVerificationState.OwnRequest ->
                Text(strings.verificationWaitingOtherDevice, style = MaterialTheme.typography.bodyMedium)
            is ActiveVerificationState.Ready -> Button(
                onClick = { state.methods.firstOrNull()?.let { method -> scope.launch { state.start(method) } } },
                modifier = Modifier.fillMaxWidth(),
            ) { Text(strings.startSasVerification) }
            is ActiveVerificationState.Start -> when (sasState) {
                is ActiveSasVerificationState.TheirSasStart -> Button(
                    onClick = { scope.launch { sasState.accept() } },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text(strings.acceptSas) }
                is ActiveSasVerificationState.ComparisonByUser -> {
                    Text(
                        strings.compareEmojiHint,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    // 沒有表情符號的對端（舊實作）只給十進位數字
                    Text(
                        if (sasState.emojis.isNotEmpty()) sasState.emojis.joinToString("   ") { it.second }
                        else sasState.decimal.joinToString(" "),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { scope.launch { sasState.match() } }, modifier = Modifier.weight(1f)) {
                            Text(strings.match)
                        }
                        OutlinedButton(onClick = { scope.launch { sasState.noMatch() } }, modifier = Modifier.weight(1f)) {
                            Text(strings.noMatch)
                        }
                    }
                }
                else -> Text(strings.verificationInProgressShort, style = MaterialTheme.typography.bodyMedium)
            }
            is ActiveVerificationState.Done ->
                Text(strings.verificationDoneHint, style = MaterialTheme.typography.bodyMedium)
            is ActiveVerificationState.Cancel ->
                Text(state.content.reason, style = MaterialTheme.typography.bodyMedium)
            else -> Text(strings.verificationInProgressShort, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
