package io.github.capricornus007.nashira.matrix

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual fun platformHttpEngine(): HttpClientEngine? = OkHttp.create()
