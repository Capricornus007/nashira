package io.github.capricornus007.nashira.matrix

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/** Android：RECORD_AUDIO 執行時權限（Activity 結果 API）。 */
@Composable
actual fun rememberRecordingPermission(): RecordingPermission {
    val context = LocalContext.current
    val granted = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { ok -> if (ok) granted.value = true }
    return remember {
        object : RecordingPermission {
            override fun isGranted(): Boolean = granted.value
            override fun request() {
                if (!granted.value) launcher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }
}
