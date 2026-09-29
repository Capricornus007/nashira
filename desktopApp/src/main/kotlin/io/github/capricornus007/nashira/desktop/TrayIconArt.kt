package io.github.capricornus007.nashira.desktop

import java.awt.RenderingHints
import java.awt.image.BufferedImage

/** 與背景平均色差多少算「內容」（nashira-icon.png 實測：90~180 都框到同一個範圍）。 */
private const val GlyphDiffThreshold = 120

/**
 * 出「實體像素」尺寸的托盤圖（原生托盤用，見 X11TrayIcon）。
 *
 * 兩步：先盒式平均把 512 的原圖收到 2 倍目標，再 2×2 平均到目標尺寸。
 * 一次到位的雙線性會把細edge 抹平；超取樣再降會保留得多一點。
 * 格子不是正方形時（理論上不會發生，i3bar 給的是正方形）把正方形置中，
 * 多出來的邊用就近邊緣填同一個色，至少不會變成透明的白線。
 */
internal fun trayArgb(src: BufferedImage, width: Int, height: Int): IntArray? {
    if (width <= 0 || height <= 0) return null
    val square = cropToGlyph(src)
    val side = minOf(width, height)
    val sample = side * 2
    val big = BufferedImage(sample, sample, BufferedImage.TYPE_INT_ARGB)
    val g = big.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.drawImage(square, 0, 0, sample, sample, null)
    g.dispose()

    val out = IntArray(width * height)
    val src2 = big.getRGB(0, 0, sample, sample, null, 0, sample)
    val squareArgb = IntArray(side * side)
    for (sy in 0 until side) {
        for (sx in 0 until side) {
            var r = 0
            var g2 = 0
            var b = 0
            for (dy in 0 until 2) {
                for (dx in 0 until 2) {
                    val value = src2[(sy * 2 + dy) * sample + sx * 2 + dx]
                    r += (value shr 16) and 0xFF
                    g2 += (value shr 8) and 0xFF
                    b += value and 0xFF
                }
            }
            squareArgb[sy * side + sx] = ((r / 4) shl 16) or ((g2 / 4) shl 8) or (b / 4)
        }
    }
    val left = (width - side) / 2
    val top = (height - side) / 2
    for (y in 0 until height) {
        for (x in 0 until width) {
            val sx = (x - left).coerceIn(0, side - 1)
            val sy = (y - top).coerceIn(0, side - 1)
            out[y * width + x] = squareArgb[sy * side + sx] and 0xFFFFFF
        }
    }
    return out
}

/**
 * AWT 托盤那條退路用的出圖：先裁內容再逐次減半縮到「使用者像素」尺寸的方塊。
 *
 * 為什麼要裁：nashira-icon.png 是 512 的方塊，但裡面那顆 N 與星只佔中間一小塊
 * （實測 bbox 140,110..380,380）。原樣縮進格子，圖案只佔中間幾像素，旁邊的
 * 微信／fcitx5 圖案都是填滿自己的方塊——所以別人看起來「正」、我們看起來
 * 「一顆小東西浮在深色塊裡」（用戶 2026-09-29 連點數輪）。
 * 另外不能留透明邊：AWT 那塊托盤畫布是 TYPE_INT_RGB（不透明），透明邊會畫成白線。
 */
internal fun awtTrayImage(src: BufferedImage, side: Int): BufferedImage {
    var cur = cropToGlyph(src)
    while (cur.width > side) {
        val nextSide = (cur.width / 2).coerceAtLeast(side)
        val next = BufferedImage(nextSide, nextSide, BufferedImage.TYPE_INT_ARGB)
        val g = next.createGraphics()
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g.drawImage(cur, 0, 0, nextSide, nextSide, null)
        g.dispose()
        cur = next
    }
    return cur
}

/** 量出內容（與背景差很多的像素）的外框，以它為中心裁一個正方形；量不到就回原圖。 */
private fun cropToGlyph(src: BufferedImage): BufferedImage {
    val w = src.width
    val h = src.height
    val step = (w / 128).coerceAtLeast(1)
    var br = 0L
    var bg = 0L
    var bb = 0L
    var n = 0L
    for (i in 0 until w step step) {
        for (y in intArrayOf(0, h - 1)) {
            val rgb = src.getRGB(i, y)
            br += (rgb shr 16) and 0xFF
            bg += (rgb shr 8) and 0xFF
            bb += rgb and 0xFF
            n++
        }
        for (x in intArrayOf(0, w - 1)) {
            val rgb = src.getRGB(x, i)
            br += (rgb shr 16) and 0xFF
            bg += (rgb shr 8) and 0xFF
            bb += rgb and 0xFF
            n++
        }
    }
    if (n == 0L) return src
    val r0 = (br / n).toInt()
    val g0 = (bg / n).toInt()
    val b0 = (bb / n).toInt()
    var minX = w
    var minY = h
    var maxX = -1
    var maxY = -1
    var y = 0
    while (y < h) {
        var x = 0
        while (x < w) {
            val rgb = src.getRGB(x, y)
            val diff = kotlin.math.abs(((rgb shr 16) and 0xFF) - r0) +
                kotlin.math.abs(((rgb shr 8) and 0xFF) - g0) +
                kotlin.math.abs((rgb and 0xFF) - b0)
            if (diff > GlyphDiffThreshold) {
                if (x < minX) minX = x
                if (x > maxX) maxX = x
                if (y < minY) minY = y
                if (y > maxY) maxY = y
            }
            x += step
        }
        y += step
    }
    if (maxX < minX || maxY < minY) return src
    // 內容外框再放 25% 的呼吸空間，取正方形並夾在圖內
    val content = maxOf(maxX - minX, maxY - minY)
    val crop = (content * 1.25f).toInt().coerceIn(content + 8, minOf(w, h))
    val cx = (minX + maxX) / 2
    val cy = (minY + maxY) / 2
    val x0 = (cx - crop / 2).coerceIn(0, w - crop)
    val y0 = (cy - crop / 2).coerceIn(0, h - crop)
    return src.getSubimage(x0, y0, crop, crop)
}
