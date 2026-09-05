package io.github.capricornus007.nashira.desktop

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.window.application
import io.github.capricornus007.nashira.App
import io.github.capricornus007.nashira.theme.readXdgColorSchemeDark
import io.github.capricornus007.nashira.theme.xdgColorSchemeDarkFlow
import java.awt.Toolkit
import javax.swing.WindowConstants

fun main() = application {
    // Linux 無動態取色；「跟隨系統」用 xdg portal（fallback GTK 設定檔）判深淺，
    // 啟動先取一次快照，之後輪詢跟隨中途切換。
    val systemDark = remember { mutableStateOf(readXdgColorSchemeDark() ?: false) }
    LaunchedEffect(Unit) {
        xdgColorSchemeDarkFlow().collect { systemDark.value = it }
    }

    // 手動建 ComposeWindow：位置用 isLocationByPlatform 交還 WM。
    // Compose Desktop 預設窗口會寫 USPosition（user specified location）hint，
    // X11 WM 慣例必須尊重 USPosition 的幾何——bspwm 因此拒絕排布（浮動）。
    // AWT 對照實驗實證：isLocationByPlatform=true 的窗口無 USPosition、STATE: tiled。
    val window = remember {
        ComposeWindow().apply {
            defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            isLocationByPlatform = true
            title = "Nashira"
            setSize(820, 640)
            iconImage = runCatching {
                Toolkit.getDefaultToolkit().createImage(
                    ComposeWindow::class.java.classLoader.getResourceAsStream("nashira-icon.png")?.readBytes(),
                )
            }.getOrNull()
        }
    }
    LaunchedEffect(Unit) {
        window.setContent {
            App(defaultDark = systemDark.value)
        }
        window.isVisible = true
    }
}
