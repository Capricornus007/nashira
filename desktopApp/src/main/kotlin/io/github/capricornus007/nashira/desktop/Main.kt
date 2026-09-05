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
import androidx.compose.ui.window.WindowPosition
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
        // 官方 Window() composable：Skia 面板與 Compose 環境由它全權管理——
        // 實驗實證在 bspwm 下平鋪與內容跟隨 resize 皆正常。position 用
        // PlatformDefault 把定位交還 WM。教訓：不要手動建 ComposeWindow 再
        // 插手 isVisible——那會產生不受管理的 Skia 面板（實測貼左上、不跟隨）。
        state = rememberWindowState(
            position = WindowPosition.PlatformDefault,
            size = DpSize(900.dp, 640.dp),
        ),
    ) {
        App(defaultDark = systemDark)
    }
}
