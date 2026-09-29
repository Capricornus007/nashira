package io.github.capricornus007.nashira.desktop

import com.sun.jna.Pointer

/**
 * X11 層監看「系統托盤擁有者」（`_NET_SYSTEM_TRAY_S0` 這個 selection 的擁有者視窗）。
 *
 * 為什麼要自己問 X：AWT 的托盤只在 `SystemTray.add()` 那一刻找一次擁有者，之後
 * bar 被重建（`i3-msg restart`、`bar mode toggle`、換 WM）時，X 會把掛在舊 bar 下面的
 * 圖示視窗一併銷掉，而 **AWT 永遠不會重新嵌入**——使用者看到的就是一個活著的行程、
 * 卻完全沒有托盤圖示（用戶 2026-09-29 兩輪點名「後臺圖標不見」）。
 *
 * 這裡只提供兩個事實：現在誰擁有托盤、以及這個行程還開不開著 X 連線。
 * 判斷與重掛由呼叫端做。
 *
 * 註：`x11` 直接可為空，不要寫「實作全部方法的佔位物件」——那種佔位每加一個
 * X11Lib 方法就會漏一個而編譯不過（CI 踩過：`TrayOwnerWatch.kt:38 Class '<anonymous>'
 * is not abstract and does not implement abstract members`）。
 */
internal class TrayOwnerWatch {
    private val x11: X11Lib? = loadX11()
    private val display: Pointer? = x11?.let { runCatching { it.XOpenDisplay(null) }.getOrNull() }
    private val atom: Long = display?.let { d ->
        runCatching { x11?.XInternAtom(d, "_NET_SYSTEM_TRAY_S0", false) }.getOrDefault(0L)
    } ?: 0L

    /** 目前擁有托盤的視窗 id；0 = 沒人擁有，-1 = 拿不到（沒有 X 連線或載入 libX11 失敗）。 */
    fun owner(): Long {
        val lib = x11 ?: return -1L
        val d = display ?: return -1L
        if (atom == 0L) return -1L
        return runCatching { lib.XGetSelectionOwner(d, atom) }.getOrDefault(-1L)
    }

    fun close() {
        val lib = x11 ?: return
        display?.let { runCatching { lib.XCloseDisplay(it) } }
    }
}
