package io.github.capricornus007.nashira.matrix

import androidx.compose.runtime.Composable

/**
 * P5-1 錄音權限的跨平台橋：Android 是執行時權限（RECORD_AUDIO），
 * 桌面沒有權限系統、恆為已授權。isGranted()/request() 讀的是 Compose 狀態，
 * 授權回來後重組會拿到新值。
 */
interface RecordingPermission {
    fun isGranted(): Boolean
    fun request()
}

@Composable
expect fun rememberRecordingPermission(): RecordingPermission
