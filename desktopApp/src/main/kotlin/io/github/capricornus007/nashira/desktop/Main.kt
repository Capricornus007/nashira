package io.github.capricornus007.nashira.desktop

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.capricornus007.nashira.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Nashira",
        icon = painterResource("nashira-icon.png"),
        state = rememberWindowState(size = DpSize(1100.dp, 760.dp)),
    ) {
        App(defaultDark = true)
    }
}
