package io.github.capricornus007.nashira.matrix

import de.connect2x.trixnity.client.verification.ActiveDeviceVerification
import de.connect2x.trixnity.client.verification.ActiveUserVerification
import de.connect2x.trixnity.client.verification.VerificationService
import de.connect2x.trixnity.core.model.UserId
import kotlinx.coroutines.flow.StateFlow

/**
 * 驗證工作階段的單一入口。UI 只顯示狀態，不自行拼 Matrix 驗證事件。
 * Trixnity 會負責 m.key.verification.*、SAS 與裝置信任更新。
 */
class VerificationRepository(private val session: MatrixSession) {
    private val service: VerificationService = session.client.di.get()

    val activeDeviceVerification get() = service.activeDeviceVerification
    val activeUserVerifications get() = service.activeUserVerifications

    /** 向指定裝置發起 m.sas.v1 驗證工作階段。 */
    suspend fun requestDeviceVerification(deviceId: String): Result<String> = runCatching {
        service.createDeviceVerificationRequest(
            session.client.userId,
            setOf(deviceId),
        ).getOrThrow().transactionId ?: "pending"
    }

    /** 向指定 Matrix 使用者發起使用者驗證，實際事件由 Trixnity 管理。 */
    suspend fun requestUserVerification(userId: String): Result<String> = runCatching {
        service.createUserVerificationRequest(UserId(userId)).getOrThrow().transactionId ?: "pending"
    }

    fun supportedMethods(): Set<String> = setOf("m.sas.v1")
}
