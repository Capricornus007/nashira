package io.github.capricornus007.nashira

import io.github.capricornus007.nashira.i18n.Strings

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
 * 一個音訊裝置。
 * - [id]：存進設定、也給平台 actual 開線用的識別名（桌面＝javax.sound 的 Mixer 名，
 *   形如 `Generic_1 [plughw:1,0]`）。**不能拿去顯示**——那是 ALSA 装置路徑，人看不懂。
 * - [label]：給人看的名字（桌面盡量換成音訊服務給的可讀描述，換不到才退回 id）。
 */
data class AudioDevice(val id: String, val label: String)

/**
 * 枚舉平台音訊裝置。Android 由系統自動路由（錄音走系統麥克風選擇 UI），
 * 回空列表——設置頁的音訊區塊也以 audioDeviceSettingsSupported 擋掉，
 * 這裡回空只是雙保險。
 */
expect object AudioDevices {
    /** 輸入（麥克風）裝置；空＝枚舉不到（回退系統預設）。 */
    fun inputs(): List<AudioDevice>

    /** 輸出（喇叭）裝置。 */
    fun outputs(): List<AudioDevice>

    /**
     * 「系統預設」目前實際落到哪個裝置的可讀名（Discord 的
     * 「系統預設: Ryzen HD Audio Controller Speaker」顯示法）。查不到回 null，
     * UI 就只顯示「系統預設」四个字。
     */
    fun defaultInputLabel(): String?

    fun defaultOutputLabel(): String?
}

/**
 * 顯示用的裝置清單。桌面常把同一張卡列成兩筆（ALSA 預設 pcm 與那張卡的
 * `plughw:C,D`），換成可讀名之後就會撞名，所以把 `[default]` 排前面、再依顯示名
 * 去重。底欄快捷面板與設置頁都走這裡，兩邊清單才會一致。
 */
fun audioDevicesFor(isInput: Boolean): List<AudioDevice> =
    (if (isInput) AudioDevices.inputs() else AudioDevices.outputs())
        .sortedBy { if (it.id.endsWith("[default]")) 0 else 1 }
        .distinctBy { it.label }

/**
 * 一行「目前選了哪個裝置」的顯示文字：selectedId 為 null 就是系統預設，
 * 這時照 Discord 的寫法把實際裝置名帶出來（`系統預設: Ryzen …`）；查不到名字
 * 就只講「系統預設」。選了裝置則顯示它的可讀名——存進設定的是 id（ALSA 路徑名），
 * 直接拿去顯示會變成 `Generic_1 [plughw:1,0]` 那種沒人看得懂的東西。
 */
fun audioDeviceLabel(
    strings: Strings,
    devices: List<AudioDevice>,
    selectedId: String?,
    defaultName: String?,
): String = when {
    selectedId != null -> devices.firstOrNull { it.id == selectedId }?.label ?: selectedId
    defaultName != null -> strings.audioDeviceDefaultNamed(defaultName)
    else -> strings.audioDeviceDefault
}
