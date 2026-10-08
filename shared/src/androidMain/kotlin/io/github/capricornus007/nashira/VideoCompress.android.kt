package io.github.capricornus007.nashira

/**
 * 手機這輪**不壓**：正解是 Android 自己的 `MediaCodec`／`MediaMuxer`（零額外依賴、
 * 硬編不吃 CPU），列在待辦 #96。回原檔＝照舊發送，不因壓縮失敗擋住使用者。
 */
internal actual suspend fun compressVideoForSending(picked: PickedFile): PickedFile = picked
