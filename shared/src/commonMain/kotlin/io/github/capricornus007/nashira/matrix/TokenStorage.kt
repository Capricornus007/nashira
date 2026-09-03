package io.github.capricornus007.nashira.matrix

/** 登入憑證持久化：accessToken/userId/deviceId/baseUrl 存磁碟，app 重啟免重登 */
expect class TokenStorage() {
    fun save(baseUrl: String, userId: String, deviceId: String, accessToken: String)
    fun load(): StoredToken?
    fun clear()
}

data class StoredToken(
    val baseUrl: String,
    val userId: String,
    val deviceId: String,
    val accessToken: String,
)
