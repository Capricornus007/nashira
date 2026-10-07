package io.github.capricornus007.nashira

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * 右鍵選單的圖示。
 *
 * 為什麼另開一檔、而且全部手繪：`material-icons-core` 只有 40 顆，選單要用的
 * 連結／圖釘／封鎖／鈴鐺／加號笑脸一個都沒有（整包 extended 是 ~30MB，不值得引）。
 * 用戶 2026-10-07 的評語是「除了檢視原始碼跟轉寄之外的圖示沒一個對的」，
 * 而且「複製訊息連結不應該跟複製文字同一個圖標」——所以這檔把每一項都畫成
 * 對得上的形狀，路徑一律用標準 Material 24×24 座標系。
 *
 * 圓與圓環用四段三次貝茲近似（kappa = 0.5523），不用 `arcTo`：
 * DSL 的 `arcTo` 起點語意（從「目前點」開始掃）在畫整圓時很容易差半圈，
 * 近似公式的結果可控、也比較不會因為版本差異跑掉。
 */
object MenuIcons {
    private const val KAPPA = 0.5523f

    /**
     * 圓。`reverse` 決定繞行方向：NonZero 填色下「外圓正向＋內圓反向」才會出現洞，
     * 而這版 DSL 的 `path {}` **沒有 `fillType` 參數**（`PathBuilder` 也沒有 `fillType` 屬性），
     * 想畫圓環只能靠繞行方向，不能靠 EvenOdd。
     */
    private fun PathBuilder.circle(cx: Float, cy: Float, r: Float, reverse: Boolean = false) {
        val k = KAPPA * r
        moveTo(cx - r, cy)
        if (reverse) {
            curveTo(cx - r, cy + k, cx - k, cy + r, cx, cy + r)
            curveTo(cx + k, cy + r, cx + r, cy + k, cx + r, cy)
            curveTo(cx + r, cy - k, cx + k, cy - r, cx, cy - r)
            curveTo(cx - k, cy - r, cx - r, cy - k, cx - r, cy)
        } else {
            curveTo(cx - r, cy - k, cx - k, cy - r, cx, cy - r)
            curveTo(cx + k, cy - r, cx + r, cy - k, cx + r, cy)
            curveTo(cx + r, cy + k, cx + k, cy + r, cx, cy + r)
            curveTo(cx - k, cy + r, cx - r, cy + k, cx - r, cy)
        }
        close()
    }

    /** 一條有寬度的斜槓（封鎖、鈴鐺斜線共用）。 */
    private fun PathBuilder.bar(x1: Float, y1: Float, x2: Float, y2: Float, thickness: Float) {
        val dx = x2 - x1
        val dy = y2 - y1
        val len = kotlin.math.sqrt(dx * dx + dy * dy)
        if (len <= 0f) return
        val ox = (dy / len) * thickness / 2f
        val oy = (-dx / len) * thickness / 2f
        moveTo(x1 + ox, y1 + oy)
        lineTo(x2 + ox, y2 + oy)
        lineTo(x2 - ox, y2 - oy)
        lineTo(x1 - ox, y1 - oy)
        close()
    }

    private fun icon(name: String, block: ImageVector.Builder.() -> Unit): ImageVector =
        ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply(block).build()

    private val black get() = SolidColor(Color.Black)

    /** 連結（複製連結用）。標準 Material `link` 的兩段鉤＋中間一條槓。 */
    val Link: ImageVector by lazy {
        icon("Link") {
            path(fill = black) {
                moveTo(3.9f, 12f)
                curveTo(3.9f, 10.29f, 5.29f, 8.9f, 7f, 8.9f)
                lineTo(11f, 8.9f); lineTo(11f, 7f); lineTo(7f, 7f)
                curveTo(4.24f, 7f, 2f, 9.24f, 2f, 12f)
                curveTo(2f, 14.76f, 4.24f, 17f, 7f, 17f)
                lineTo(11f, 17f); lineTo(11f, 15.1f); lineTo(7f, 15.1f)
                curveTo(5.29f, 15.1f, 3.9f, 13.71f, 3.9f, 12f)
                close()
                moveTo(8f, 11f); lineTo(16f, 11f); lineTo(16f, 13f); lineTo(8f, 13f); close()
                moveTo(17f, 7f); lineTo(13f, 7f); lineTo(13f, 8.9f); lineTo(17f, 8.9f)
                curveTo(18.71f, 8.9f, 20.1f, 10.29f, 20.1f, 12f)
                curveTo(20.1f, 13.71f, 18.71f, 15.1f, 17f, 15.1f)
                lineTo(13f, 15.1f); lineTo(13f, 17f); lineTo(17f, 17f)
                curveTo(19.76f, 17f, 22f, 14.76f, 22f, 12f)
                curveTo(22f, 9.24f, 19.76f, 7f, 17f, 7f)
                close()
            }
        }
    }

