package io.github.capricornus007.nashira.matrix

import de.connect2x.trixnity.client.MatrixClient
import io.github.capricornus007.nashira.i18n.friendlyError
import io.github.capricornus007.nashira.i18n.isCredentialFailure
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock

/**
 * 待送訊息卡住時，**當場問一次伺服器「為什麼送不出去」**。
 *
 * 為什麼要自己查：Trixnity 的 `OutboxMessageEventHandler` 只在拿到 `MatrixServerException`
 * 時才把 `sendError` 寫回資料庫。TLS 憑證過期、伺服器連不到、401 之後 token 也刷不動
 * ——這三種都**不會**寫 `sendError`，例外一路冒到 `retryLoop` 只留一行 warn
 * （2026-09-30 實測：三筆待送掛了一整天，資料庫裡連錯誤欄位都是空的）。
 * 所以畫面只能靠「超過 15 秒沒被回音」判斷卡住，判斷不出**原因**；
 * 而「網路一會兒就好」跟「你要重新登入」是两件完全不同的事，用戶得知道是哪一件（#75）。
 *
 * 兩段式探測：
 * 1. `/versions` 不帶憑證也該有回應 → 這關就測**傳輸層＋憑證層**
 *    （TLS 握手失敗、憑證過期／自簽、DNS 解析不到、連不上，全在這裡成形）。
 * 2. `/pushrules` 要登入 → 這關掛在 401／`M_UNKNOWN_TOKEN` 上就是**登入憑證失效**，
 *    不是網路問題。其他錯誤不能斷言成憑證問題（寧可回 null 講「原因不明」）。
 *
 * 快取 30 秒：一屏可能掛著好幾則卡住的訊息，不能每則各打一次伺服器。
 * 鎖保護「讀快取→打網路→寫快取」整段，否則十列會同時發現快取過期、一起衝出去。
 */
private const val ProbeCacheMillis = 30_000L

private val probeLock = Mutex()
private var probeReason: String? = null
private var probeAtMillis = 0L

/** 卡住的原因，翻成人話；查不出來回 null（呼叫端退回「未送達·仍在自動重試」）。 */
suspend fun diagnoseSendFailure(client: MatrixClient): String? = probeLock.withLock {
    val now = Clock.System.now().toEpochMilliseconds()
    if (probeAtMillis > 0 && now - probeAtMillis < ProbeCacheMillis) return probeReason
    val reason = runCatching { probeOnce(client) }.getOrElse { friendlyError(it) }
    probeReason = reason
    probeAtMillis = now
    reason
}

private suspend fun probeOnce(client: MatrixClient): String? {
    val transport = runCatching { client.api.server.getVersions().getOrThrow() }
    if (transport.isFailure) return friendlyError(transport.exceptionOrNull()!!)

    val authed = runCatching { client.api.push.getPushRules().getOrThrow() }
    val failure = authed.exceptionOrNull()
    if (failure != null && isCredentialFailure(failure)) return friendlyError(failure)
    return null
}
