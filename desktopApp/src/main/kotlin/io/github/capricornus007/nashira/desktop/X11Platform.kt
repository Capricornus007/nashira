package io.github.capricornus007.nashira.desktop

import java.io.File

/**
 * X11 平台調校，**必須在 AWT/Compose 初始化之前呼叫**。
 *
 * 兩件事：
 *
 * 1. 非重親（non-reparenting）視窗管理器。bspwm / dwm / xmonad / i3 這類 WM 不會把
 *    應用視窗塞進自己的裝飾框（沒有 reparent），AWT 卻預設假設有框、自己算一組
 *    「假邊框」偏移。結果是 Frame 明明被 WM 平鋪成全螢幕，內容面板卻按假偏移貼在
 *    -5,-25 並維持 900x640 的初始大小——看起來就是「死趴在左上角的小窗，旁邊還露出
 *    別的視窗背景」。JDK 有開關 `_JAVA_AWT_WM_NONREPARENTING=1`，但那是環境變數，
 *    行程啟動後改不了（`XToolkit.getEnv` 是 native 讀 environ）。所以直接反射把
 *    `sun.awt.X11.XWM.awtWMNonReparenting` 設為 1，效果等價且不需要外部 wrapper。
 *    需要 `--add-opens java.desktop/sun.awt.X11=ALL-UNNAMED`（見 build.gradle.kts）。
 *
 * 2. HiDPI 縮放。JDK 在 Linux 只認 GDK/Xft 的一部分來源；此機 Xft.dpi=168（1.75x）
 *    但 AWT 仍給 1.0 的 transform，於是全 UI 以 1dp=1px 畫在 2240x1400 上，字小如蚊。
 *    這裡從 X resources 的 Xft.dpi（其次 GDK_SCALE）推出縮放並寫入
 *    `sun.java2d.uiScale`——必須在第一次觸碰 GraphicsEnvironment 前設定才生效。
 */
internal fun configureX11Platform() {
    if (System.getProperty("os.name")?.contains("linux", ignoreCase = true) != true) return
    if (System.getenv("DISPLAY") == null && System.getenv("WAYLAND_DISPLAY") != null) return

    applyUiScale()
    applyNonReparentingWm()
}

/**
 * 讀 Xft.dpi / GDK_SCALE 推縮放；使用者已明確設 uiScale 時不覆寫。
 *
 * **只能給整數**：JDK 的 X11 後端把 uiScale 取成 int，實測 1.5 / 1.75 一律退回 1.0，
 * 只有 1 / 2 / 3 生效。所以四捨五入到整數：1.75x 的螢幕用 2x
 *（2240x1400 → 1120x700 邏輯像素，字級才正常）。
 */
private fun applyUiScale() {
    if (System.getProperty("sun.java2d.uiScale") != null) return
    val scale = detectScale() ?: return
    if (scale <= 1) return
    System.setProperty("sun.java2d.uiScale.enabled", "true")
    System.setProperty("sun.java2d.uiScale", scale.toString())
}

private fun detectScale(): Int? {
    System.getenv("GDK_SCALE")?.toFloatOrNull()?.let { if (it > 0f) return Math.round(it) }
    val dpi = xftDpi() ?: return null
    // 96dpi = 1x；1.5x 以上進到 2x（JDK 不吃分數縮放）
    return Math.round(dpi / 96f).coerceIn(1, 4)
}

/** 先問 X server 的 resource database（xrdb -query），失敗再讀 ~/.Xresources。 */
private fun xftDpi(): Float? {
    runCatching {
        val process = ProcessBuilder("xrdb", "-query").redirectErrorStream(true).start()
        val output = process.inputStream.bufferedReader().use { it.readText() }
        process.waitFor()
        parseXftDpi(output)?.let { return it }
    }
    runCatching {
        val file = File(System.getProperty("user.home"), ".Xresources")
        if (file.isFile) parseXftDpi(file.readText())?.let { return it }
    }
    return null
}

private fun parseXftDpi(text: String): Float? =
    text.lineSequence()
        .firstOrNull { it.trim().startsWith("Xft.dpi:", ignoreCase = true) }
        ?.substringAfter(':')
        ?.trim()
        ?.toFloatOrNull()
        ?.takeIf { it > 0f }

/**
 * 反射設定 `XWM.awtWMNonReparenting = 1`。這條路徑只有在 AWT 尚未決定 WM 種類前
 * 設定才有意義，因此本函式必須在建任何視窗之前跑。失敗（未來 JDK 改名／未開 add-opens）
 * 就安靜跳過：最差退回舊行為，不會讓應用啟動失敗。
 */
private fun applyNonReparentingWm() {
    runCatching {
        val xwm = Class.forName("sun.awt.X11.XWM")
        val field = xwm.getDeclaredField("awtWMNonReparenting")
        field.isAccessible = true
        field.setInt(null, 1)
    }
}
