package io.github.capricornus007.nashira

import java.util.Properties
import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists
import kotlin.io.path.reader
import kotlin.io.path.writer

actual class SettingsStorage actual constructor() {
    private val file = Path(System.getProperty("user.home") ?: ".", ".nashira", "settings.properties")

    actual fun load(): Map<String, String> {
        if (!file.exists()) return emptyMap()
        val props = Properties()
        runCatching { file.reader().use(props::load) }.onFailure { return emptyMap() }
        return props.stringPropertyNames().associateWith { props.getProperty(it) }
    }

    actual fun save(values: Map<String, String>) {
        val props = Properties().apply { values.forEach { (key, value) -> setProperty(key, value) } }
        runCatching {
            file.createParentDirectories()
            file.writer().use { props.store(it, "Nashira UI settings") }
        }
    }
}
