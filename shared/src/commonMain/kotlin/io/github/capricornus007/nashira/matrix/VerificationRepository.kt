package io.github.capricornus007.nashira.matrix

import de.connect2x.trixnity.client.key
import de.connect2x.trixnity.client.key.KeyService
import de.connect2x.trixnity.client.verification
import de.connect2x.trixnity.client.verification.ActiveDeviceVerification
import de.connect2x.trixnity.client.verification.ActiveUserVerification
import de.connect2x.trixnity.client.verification.SelfVerificationMethod
import de.connect2x.trixnity.client.verification.VerificationService
import de.connect2x.trixnity.core.model.UserId
import de.connect2x.trixnity.crypto.key.DeviceTrustLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/** 本帳戶的另一個工作階段。 */
data class DeviceSession(
    val deviceId: String,
    val displayName: String?,
    val lastSeenIp: String?,
    val lastSeenTimestamp: Long?,
    val isCurrent: Boolean,
    val trust: SessionTrust,
)

/** 工作階段的信任狀態，決定 UI 顯示盾牌或警告。 */
enum class SessionTrust { VERIFIED, UNVERIFIED, BLOCKED, UNKNOWN }

/**
 * 本裝置能用哪些方式完成自我驗證。對齊 Element 的兩條路：
 * 用復原金鑰／密語直接驗證，或是找另一台已驗證的裝置掃 SAS。
 */
sealed interface SelfVerificationOption {
    /** 輸入 base58 復原金鑰。 */
    data class RecoveryKey(internal val method: SelfVerificationMethod.AesHmacSha2RecoveryKey) : SelfVerificationOption

    /** 輸入使用者設定的安全密語。 */
    data class Passphrase(internal val method: SelfVerificationMethod.AesHmacSha2RecoveryKeyWithPbkdf2Passphrase) : SelfVerificationOption

    /** 由另一台已交叉簽署的裝置確認（SAS 表情符號比對）。 */
    data class OtherDevice(internal val method: SelfVerificationMethod.CrossSignedDeviceVerification) : SelfVerificationOption
}

/** 自我驗證的整體狀態。 */
sealed interface SelfVerificationStatus {
    /** 本裝置已被交叉簽署，不需要再驗證。 */
    data object Verified : SelfVerificationStatus

    /** 帳戶已啟用交叉簽署，本裝置可用這些方式驗證自己。 */
    data class NeedsVerification(val options: List<SelfVerificationOption>) : SelfVerificationStatus

    /** 帳戶還沒有交叉簽署金鑰，要先在本裝置初始化（會產生復原金鑰）。 */
    data object NeedsBootstrap : SelfVerificationStatus

    /** 同步或金鑰尚未就緒，先等一下。 */
    data class NotReady(val reasons: List<String>) : SelfVerificationStatus
}

/**
 * 驗證與金鑰的單一入口。UI 只描述狀態與呼叫動作，
 * 實際的 `m.key.verification.*`、SAS、SSSS 與交叉簽署都交給 Trixnity。
 */
class VerificationRepository(private val session: MatrixSession) {
    private val service: VerificationService = session.client.verification
    private val keys: KeyService = session.client.key

    val activeDeviceVerification: StateFlow<ActiveDeviceVerification?> get() = service.activeDeviceVerification
    val activeUserVerifications: StateFlow<List<ActiveUserVerification>> get() = service.activeUserVerifications
    val bootstrapRunning: StateFlow<Boolean> get() = keys.bootstrapRunning

    /** 本裝置的自我驗證狀態；同步中會先回 NotReady。 */
    val selfVerification: Flow<SelfVerificationStatus> =
        service.getSelfVerificationMethods()
            .map { methods ->
                when (methods) {
                    is VerificationService.SelfVerificationMethods.AlreadyCrossSigned ->
                        SelfVerificationStatus.Verified
                    is VerificationService.SelfVerificationMethods.NoCrossSigningEnabled ->
                        SelfVerificationStatus.NeedsBootstrap
                    is VerificationService.SelfVerificationMethods.PreconditionsNotMet ->
                        SelfVerificationStatus.NotReady(methods.reasons.map { it::class.simpleName ?: "unknown" })
                    is VerificationService.SelfVerificationMethods.CrossSigningEnabled ->
                        SelfVerificationStatus.NeedsVerification(methods.methods.mapNotNull(::optionOf))
                }
            }
            .flowOn(Dispatchers.Default)

