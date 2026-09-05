package io.github.capricornus007.nashira.matrix

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

/** 桌面用 OkHttp：CIO 在 keep-alive 被伺服器關閉時拋「Not enough data available」。 */
actual fun platformHttpEngine(): HttpClientEngine? = OkHttp.create()
