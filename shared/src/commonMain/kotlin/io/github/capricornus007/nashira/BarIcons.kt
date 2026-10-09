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
    /**
     * 下載（圖片訊息選單用）。material-icons-core 沒有 Download／FileDownload，
     * 照本檔模式手繪標準 Material `file_download` 輪廓（一條底線＋一支插進去的箭頭）。
     */
    val Download: ImageVector by lazy {
        ImageVector.Builder(name = "Download", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(5f, 20f); lineTo(19f, 20f); lineTo(19f, 18f); lineTo(5f, 18f); close()
                moveTo(19f, 9f); lineTo(15f, 9f); lineTo(15f, 3f); lineTo(9f, 3f)
                lineTo(9f, 9f); lineTo(5f, 9f); lineTo(12f, 16f); lineTo(19f, 9f); close()
            }
        }.build()
    }

    /**
     * 旋轉（全螢幕檢視器右下那顆）。core 集裡最接近的是 `Refresh`，但那個語意是
     * 「重新整理」，擺在圖片上會讓人以為按了要重抓——照 Element 的 ↺ 手繪。
     *
     * 這版本的 `path` DSL 不收 `stroke`（編譯器把它當成 `fill: Brush?`），
     * 所以環是用「外弧過去＋內弧回填」的填色畫法做的，不是描邊。
     */
    val Rotate: ImageVector by lazy {
        ImageVector.Builder(name = "Rotate", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(fill = SolidColor(Color.Black)) {
                // 外緣：北 → 東 → 南 → 西（缺左上四分之一，那一端放箭頭）
                moveTo(12f, 3f)
                curveTo(16.97f, 3f, 21f, 7.03f, 21f, 12f)
                curveTo(21f, 16.97f, 16.97f, 21f, 12f, 21f)
                curveTo(7.03f, 21f, 3f, 16.97f, 3f, 12f)
                // 內緣回填
                lineTo(5.5f, 12f)
                curveTo(5.5f, 15.59f, 8.41f, 18.5f, 12f, 18.5f)
                curveTo(15.59f, 18.5f, 18.5f, 15.59f, 18.5f, 12f)
                curveTo(18.5f, 8.41f, 15.59f, 5.5f, 12f, 5.5f)
                close()
                // 箭頭：在北端往左指，讀起來才是「逆時針轉」
                moveTo(7.5f, 4.25f)
                lineTo(12f, 1.5f)
                lineTo(12f, 7f)
                close()
            }
        }.build()
    }

    /**
     * 開著的鎖＝這房間**沒有**端到端加密。用戶 2026-10-09 明確要兩個狀態都畫出來
     *（「有無可能我是要你弄个鎖上的跟沒鎖上的分別代表？」）。
     * 先講清楚代價：Element／Telegram 只標加密的那一邊（不標＝沒加密），
     * 兩個都標會讓清單多出一把灰鎖；這裡照他要的做，但把「沒鎖」畫得比較淡，
     * 讓「有鎖」仍然是掃視時先看到的那個。
     */
    val LockOpen: ImageVector by lazy {
        ImageVector.Builder(name = "LockOpen", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(fill = SolidColor(Color.Black)) {
                // 鎖身
                moveTo(4.5f, 10.5f); lineTo(19.5f, 10.5f); lineTo(19.5f, 21f); lineTo(4.5f, 21f); close()
                // 弓：只從左側起、往右上開，右邊不閉合（閉合就是 LockOpen 變 Lock）
                moveTo(7f, 10.5f); lineTo(4.6f, 10.5f); lineTo(4.6f, 7f)
                curveTo(4.6f, 3.6f, 7.2f, 1.2f, 10.4f, 1.4f)
                curveTo(13.2f, 1.6f, 15.2f, 3.8f, 15.2f, 6.6f)
                lineTo(15.2f, 8f); lineTo(12.9f, 8f); lineTo(12.9f, 6.6f)
                curveTo(12.9f, 4.9f, 11.7f, 3.7f, 10.2f, 3.7f)
                curveTo(8.5f, 3.7f, 7f, 5f, 7f, 7f); close()
            }
        }.build()
    }

    /** 附件面板／「檢視所有照片」：2x2 的格子，Element 與 Telegram 都是這個語彙。 */
    val Gallery: ImageVector by lazy {
        ImageVector.Builder(name = "Gallery", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(3f, 3f); lineTo(10.5f, 3f); lineTo(10.5f, 10.5f); lineTo(3f, 10.5f); close()
                moveTo(13.5f, 3f); lineTo(21f, 3f); lineTo(21f, 10.5f); lineTo(13.5f, 10.5f); close()
                moveTo(3f, 13.5f); lineTo(10.5f, 13.5f); lineTo(10.5f, 21f); lineTo(3f, 21f); close()
                moveTo(13.5f, 13.5f); lineTo(21f, 13.5f); lineTo(21f, 21f); lineTo(13.5f, 21f); close()
            }
        }.build()
    }

    /**
     * 置頂／置底。用戶 2026-10-07 看了第一版直接問「你這圖標確定正常嗎」——
     * 那版用 `KeyboardArrowUp/Down`，在選單裡就是兩顆孤零零的 `^` `v`，
     * 讀不出「釘到頂部」的意思。改成自畫的**「靠邊一條槓＋一支箭頭插過去」**，
     * 這才是各家（Telegram／Discord）置頂置底的共同語彙。
     */
    val PinTop: ImageVector by lazy {
        ImageVector.Builder(name = "PinTop", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(5f, 3f); lineTo(19f, 3f); lineTo(19f, 5f); lineTo(5f, 5f); close()
                moveTo(12f, 6f); lineTo(17f, 12f); lineTo(7f, 12f); close()
                moveTo(11f, 12f); lineTo(13f, 12f); lineTo(13f, 21f); lineTo(11f, 21f); close()
            }
        }.build()
    }

    /** [PinTop] 的上下鏡像（y' = 24 - y）。 */
    val PinBottom: ImageVector by lazy {
        ImageVector.Builder(name = "PinBottom", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(5f, 19f); lineTo(19f, 19f); lineTo(19f, 21f); lineTo(5f, 21f); close()
                moveTo(12f, 18f); lineTo(7f, 12f); lineTo(17f, 12f); close()
                moveTo(11f, 12f); lineTo(13f, 12f); lineTo(13f, 3f); lineTo(11f, 3f); close()
            }
        }.build()
    }

    /** 「原始碼」那列用的 `</>`（原本是「人頭」＝帳號，用戶 2026-09-29 點名不對）。 */
    val Code: ImageVector by lazy {
        ImageVector.Builder(
            name = "Code", defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(9.4f, 16.6f); lineTo(4.8f, 12f); lineTo(9.4f, 7.4f)
                lineTo(8f, 6f); lineTo(2f, 12f); lineTo(8f, 18f); close()
                moveTo(14.6f, 16.6f); lineTo(19.2f, 12f); lineTo(14.6f, 7.4f)
                lineTo(16f, 6f); lineTo(22f, 12f); lineTo(16f, 18f); close()
            }
        }.build()
    }

    /** 「外觀」用調色盤意象的亮度圖示（原本那顆星星語意是「收藏」）。 */
    val Appearance: ImageVector by lazy {
        ImageVector.Builder(
            name = "Appearance", defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color.Black)) { circle(12f, 12f, 4.4f) }
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.9f) {
                for (i in 0 until 8) {
                    val angle = Math.PI / 4.0 * i
                    val dx = kotlin.math.cos(angle).toFloat()
                    val dy = kotlin.math.sin(angle).toFloat()
                    moveTo(12f + dx * 7f, 12f + dy * 7f)
                    lineTo(12f + dx * 9.4f, 12f + dy * 9.4f)
                }
            }
        }.build()
    }

    /** 「語言」用的地球圖示（原本那顆鉛筆是「編輯」）。 */
    val Language: ImageVector by lazy {
        ImageVector.Builder(
            name = "Language", defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.7f) {
                circle(12f, 12f, 9f)
                moveTo(3f, 12f); lineTo(21f, 12f)
                moveTo(12f, 3f)
                curveTo(15.4f, 6.6f, 15.4f, 17.4f, 12f, 21f)
                curveTo(8.6f, 17.4f, 8.6f, 6.6f, 12f, 3f)
                close()
                moveTo(4.6f, 7.4f); lineTo(19.4f, 7.4f)
                moveTo(4.6f, 16.6f); lineTo(19.4f, 16.6f)
            }
        }.build()
    }

    /** 「純黑（AMOLED）」那列的對比圖示（這列原本左邊空著，看起來像漏圖）。 */
    val Contrast: ImageVector by lazy {
        ImageVector.Builder(
            name = "Contrast", defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f) { circle(12f, 12f, 9f) }
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 3f)
                curveTo(16.97f, 3f, 21f, 7.03f, 21f, 12f)
                curveTo(21f, 16.97f, 16.97f, 21f, 12f, 21f)
                close()
            }
        }.build()
    }
    /**
     * 「ID 徽章」：圓角牌＋裡面的 I 與 D。
     * 給「複製 Matrix ID」用——那顆通用「兩張紙」的複製圖示在Discord對照下被點名
     * （用戶 2026-09-29：「discord 那邊的圖標好像比起單純的複製圖標更合適」），
     * 因為這裡複製的是**身份識別碼**，不是任意內容。
     */
    val IdBadge: ImageVector by lazy {
        ImageVector.Builder(
            name = "IdBadge", defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.7f) {
                roundRect(2.6f, 5.2f, 21.4f, 18.8f, 3f)
            }
            // I
            path(fill = SolidColor(Color.Black)) { rect(6.4f, 9.4f, 7.9f, 14.6f) }
            // D 的豎棒
            path(fill = SolidColor(Color.Black)) { rect(10.4f, 9.4f, 11.9f, 14.6f) }
            // D 的右半圓：從上緣畫到下緣
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.7f) {
                moveTo(11.9f, 9.4f)
                lineTo(14.2f, 9.4f)
                curveTo(17.4f, 9.4f, 17.4f, 14.6f, 14.2f, 14.6f)
                lineTo(11.9f, 14.6f)
            }
        }.build()
    }
}

