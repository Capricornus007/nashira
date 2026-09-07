package io.github.capricornus007.nashira

import io.ktor.http.Url
import io.ktor.http.encodeURLParameter

/** Nashira-owned links used for callbacks and local navigation. */
object NashiraUri {
    const val scheme = "nashira"
    const val ssoHost = "sso"

    fun ssoCallback(loginToken: String): String =
        "$scheme://$ssoHost/callback?loginToken=${loginToken.encodeURLParameter()}"

    fun parseSsoCallback(value: String): String? = runCatching {
        val url = Url(value)
        if (url.protocol.name != scheme || url.host != ssoHost || value.substringBefore('?') != "$scheme://$ssoHost/callback") return null
        url.parameters["loginToken"]?.takeIf { it.isNotBlank() }
    }.getOrNull()
}
