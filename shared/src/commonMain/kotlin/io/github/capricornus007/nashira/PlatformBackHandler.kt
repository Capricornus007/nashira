package io.github.capricornus007.nashira

import androidx.compose.runtime.Composable

/** Android 使用系統返回手勢；桌面端由視窗管理器處理。 */
@Composable
expect fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit)
