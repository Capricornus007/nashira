package io.github.capricornus007.nashira

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * material-icons-core 精簡集缺的圖標在這裡用 Material 標準向量路徑自建
 * （material-icons-extended 有數十 MB，不值得引入）。
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

    /** 麥克風（輸入列空白時最右）。 */
    val Mic: ImageVector by lazy { vector("Mic", "M12 14c1.66 0 2.99-1.34 2.99-3L15 5c0-1.66-1.34-3-3-3S9 3.34 9 5v6c0 1.66 1.34 3 3 3zm5.3-3c0 3-2.54 5.1-5.3 5.1S6.7 14 6.7 11H5c0 3.41 2.72 6.23 6 6.72V21h2v-3.28c3.28-.48 6-3.3 6-6.72h-1.7z") }

    /** 停止（錄音列）——M3 圓角方形，不是生硬的直角方塊。 */
    val Stop: ImageVector by lazy { vector("Stop", "M8 5h8c1.66 0 3 1.34 3 3v8c0 1.66-1.34 3-3 3H8c-1.66 0-3-1.34-3-3V8c0-1.66 1.34-3 3-3z") }

    /** 暫停（播放中）。 */
    val Pause: ImageVector by lazy { vector("Pause", "M6 19h4V5H6v14zm8-14v14h4V5h-4z") }

    /** 照片／圖片（附件選單）——山形風景框，不是 icons-core 湊數的 AccountBox。 */
    val Photo: ImageVector by lazy { vector("Photo", "M21 19V5c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zM8.5 13.5l2.5 3.01L14.5 12l4.5 6H5l3.5-4.5z") }

    /** 檔案（附件選單）——折角文件＋文字行，不是 icons-core 湊數的 List。 */
    val File: ImageVector by lazy { vector("File", "M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z") }
}