    private fun optionOf(method: SelfVerificationMethod): SelfVerificationOption? = when (method) {
        is SelfVerificationMethod.AesHmacSha2RecoveryKey -> SelfVerificationOption.RecoveryKey(method)
        is SelfVerificationMethod.AesHmacSha2RecoveryKeyWithPbkdf2Passphrase -> SelfVerificationOption.Passphrase(method)
        is SelfVerificationMethod.CrossSignedDeviceVerification -> SelfVerificationOption.OtherDevice(method)
        else -> null
    }

    /** 用復原金鑰或密語驗證本裝置；成功後 Trixnity 會自動取回並簽署金鑰。 */
    suspend fun verifyWithSecret(option: SelfVerificationOption, secret: String): Result<Unit> = when (option) {
        is SelfVerificationOption.RecoveryKey -> option.method.verify(secret.trim())
        is SelfVerificationOption.Passphrase -> option.method.verify(secret)
        is SelfVerificationOption.OtherDevice -> Result.failure(IllegalArgumentException("此方式需要另一台裝置確認"))
    }

    /** 向自己其他已驗證的裝置發起 SAS 驗證。 */
    suspend fun startOtherDeviceVerification(option: SelfVerificationOption.OtherDevice): Result<ActiveDeviceVerification> =
        option.method.createDeviceVerification()

    /** 對指定裝置發起驗證（手動輸入裝置 ID 的路徑）。 */
    suspend fun requestDeviceVerification(deviceId: String): Result<ActiveDeviceVerification> =
        service.createDeviceVerificationRequest(session.client.userId, setOf(deviceId))

    /** 向其他 Matrix 使用者發起驗證。 */
    suspend fun requestUserVerification(userId: String): Result<ActiveUserVerification> =
        service.createUserVerificationRequest(UserId(userId))

    /**
     * 初始化交叉簽署並取得復原金鑰。**只在帳戶尚未啟用交叉簽署時呼叫**，
     * 回傳的金鑰要讓使用者立刻抄下來——之後拿不回來。
     */
    suspend fun bootstrapCrossSigning(): Result<String> = runCatching {
        val bootstrap = keys.bootstrapCrossSigning()
        bootstrap.result.getOrThrow()
        bootstrap.recoveryKey
    }

    /** 用密語初始化交叉簽署（同時產生復原金鑰）。 */
    suspend fun bootstrapCrossSigningFromPassphrase(passphrase: String): Result<String> = runCatching {
        val bootstrap = keys.bootstrapCrossSigningFromPassphrase(passphrase)
        bootstrap.result.getOrThrow()
        bootstrap.recoveryKey
    }

    /** 本帳戶的工作階段清單，附各自的信任狀態。 */
    suspend fun sessions(): Result<List<DeviceSession>> = runCatching {
        val devices = session.client.api.device.getDevices().getOrThrow()
        devices.map { device ->
            DeviceSession(
                deviceId = device.deviceId,
                displayName = device.displayName,
                lastSeenIp = device.lastSeenIp,
                lastSeenTimestamp = device.lastSeenTs,
                isCurrent = device.deviceId == session.client.deviceId,
                trust = trustOf(device.deviceId),
            )
        }.sortedWith(compareByDescending<DeviceSession> { it.isCurrent }.thenByDescending { it.lastSeenTimestamp ?: 0L })
    }

    private suspend fun trustOf(deviceId: String): SessionTrust =
        when (val level = keys.getTrustLevel(session.client.userId, deviceId).first()) {
            is DeviceTrustLevel.CrossSigned -> if (level.verified) SessionTrust.VERIFIED else SessionTrust.UNVERIFIED
            is DeviceTrustLevel.NotCrossSigned -> SessionTrust.UNVERIFIED
            is DeviceTrustLevel.Blocked -> SessionTrust.BLOCKED
            is DeviceTrustLevel.Invalid -> SessionTrust.BLOCKED
            else -> SessionTrust.UNKNOWN
        }

    /** 登出指定工作階段。伺服器要求二次認證時回傳未完成的 UIA 錯誤。 */
    suspend fun logoutSession(deviceId: String): Result<Unit> = runCatching {
        val uia = session.client.api.device.deleteDevice(deviceId).getOrThrow()
        uia.let { result ->
            // 沒有互動認證需求時 Trixnity 直接回 Success；有需求就丟出來讓 UI 顯示
            if (result !is de.connect2x.trixnity.clientserverapi.client.UIA.Success) {
                error("需要密碼再次確認才能登出此工作階段")
            }
        }
    }
}
