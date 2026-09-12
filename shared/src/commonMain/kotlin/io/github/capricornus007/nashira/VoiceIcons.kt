package io.github.capricornus007.nashira

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * P5-1 語音訊息需要的三個圖標（Mic/Stop/Pause）不在 material-icons-core 的精簡集裡，
 * 而 material-icons-extended 有數十 MB——直接用 Material 圖標的標準向量路徑自建。
 * 路徑取自 Material Icons（Apache-2.0）的 24dp 版。
 *
 * **fill 必須顯式給**：ImageVector.Builder.addPath 的 fill 預設是 null（不畫），
 * Icon 的 tint 是靠 ColorFilter 蓋在已有顏料上——沒有 fill 的路徑整條透明
 * （真機實證：麥克風鈕一片空白，輸入列看起來整排偏左）。
 */
internal object VoiceIcons {
    private fun vector(name: String, path: String): ImageVector =
        ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).addPath(
            pathData = PathParser().parsePathString(path).toNodes(),
            fill = SolidColor(Color.Black),
        ).build()

    val Mic: ImageVector by lazy { vector("Mic", "M12 14c1.66 0 2.99-1.34 2.99-3L15 5c0-1.66-1.34-3-3-3S9 3.34 9 5v6c0 1.66 1.34 3 3 3zm5.3-3c0 3-2.54 5.1-5.3 5.1S6.7 14 6.7 11H5c0 3.41 2.72 6.23 6 6.72V21h2v-3.28c3.28-.48 6-3.3 6-6.72h-1.7z") }

    val Stop: ImageVector by lazy { vector("Stop", "M6 6h12v12H6z") }

    val Pause: ImageVector by lazy { vector("Pause", "M6 19h4V5H6v14zm8-14v14h4V5h-4z") }
}
