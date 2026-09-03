package io.github.capricornus007.nashira.desktop

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.capricornus007.nashira.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Nashira",
        icon = painterResource("nashira-icon.png"),
    ) {
        App(defaultDark = true)
    }
}
