package io.github.capricornus007.nashira.matrix

/**
 * P5-1 語音訊息播放器。UI 負責狀態（下載、播放中、暫停），這裡只提供平台原語。
 * **prepare 是非同步的**：解碼/開檔是阻塞工作，絕不能在主執行緒做——
 * 真機實證同步 prepare 在點擊處理器裡直接把 UI 凍住（ANR 觀感）。
 * onReady 在主執行緒回呼。
 */
expect class AudioPlayer() {
    /**
     * 後台準備播放；完成後 onReady(true)（主執行緒）。格式播不了回 onReady(false)。
     * 準備期間重複呼叫會取消前一次（以最後一次為準）。
     */
    fun prepare(bytes: ByteArray, mimeType: String?, onReady: (Boolean) -> Unit)
    /** 開始／繼續播放（須在 onReady(true) 之後）。 */
    fun play()
    /** 暫停。 */
    fun pause()
    /** 停止並釋放資源；之後再播要重新 prepare。 */
    fun release()
    /** 目前播放位置（ms）；拿不到就回 -1。 */
    fun positionMs(): Long
    /** 總長度（ms）；拿不到就回 -1。 */
    fun durationMs(): Long
    /** 是否還在播。 */
    fun isPlaying(): Boolean
}
