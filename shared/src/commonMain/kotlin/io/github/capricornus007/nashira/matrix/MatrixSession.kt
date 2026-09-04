package io.github.capricornus007.nashira.matrix

import de.connect2x.trixnity.client.CryptoDriverModule
import de.connect2x.trixnity.client.MatrixClient
import de.connect2x.trixnity.client.MediaStoreModule
import de.connect2x.trixnity.client.cryptodriver.vodozemac.vodozemac
import de.connect2x.trixnity.client.media.inMemory
import de.connect2x.trixnity.clientserverapi.client.MatrixClientAuthProviderData
import de.connect2x.trixnity.clientserverapi.client.classic
import de.connect2x.trixnity.clientserverapi.client.classicLogin
import de.connect2x.trixnity.client.create
import de.connect2x.trixnity.clientserverapi.model.authentication.IdentifierType
import de.connect2x.trixnity.clientserverapi.model.user.Filters
import io.ktor.http.Url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Matrix 引擎會話：持有 MatrixClient 生命週期（登入 → startSync → 數據流）。
 */
class MatrixSession(
    val client: MatrixClient,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    fun start() {
        scope.launch { client.startSync() }
    }

    fun close() {
        scope.cancel()
        client.close()
    }
}

/** 客戶端引擎：登入態單例 */
object MatrixEngine {
    private val _session = MutableStateFlow<MatrixSession?>(null)
    val session: StateFlow<MatrixSession?> = _session.asStateFlow()

    private val storage = TokenStorage()

    /** 磁碟憑證恢復中：UI 首帧就顯示啟動頁而不是登入表單 */
    private val _restoring = MutableStateFlow(runCatching { storage.load() != null }.getOrDefault(false))
    val restoring: StateFlow<Boolean> = _restoring.asStateFlow()

    // 手機先同步最近訊息；限制初次同步資料量，避免房間清單長時間只顯示載入中。
    private val syncFilter = Filters(
        room = Filters.RoomFilter(
            state = Filters.RoomFilter.RoomEventFilter(lazyLoadMembers = true),
            timeline = Filters.RoomFilter.RoomEventFilter(limit = 50),
        ),
    )

    private fun databaseKey(baseUrl: String, identity: String): String {
        val localpart = identity.removePrefix("@").substringBefore(':')
        return "${Url(baseUrl).host}-$localpart"
    }

    fun logout() {
        _session.value?.close()
        _session.value = null
        storage.clear()
        _restoring.value = false
    }

    /** 啟動恢復：磁碟有 token 則直接建 client（免密碼重登）；token 失效自動清除 */
    suspend fun restoreFromDisk() {
        if (_session.value != null) return
        val stored = storage.load() ?: run {
            _restoring.value = false
            return
        }
        _restoring.value = true
        val client = MatrixClient.create(
            repositoriesModule = persistentRepositories(databaseKey(stored.baseUrl, stored.userId)),
            mediaStoreModule = MediaStoreModule.inMemory(),
            cryptoDriverModule = CryptoDriverModule.vodozemac(),
            authProviderData = MatrixClientAuthProviderData.classic(
                baseUrl = Url(stored.baseUrl),
                accessToken = stored.accessToken,
                refreshToken = null,
            ),
            configuration = { this.syncFilter = MatrixEngine.syncFilter },
        ).getOrNull() ?: run {
            storage.clear()
            _restoring.value = false
            return
        }
        val session = MatrixSession(client)
        session.start()
        _session.value = session
        _restoring.value = false
    }

    /**
     * 密碼登入。baseUrl 例 "https://matrix.org"。
     * classicLogin 完成密碼換 token；MatrixClient.create 建客戶端並啟動同步。
     * 存儲：登入憑證與 Trixnity Room 資料庫都持久化，重啟後先恢復本機資料再背景同步。
     */
    suspend fun login(baseUrl: String, username: String, password: String): Result<Unit> {
        if (_session.value != null) return Result.failure(IllegalStateException("already logged in"))
        val authData = MatrixClientAuthProviderData.classicLogin(
            baseUrl = Url(baseUrl),
            identifier = IdentifierType.User(username),
            password = password,
            initialDeviceDisplayName = "Nashira",
        ).getOrElse { return Result.failure(it) }

        val client = MatrixClient.create(
            repositoriesModule = persistentRepositories(databaseKey(baseUrl, username)),
            mediaStoreModule = MediaStoreModule.inMemory(),
            cryptoDriverModule = CryptoDriverModule.vodozemac(),
            authProviderData = authData,
            configuration = { this.syncFilter = MatrixEngine.syncFilter },
        ).getOrElse { return Result.failure(it) }

        storage.save(
            baseUrl = baseUrl,
            userId = client.userId.full,
            deviceId = client.deviceId,
            accessToken = authData.accessToken,
        )
        val session = MatrixSession(client)
        session.start()
        _session.value = session
        return Result.success(Unit)
    }
}
