package io.github.capricornus007.nashira.matrix

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.Dispatcher

/**
 * OkHttp 的預設 `maxRequestsPerHost` 是 5。
 *
 * 這對 Matrix 很致命：頭像／貼圖縮圖全都打同一台 homeserver，而實測 matrix.org 的
 * 縮圖端點 TTFB 就要 0.57–1.07 秒（要回源去對方伺服器抓原圖再生成縮圖，下載本身只有
 * 7–19 KB）。一次要填滿五十個頭像時，5 條併發等於排隊十輪 ≈ 10 秒以上——這就是
 * 「頭像載入很慢」裡客戶端能修的那一半。放寬到 24 條讓等待重疊。
 *
 * 桌面另外必須用 OkHttp 而不是 CIO：CIO 在 keep-alive 被伺服器關閉時會拋
 * 「Not enough data available」。
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
