package io.github.capricornus007.nashira

/**
 * 手機這輪回 null：照樣發 m.video，只是 `info` 裡沒有 w/h/duration 與縮圖。
 * 正解是 `MediaMetadataRetriever`（尺寸／時長）＋ `createThumbnailFromUri`（縮圖），
 * 歸在待辦 #96（手機端追平桌面）。
 */
internal actual suspend fun probeVideoForSending(bytes: ByteArray): VideoSendMeta? = null
