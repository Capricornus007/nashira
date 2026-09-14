package io.github.capricornus007.nashira

/**
 * 音訊裝置選擇（語音錄音／播放）。
 *
 * 值是裝置的顯示名（桌面＝javax.sound Mixer 名），null＝系統預設。
 * UiState 載入與變更時同步進來，平台 actual（錄音器/播放器）開線時讀取。
 */
object AudioSelection {
    var input: String? = null
    var output: String? = null

    /** 錄音增益 0-100（百分比）：VoiceRecorder 對 PCM 樣本線性縮放。 */
    var inputGain: Int = 100

    /** 播放音量 0-100（百分比）：Clip.setVolume / ffplay -volume。 */
    var outputVolume: Int = 100

    /** 麥克風靜音（Discord 底欄麥克風鈕語義）：true 時錄音直接取消。 */
    var micMuted: Boolean = false

    /** 播放靜音（Discord 拒聽語義）：true 時語音訊息不出聲。 */
    var playbackMuted: Boolean = false
}

/**
 * 枚舉平台音訊裝置。Android 由系統自動路由（錄音走系統麥克風選擇 UI），
 * 回空列表——設置頁的音訊區塊也以 audioDeviceSettingsSupported 擋掉，
 * 這裡回空只是雙保險。
 */
expect object AudioDevices {
    /** 輸入（麥克風）裝置名；空＝枚舉不到（回退系統預設）。 */
    fun inputs(): List<String>

    /** 輸出（喇叭）裝置名。 */
    fun outputs(): List<String>
}
