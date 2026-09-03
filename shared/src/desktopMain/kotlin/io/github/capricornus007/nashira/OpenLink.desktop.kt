package io.github.capricornus007.nashira

import java.awt.Desktop
import java.net.URI

actual fun openLink(url: String) {
    if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().browse(URI(url))
    }
}
