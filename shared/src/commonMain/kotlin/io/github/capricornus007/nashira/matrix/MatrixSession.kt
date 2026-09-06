package io.github.capricornus007.nashira.matrix

import de.connect2x.trixnity.client.CryptoDriverModule
import de.connect2x.trixnity.client.MatrixClient
import de.connect2x.trixnity.client.MediaStoreModule
import de.connect2x.trixnity.client.cryptodriver.vodozemac.vodozemac
import de.connect2x.trixnity.client.media.okio.okio
import okio.Path.Companion.toPath
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * 各平台的 ktor 引擎：桌面 CIO 引擎在 keep-alive 連接被伺服器關閉時會拋
 * 「Not enough data available」，統一改用 OkHttp（Android 本來就用它）。
 */
internal expect fun platformHttpEngine(): io.ktor.client.engine.HttpClientEngine?

/**
 * 磁碟媒體快取。inMemory 版每次冷啟動都是空的，會讓成員列表／聊天室頭像
 * 每次開 app 全部重下一輪（使用者回報「頭像同步很慢」的真因）。
 */
private fun persistentMediaStore(databaseKey: String): MediaStoreModule =
    MediaStoreModule.okio(mediaStoreDirectory(databaseKey).toPath())

/**
 * Matrix 引擎會話：持有 MatrixClient 生命週期（登入 → startSync → 數據流）。
 */
class MatrixSession(
    val client: MatrixClient,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    fun start() {
        scope.launch { client.startSync() }
        // 通知在背景才需要發，所以觀察器綁在 session 上而不是 Compose 樹上
        scope.launch {
            io.github.capricornus007.nashira.watchNotifications(client, client.userId)
        }
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
            // 每房 20 則就夠填第一屏與清單預覽；50 則 × 幾十個房間的首次 sync
            // 會讓解析與解密的暫時分配衝到 250MB（Android 堆積上限 256MB）
            timeline = Filters.RoomFilter.RoomEventFilter(limit = 20),
        ),
    )

    private fun databaseKey(baseUrl: String, identity: String): String {
        val localpart = identity.removePrefix("@").substringBefore(':')
        return "${Url(baseUrl).host}-$localpart"
    }

    /**
     * 登出。除了清 token，**本機資料庫也要一起刪**：庫裡的 Account 行記著舊 deviceId，
     * 下次密碼登入拿到的是新 deviceId，Trixnity 的一致性檢查會擋下
     *（"newly authenticated deviceId … must match stored authenticated deviceId …"），
     * 表現就是「登出過就再也登不回去」。
     */
    fun logout() {
        val stored = runCatching { storage.load() }.getOrNull()
        _session.value?.close()
        _session.value = null
        storage.clear()
        stored?.let { clearPersistentStore(databaseKey(it.baseUrl, it.userId)) }
        _restoring.value = false
    }

    /**
     * 啟動恢復：磁碟有 token 則直接建 client（免密碼重登）；token 失效自動清除。
     *
     * **必須互斥**：Android 端有兩個呼叫者——前台服務的 onCreate 與 Compose 的
     * LaunchedEffect——只靠 `_session.value != null` 這個檢查擋不住，兩邊都會在
     * MatrixClient.create 這個 suspend 點之前通過檢查，於是建出兩個 client、
     * 兩條 sync 迴圈壓同一個 SQLite 檔，記憶體翻倍、同步反而更慢
     *（實測啟動 40 秒內堆積衝到 252MB 然後 OOM）。
     */
    private val restoreLock = Mutex()

    suspend fun restoreFromDisk() = restoreLock.withLock {
        if (_session.value != null) return@withLock
        val stored = storage.load() ?: run {
            _restoring.value = false
            return@withLock
        }
        _restoring.value = true
        val key = databaseKey(stored.baseUrl, stored.userId)
        val client = MatrixClient.create(
            repositoriesModule = persistentRepositories(key),
            mediaStoreModule = persistentMediaStore(key),
            cryptoDriverModule = CryptoDriverModule.vodozemac(),
            authProviderData = MatrixClientAuthProviderData.classic(
                baseUrl = Url(stored.baseUrl),
                accessToken = stored.accessToken,
                refreshToken = null,
            ),
            configuration = {
                this.syncFilter = MatrixEngine.syncFilter
                this.modulesFactories = trixnityModuleFactoriesWithPonies()
                this.httpClientEngine = platformHttpEngine()
            },
        ).getOrNull() ?: run {
            storage.clear()
            _restoring.value = false
            return@withLock
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

        val key = databaseKey(baseUrl, username)

        suspend fun create() = MatrixClient.create(
            repositoriesModule = persistentRepositories(key),
            mediaStoreModule = persistentMediaStore(key),
            cryptoDriverModule = CryptoDriverModule.vodozemac(),
            authProviderData = authData,
            configuration = {
                this.syncFilter = MatrixEngine.syncFilter
                this.modulesFactories = trixnityModuleFactoriesWithPonies()
                this.httpClientEngine = platformHttpEngine()
            },
        )

        // 密碼登入永遠拿到新 deviceId。若磁碟上還留著別的 deviceId 的庫（上次沒清乾淨、
        // 或 app 被強殺沒走 logout），create 會以一致性檢查失敗。這種情況把舊庫刪掉重試一次：
        // 舊 deviceId 的金鑰沒有對應 token 本來也用不了，留著只會擋住登入。
        val client = create().getOrElse { first ->
            if (!isStaleStoreError(first)) return Result.failure(first)
            clearPersistentStore(key)
            create().getOrElse { return Result.failure(it) }
        }

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

/**
 * Trixnity 的一致性檢查訊息長這樣：
 * "newly authenticated userId (…) and deviceId (X) must match stored authenticated userId (…) and deviceId (Y)."
 * 沒有專屬異常型別可判，只能認訊息。認錯了最壞情況是白刪一次本機快取（可重新同步）。
 */
private fun isStaleStoreError(error: Throwable): Boolean {
    val message = error.message ?: return false
    return message.contains("must match stored authenticated", ignoreCase = true)
}
