package io.github.capricornus007.nashira

import androidx.compose.runtime.Composable

// 桌面沒有軟鍵盤；面板高度由呼叫端用預設值。
@Composable
internal actual fun systemImeHeightPx(): Int = 0
