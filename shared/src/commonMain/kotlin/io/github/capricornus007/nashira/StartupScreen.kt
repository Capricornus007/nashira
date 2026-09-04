package io.github.capricornus007.nashira

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.capricornus007.nashira.i18n.stringsFor

/**
 * 啟動頁：磁碟有登入憑證時顯示，取代「先閃一次登入表單」的舊行為。
 * 只在 MatrixClient 尚未建立好的那段時間出現。
 */
@Composable
fun StartupScreen() {
    val strings = stringsFor(LocalUiState.current.language)
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            strings.appName,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )
        CircularProgressIndicator(Modifier.size(28.dp), strokeWidth = 3.dp)
        Text(
            strings.restoringSession,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
