package io.github.capricornus007.nashira.matrix

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/** 桌面：沒有執行時權限系統，錄音能力視為恆可用。 */
@Composable
actual fun rememberRecordingPermission(): RecordingPermission = remember {
    object : RecordingPermission {
        override fun isGranted(): Boolean = true
        override fun request() {}
    }
}
