package io.github.capricornus007.nashira.matrix

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.Dispatcher

/**
 * 同桌面：OkHttp 預設 `maxRequestsPerHost = 5`，而縮圖全打同一台 homeserver 且
 * 每張 TTFB 接近一秒（伺服器要回源生成縮圖），5 條併發會讓成員列表一路排隊。
 */
actual fun platformHttpEngine(): HttpClientEngine? = OkHttp.create {
    config {
        dispatcher(
            Dispatcher().apply {
                maxRequests = 64
                maxRequestsPerHost = 24
            },
        )
    }
}
