package io.github.capricornus007.nashira

import de.connect2x.trixnity.client.MatrixClient
import io.github.capricornus007.nashira.matrix.MediaSource

/**
 * 手機不做本機代理：這端的正解是系統內建的播放器（ExoPlayer／Media3，自己會帶憑證、
 * 也有聲音與同步），列在待辦 #96。回 null 讓內嵌播放器退回「用抓好的整份檔播」。
 */
internal actual suspend fun mediaStreamUrl(client: MatrixClient, source: MediaSource): String? = null
