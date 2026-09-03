package io.github.capricornus007.nashira.matrix

import java.util.Properties
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText

actual class TokenStorage actual constructor() {
    private val file = Path(System.getProperty("user.home") ?: ".", ".nashira", "session.properties")

    actual fun save(baseUrl: String, userId: String, deviceId: String, accessToken: String) {
        val props = Properties().apply {
            setProperty("baseUrl", baseUrl)
            setProperty("userId", userId)
            setProperty("deviceId", deviceId)
            setProperty("accessToken", accessToken)
        }
        file.createParentDirectories()
        file.writeText(props.entries.joinToString("\n") { (k, v) -> "$k=$v" })
    }

    actual fun load(): StoredToken? {
        if (!file.exists()) return null
        val props = Properties().apply { load(file.readText().reader()) }
        val token = props.getProperty("accessToken") ?: return null
        return StoredToken(
            baseUrl = props.getProperty("baseUrl") ?: "",
            userId = props.getProperty("userId") ?: "",
            deviceId = props.getProperty("deviceId") ?: "",
            accessToken = token,
        )
    }

    actual fun clear() {
        file.toFile().delete()
    }
}
