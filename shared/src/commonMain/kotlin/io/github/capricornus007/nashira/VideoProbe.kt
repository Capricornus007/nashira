package io.github.capricornus007.nashira

/**
 * 發送影片前要補上的資訊（`m.video` 的 `info` 與縮圖）。
 *
 * 缺了這些，對方（以及我們自己的時間線）只會把它當附件看：
 * `info` 沒有 w/h 就排不出畫面比例、沒有 duration 就不知道長短（行內連播的判斷也要它），
 * 沒縮圖就是一片黑。
 */
internal data class VideoSendMeta(
    val widthPx: Int?,
    val heightPx: Int?,
    val durationMs: Long?,
    /** JPEG 縮圖本體；null 就單純不給 `thumbnail_url`。 */
    val thumbnailJpeg: ByteArray?,
)

/** 平台沒有可用的探測器時回 null（照樣發 m.video，只是 `info` 精簡）。 */
internal expect suspend fun probeVideoForSending(bytes: ByteArray): VideoSendMeta?
