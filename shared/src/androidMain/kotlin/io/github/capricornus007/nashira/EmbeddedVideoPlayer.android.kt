package io.github.capricornus007.nashira

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import de.connect2x.trixnity.client.MatrixClient
import io.github.capricornus007.nashira.matrix.MediaSource

/**
 * 手機這輪仍顯示首格。系統內建的 ExoPlayer／Media3 才是這端的正解（0MB、有聲音、有同步），
 * 列在待辦 #96；這裡先不擺一個「只有封面、點了也不會動」的假控制列。
 */
@Composable
actual fun EmbeddedVideoPlayer(
    client: MatrixClient,
    source: MediaSource,
    bytes: ByteArray?,
    poster: ImageBitmap?,
    boxWidth: Dp,
    boxHeight: Dp,
    modifier: Modifier,
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        poster?.let {
            Image(
                bitmap = it,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }
    }
}
