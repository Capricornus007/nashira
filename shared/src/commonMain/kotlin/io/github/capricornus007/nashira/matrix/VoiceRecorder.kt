package io.github.capricornus007.nashira.matrix

/**
 * P5-1 語音訊息：一次錄音的成品。Android 是 AAC/m4a（MediaRecorder），
 * 桌面是 PCM WAV（javax.sound.sampled）——兩種格式 Element/各客戶端都能播。
 */
data class RecordedVoice(
    val bytes: ByteArray,
    val durationMs: Long,
    val mimeType: String,
)

/**
 * 平台錄音器。生命週期：start() → (amplitude() 輪詢) → stop() 取得成品或 cancel() 丟棄。
 * stop() 回 null 表示錄得太短（<500ms，多半是誤觸），呼叫端直接當取消處理。
 */
expect class VoiceRecorder() {
    fun start()
    fun stop(): RecordedVoice?
    fun cancel()
    /** 給 UI 的音量脈衝（0..1），取不到就回 0。 */
    fun amplitude(): Float
}
