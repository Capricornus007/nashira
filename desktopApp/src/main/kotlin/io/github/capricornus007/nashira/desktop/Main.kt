package io.github.capricornus007.nashira.desktop

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.capricornus007.nashira.App
import io.github.capricornus007.nashira.theme.readXdgColorSchemeDark
import io.github.capricornus007.nashira.theme.xdgColorSchemeDarkFlow

fun main() = application {
    // Linux 無動態取色；「跟隨系統」用 xdg portal（fallback GTK 設定檔）判深淺，
    // 啟動先取一次快照，之後輪詢跟隨中途切換。
    var systemDark by remember { mutableStateOf(readXdgColorSchemeDark() ?: false) }
    LaunchedEffect(Unit) {
        xdgColorSchemeDarkFlow().collect { systemDark = it }
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Nashira",
        icon = painterResource("nashira-icon.png"),
        state = rememberWindowState(size = DpSize(1100.dp, 760.dp)),
    ) {
        App(defaultDark = systemDark)
    }
}
