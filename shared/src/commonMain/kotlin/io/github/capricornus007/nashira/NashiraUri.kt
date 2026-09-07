package io.github.capricornus007.nashira

import io.ktor.http.Url
import io.ktor.http.encodeURLParameter

/** Nashira-owned links used for callbacks and local navigation. */
object NashiraUri {
    const val scheme = "nashira"
    const val ssoHost = "sso"

    fun ssoCallback(loginToken: String, baseUrl: String = "https://matrix.org"): String =
        "$scheme://$ssoHost/callback?loginToken=${loginToken.encodeURLParameter()}&baseUrl=${baseUrl.encodeURLParameter()}"

    fun parseSsoCallback(value: String): Pair<String, String>? = runCatching {
        val url = Url(value)
        if (url.protocol.name != scheme || url.host != ssoHost || value.substringBefore('?') != "$scheme://$ssoHost/callback") return null
        val token = url.parameters["loginToken"]?.takeIf { it.isNotBlank() } ?: return null
        val baseUrl = url.parameters["baseUrl"]?.takeIf { it.isNotBlank() } ?: "https://matrix.org"
        token to baseUrl
    }.getOrNull()
}
