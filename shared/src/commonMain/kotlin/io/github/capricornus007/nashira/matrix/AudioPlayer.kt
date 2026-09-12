package io.github.capricornus007.nashira.matrix

/**
 * P5-1 語音訊息播放器。UI 負責狀態（下載、播放中、暫停），這裡只提供平台原語。
 * Android 用 MediaPlayer（m4a/WAV/OGG 全通）；桌面用 javax.sound Clip（WAV），
 * 壓縮格式抓不到直解時走 ffplay（有裝 ffmpeg 就全格式能播，只是拿不到進度）。
 */
expect class AudioPlayer() {
    /** 準備播放。回 false 表示這個格式在此平台播不了（UI 顯示提示）。 */
    fun prepare(bytes: ByteArray, mimeType: String?): Boolean
    /** 開始／繼續播放。 */
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