    /** 圖釘（釘選訊息）。圓形釘帽＋一根斜下來的釘子。 */
    val PushPin: ImageVector by lazy {
        icon("PushPin") {
            path(fill = black) {
                moveTo(12f, 2.5f)
                curveTo(15f, 2.5f, 17.4f, 4.9f, 17.4f, 7.9f)
                curveTo(17.4f, 10.2f, 16f, 12.1f, 14f, 12.8f)
                lineTo(13.4f, 12.8f); lineTo(13.4f, 19.5f)
                lineTo(10.6f, 19.5f); lineTo(10.6f, 12.8f); lineTo(10f, 12.8f)
                curveTo(8f, 12.1f, 6.6f, 10.2f, 6.6f, 7.9f)
                curveTo(6.6f, 4.9f, 9f, 2.5f, 12f, 2.5f)
                close()
            }
            path(fill = black) {
                moveTo(9.4f, 20f); lineTo(14.6f, 20f); lineTo(14.6f, 22.2f); lineTo(9.4f, 22.2f); close()
            }
        }
    }

    /** 封鎖用戶：圓環＋一條 45° 斜槓。 */
    val Block: ImageVector by lazy {
        icon("Block") {
            path(fill = black) {
                circle(12f, 12f, 9f)
                circle(12f, 12f, 6.4f, reverse = true)
            }
            path(fill = black) { bar(6.2f, 6.2f, 17.8f, 17.8f, 2.6f) }
        }
    }

    /** 標記為未讀：一個空心圓圈（已讀那項用打勾）。 */
    val MarkUnread: ImageVector by lazy {
        icon("MarkUnread") {
            path(fill = black) {
                circle(12f, 12f, 8.6f)
                circle(12f, 12f, 5.8f, reverse = true)
            }
        }
    }

    /** 新增反應：圓圈裡一個加號（笑脸在 core 裡只有 `Face` 那顆，畫起來不像「加反應」）。 */
    val AddReaction: ImageVector by lazy {
        icon("AddReaction") {
            path(fill = black) {
                circle(12f, 12f, 9f)
                circle(12f, 12f, 6.6f, reverse = true)
            }
            path(fill = black) {
                moveTo(8.6f, 10.9f); lineTo(15.4f, 10.9f); lineTo(15.4f, 13.1f); lineTo(8.6f, 13.1f); close()
                moveTo(10.9f, 8.6f); lineTo(13.1f, 8.6f); lineTo(13.1f, 15.4f); lineTo(10.9f, 15.4f); close()
            }
        }
    }

    /** 鈴鐺（取消靜音）。 */
    val Bell: ImageVector by lazy {
        icon("Bell") {
            path(fill = black) {
                moveTo(12f, 3f)
                curveTo(14.9f, 3f, 16.8f, 5.2f, 16.8f, 8.6f)
                lineTo(16.8f, 13.2f); lineTo(18.4f, 15.8f); lineTo(5.6f, 15.8f); lineTo(7.2f, 13.2f)
                lineTo(7.2f, 8.6f)
                curveTo(7.2f, 5.2f, 9.1f, 3f, 12f, 3f)
                close()
            }
            path(fill = black) { circle(12f, 18.4f, 2f) }
        }
    }

    /** 鈴鐺＋斜（靜音通知）。 */
    val BellOff: ImageVector by lazy {
        icon("BellOff") {
            path(fill = black) {
                moveTo(12f, 3f)
                curveTo(14.9f, 3f, 16.8f, 5.2f, 16.8f, 8.6f)
                lineTo(16.8f, 13.2f); lineTo(18.4f, 15.8f); lineTo(5.6f, 15.8f); lineTo(7.2f, 13.2f)
                lineTo(7.2f, 8.6f)
                curveTo(7.2f, 5.2f, 9.1f, 3f, 12f, 3f)
                close()
            }
            path(fill = black) { circle(12f, 18.4f, 2f) }
            path(fill = black) { bar(4.6f, 3.4f, 19.4f, 20.6f, 2.4f) }
        }
    }

    /** 邀請：人＋一個加號（core 的 `Person` 只有人頭，讀不出「邀請」這個動作）。 */
    val PersonAdd: ImageVector by lazy {
        icon("PersonAdd") {
            path(fill = black) { circle(10f, 8f, 3.6f) }
            path(fill = black) {
                moveTo(3.4f, 20f)
                curveTo(3.4f, 16.1f, 6.3f, 13.9f, 10f, 13.9f)
                curveTo(11.6f, 13.9f, 13.1f, 14.3f, 14.3f, 15.1f)
                lineTo(14.3f, 20f); close()
            }
            path(fill = black) {
                moveTo(16.4f, 8.7f); lineTo(23.6f, 8.7f); lineTo(23.6f, 10.9f); lineTo(16.4f, 10.9f); close()
                moveTo(19.1f, 6f); lineTo(20.9f, 6f); lineTo(20.9f, 13.6f); lineTo(19.1f, 13.6f); close()
            }
        }
    }
}
