package io.github.capricornus007.nashira.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable

/** 平台桌布種子色：Android 動態取色開啟時回傳系統種子；其他情境回 null */
@Composable
expect fun wallpaperSeedColor(enabled: Boolean): Color?