/** 圓角矩形路徑（四角各一段貝塞爾，k 與 [circle] 同一個近似常數）。 */
private fun androidx.compose.ui.graphics.vector.PathBuilder.roundRect(
    left: Float, top: Float, right: Float, bottom: Float, r: Float,
) {
    val k = r * 0.5523f
    moveTo(left + r, top)
    lineTo(right - r, top)
    curveTo(right - r + k, top, right, top + r - k, right, top + r)
    lineTo(right, bottom - r)
    curveTo(right, bottom - r + k, right - r + k, bottom, right - r, bottom)
    lineTo(left + r, bottom)
    curveTo(left + r - k, bottom, left, bottom - r + k, left, bottom - r)
    lineTo(left, top + r)
    curveTo(left, top + r - k, left + r - k, top, left + r, top)
    close()
}

/** 實心矩形（PathBuilder 沒有 addRect）。 */
private fun androidx.compose.ui.graphics.vector.PathBuilder.rect(
    left: Float, top: Float, right: Float, bottom: Float,
) {
    moveTo(left, top)
    lineTo(right, top)
    lineTo(right, bottom)
    lineTo(left, bottom)
    close()
}
/** 用四段貝塞爾近似一個正圓（PathBuilder 沒有 addCircle，本檔所有圖示共用這個）。 */
private fun androidx.compose.ui.graphics.vector.PathBuilder.circle(cx: Float, cy: Float, r: Float) {
    val k = r * 0.5523f
    moveTo(cx, cy - r)
    curveTo(cx + k, cy - r, cx + r, cy - k, cx + r, cy)
    curveTo(cx + r, cy + k, cx + k, cy + r, cx, cy + r)
    curveTo(cx - k, cy + r, cx - r, cy + k, cx - r, cy)
    curveTo(cx - r, cy - k, cx - k, cy - r, cx, cy - r)
    close()
}
