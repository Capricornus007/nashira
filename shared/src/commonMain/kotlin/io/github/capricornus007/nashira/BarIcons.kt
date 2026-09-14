package io.github.capricornus007.nashira

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * 底欄（帳號列）用的圖標——material-icons-core 不含 Mic/Headset/VolumeUp
 * （那些在 extended 集，整包 ~30MB 不值得引入），照 VoiceIcons 的模式手繪
 * 標準 Material 路徑。ImageVector.Builder 的 addPath 預設 fill=null 會隱形，
 * 一定要顯式 SolidColor。
 */
object BarIcons {
    val Mic: ImageVector by lazy {
        ImageVector.Builder(name = "Mic", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 14f)
                curveTo(13.66f, 14f, 14.99f, 12.66f, 14.99f, 11f)
                lineTo(15f, 5f)
                curveTo(15f, 3.34f, 13.66f, 2f, 12f, 2f)
                curveTo(10.34f, 2f, 9f, 3.34f, 9f, 5f)
                lineTo(9f, 11f)
                curveTo(9f, 12.66f, 10.34f, 14f, 12f, 14f)
                close()
                moveTo(17.3f, 11f)
                curveTo(17.3f, 14f, 14.76f, 16.1f, 12f, 16.1f)
                curveTo(9.24f, 16.1f, 6.7f, 14f, 6.7f, 11f)
                lineTo(5f, 11f)
                curveTo(5f, 14.41f, 7.72f, 17.23f, 11f, 17.72f)
                lineTo(11f, 21f)
                lineTo(13f, 21f)
                lineTo(13f, 17.72f)
                curveTo(16.28f, 17.23f, 19f, 14.41f, 19f, 11f)
                close()
            }
        }.build()
    }

    val Headset: ImageVector by lazy {
        ImageVector.Builder(name = "Headset", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 1f)
                curveTo(7.03f, 1f, 3f, 5.03f, 3f, 10f)
                lineTo(3f, 17f)
                curveTo(3f, 18.66f, 4.34f, 20f, 6f, 20f)
                lineTo(9f, 20f)
                lineTo(9f, 12f)
                lineTo(5f, 12f)
                lineTo(5f, 10f)
                curveTo(5f, 6.13f, 8.13f, 3f, 12f, 3f)
                curveTo(15.87f, 3f, 19f, 6.13f, 19f, 10f)
                lineTo(19f, 12f)
                lineTo(15f, 12f)
                lineTo(15f, 20f)
                lineTo(18f, 20f)
                curveTo(19.66f, 20f, 21f, 18.66f, 21f, 17f)
                lineTo(21f, 10f)
                curveTo(21f, 5.03f, 16.97f, 1f, 12f, 1f)
                close()
            }
        }.build()
    }

    val Speaker: ImageVector by lazy {
        ImageVector.Builder(name = "Speaker", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(3f, 9f)
                lineTo(3f, 15f)
                lineTo(7f, 15f)
                lineTo(12f, 20f)
                lineTo(12f, 4f)
                lineTo(7f, 9f)
                close()
                moveTo(16.5f, 12f)
                curveTo(16.5f, 10.23f, 15.48f, 8.71f, 14f, 7.97f)
                lineTo(14f, 16.02f)
                curveTo(15.48f, 15.29f, 16.5f, 13.77f, 16.5f, 12f)
                close()
                moveTo(14f, 3.23f)
                lineTo(14f, 5.29f)
                curveTo(16.89f, 6.15f, 19f, 8.83f, 19f, 12f)
                curveTo(19f, 15.17f, 16.89f, 17.85f, 14f, 18.71f)
                lineTo(14f, 20.77f)
                curveTo(18.01f, 19.86f, 21f, 16.28f, 21f, 12f)
                curveTo(21f, 7.72f, 18.01f, 4.14f, 14f, 3.23f)
                close()
            }
        }.build()
    }

    val Reply: ImageVector by lazy {
        ImageVector.Builder(name = "Reply", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(10f, 9f)
                lineTo(10f, 5f)
                lineTo(3f, 12f)
                lineTo(10f, 19f)
                lineTo(10f, 15.1f)
                curveTo(15f, 15.1f, 18.5f, 16.6f, 21f, 20.1f)
                curveTo(20f, 15.1f, 17f, 10.1f, 10f, 9.1f)
                close()
            }
        }.build()
    }

    val ContentCopy: ImageVector by lazy {
        ImageVector.Builder(name = "ContentCopy", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(16f, 1f)
                lineTo(4f, 1f)
                curveTo(2.9f, 1f, 2f, 1.9f, 2f, 3f)
                lineTo(2f, 17f)
                lineTo(4f, 17f)
                lineTo(4f, 3f)
                lineTo(16f, 3f)
                close()
                moveTo(19f, 5f)
                lineTo(8f, 5f)
                curveTo(6.9f, 5f, 6f, 5.9f, 6f, 7f)
                lineTo(6f, 21f)
                curveTo(6f, 22.1f, 6.9f, 23f, 8f, 23f)
                lineTo(19f, 23f)
                curveTo(20.1f, 23f, 21f, 22.1f, 21f, 21f)
                lineTo(21f, 7f)
                curveTo(21f, 5.9f, 20.1f, 5f, 19f, 5f)
                close()
                moveTo(19f, 21f)
                lineTo(8f, 21f)
                lineTo(8f, 7f)
                lineTo(19f, 7f)
                close()
            }
        }.build()
    }
}
