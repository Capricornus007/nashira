package io.github.capricornus007.nashira

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.DataLine
import javax.sound.sampled.Mixer
import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine

/**
 * 桌面音訊裝置枚舉（javax.sound.sampled Mixer）。
 *
 * 只列「真的支援我們用的線型」的 mixer：輸入用錄音規格
 * （16kHz/16bit/單聲道，VoiceRecorder 的格式），輸出用 Clip
 * （AudioPlayer 的 WAV 直解路徑）——避免把 MIDI 之類的埠全塞進列表。
 */
actual object AudioDevices {
    private val recordFormat = AudioFormat(16_000f, 16, 1, true, false)
    private val playFormat = AudioFormat(44_100f, 16, 2, true, false)

    actual fun inputs(): List<String> = runCatching {
        AudioSystem.getMixerInfo()
            .filter { info ->
                runCatching {
                    AudioSystem.getMixer(info).isLineSupported(
                        DataLine.Info(TargetDataLine::class.java, recordFormat),
                    )
                }.getOrDefault(false)
            }
            .map { it.name }
    }.getOrDefault(emptyList())

    actual fun outputs(): List<String> = runCatching {
        AudioSystem.getMixerInfo()
            .filter { info ->
                runCatching {
                    AudioSystem.getMixer(info).isLineSupported(
                        DataLine.Info(Clip::class.java, playFormat),
                    ) || AudioSystem.getMixer(info).isLineSupported(
                        DataLine.Info(SourceDataLine::class.java, playFormat),
                    )
                }.getOrDefault(false)
            }
            .map { it.name }
    }.getOrDefault(emptyList())
}

/** 依名字找 mixer info；null／找不到回 null（呼叫端走系統預設）。 */
internal fun mixerInfoFor(name: String?): Mixer.Info? {
    if (name.isNullOrBlank()) return null
    return runCatching {
        AudioSystem.getMixerInfo().firstOrNull { it.name == name }
    }.getOrNull()
}

/** 依名字找 mixer；null／找不到回系統預設 mixer。 */
internal fun mixerFor(name: String?): Mixer =
    mixerInfoFor(name)?.let { runCatching { AudioSystem.getMixer(it) }.getOrNull() }
        ?: AudioSystem.getMixer(null)
