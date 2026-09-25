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
    /**
     * 輸入法／鍵盤圖示：貼圖面板開著時取代輸入列那顆笑臉（Telegram／64Gram 的行為，
     * 用戶 2026-09-25 點名）。material-icons-core 沒有 Keyboard，照本檔模式手繪。
     *
     * 只畫鍵位、不畫外殼：Icon 是單色 tint，外殼與鍵位若畫在同一條 path 需要 EvenOdd
     * 挖空，而向量 DSL 的接收者是 PathBuilder（只有 moveTo/lineTo/curveTo/close，
     * 沒有 addRect/fillType），所以改成「三排鍵位＋一條空白鍵」的實心画法——
     * 讀起來仍是鍵盤，且不會被 tint 塗成一整塊。
     */
    val Keyboard: ImageVector by lazy {
        ImageVector.Builder(
            name = "Keyboard",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                val keyW = 2.6f
                val keyH = 2.8f
                val xs = floatArrayOf(3f, 7f, 11f, 15f, 19f)
                for (y in floatArrayOf(5.6f, 9.6f)) {
                    for (x in xs) {
                        moveTo(x, y); lineTo(x + keyW, y)
                        lineTo(x + keyW, y + keyH); lineTo(x, y + keyH); close()
                    }
                }
                // 第三排：兩顆鍵 ＋ 一條長空白鍵
                for (x in floatArrayOf(3f, 7f)) {
                    moveTo(x, 13.6f); lineTo(x + keyW, 13.6f)
                    lineTo(x + keyW, 13.6f + keyH); lineTo(x, 13.6f + keyH); close()
                }
                moveTo(10.6f, 13.6f); lineTo(21.6f, 13.6f)
                lineTo(21.6f, 13.6f + keyH); lineTo(10.6f, 13.6f + keyH); close()
            }
        }.build()
    }

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
