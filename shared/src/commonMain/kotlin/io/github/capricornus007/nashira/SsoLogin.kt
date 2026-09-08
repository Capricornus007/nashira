package io.github.capricornus007.nashira

/**
 * 開始 SSO 登入流程。
 *
 * Android：開瀏覽器到 homeserver SSO redirect，callback 是 `nashira://sso/callback`
 * deep link（由 MainActivity 的 intent filter 接收並呼叫 loginWithToken），此函式回傳 null。
 *
 * Desktop：起 localhost loopback HTTP server，開瀏覽器到 SSO redirect（redirectUrl 指向
 * 本機 callback），等瀏覽器帶 loginToken 回來後呼叫 loginWithToken 交換，回傳交換結果。
 */
expect suspend fun startSsoLogin(homeserver: String): Result<Unit>?
