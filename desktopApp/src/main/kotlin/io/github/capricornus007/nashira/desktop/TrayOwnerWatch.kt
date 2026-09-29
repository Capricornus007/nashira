package io.github.capricornus007.nashira.desktop

import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.LongByReference
import com.sun.jna.ptr.PointerByReference

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
 */
internal class TrayOwnerWatch {
    private val x11: X11Lib = loadX11() ?: unavailable
    private val display: Pointer? = runCatching { x11.XOpenDisplay(null) }.getOrNull()
    private val atom: Long = display?.let { runCatching { x11.XInternAtom(it, "_NET_SYSTEM_TRAY_S0", false) }.getOrDefault(0L) }
        ?: 0L

    /** 目前擁有托盤的視窗 id；0 = 沒人擁有，-1 = 拿不到（沒有 X 連線或載入 libX11 失敗）。 */
    fun owner(): Long {
        val d = display ?: return -1L
        if (atom == 0L) return -1L
        return runCatching { x11.XGetSelectionOwner(d, atom) }.getOrDefault(-1L)
    }

    fun close() {
        display?.let { runCatching { x11.XCloseDisplay(it) } }
    }

    private companion object {
        /** 載不到 libX11 時的佔位：所有查詢都會回 -1，呼叫端據此放弃自癒、不影響主流程。 */
        val unavailable = object : X11Lib {
            override fun XOpenDisplay(name: String?): Pointer? = null
            override fun XCloseDisplay(display: Pointer?): Int = 0
            override fun XInternAtom(display: Pointer?, name: String, onlyIfExists: Boolean): Long = 0
            override fun XGetSelectionOwner(display: Pointer?, selection: Long): Long = 0
            override fun XQueryTree(
                display: Pointer?,
                window: Long,
                rootReturn: LongByReference?,
                parentReturn: LongByReference?,
                childrenReturn: PointerByReference?,
                nChildrenReturn: IntByReference?,
            ): Int = 0

            override fun XGetGeometry(
                display: Pointer?,
                drawable: Long,
                rootReturn: LongByReference?,
                xReturn: IntByReference?,
                yReturn: IntByReference?,
                widthReturn: IntByReference?,
                heightReturn: IntByReference?,
                borderWidthReturn: IntByReference?,
                depthReturn: IntByReference?,
            ): Int = 0

            override fun XGetClassHint(display: Pointer?, window: Long, hint: X11ClassHint): Int = 0
            override fun XFree(data: Pointer?): Int = 0
            override fun XDefaultRootWindow(display: Pointer?): Long = 0

            override fun XQueryPointer(
                display: Pointer?,
                window: Long,
                rootReturn: LongByReference?,
                childReturn: LongByReference?,
                rootXReturn: IntByReference?,
                rootYReturn: IntByReference?,
                winXReturn: IntByReference?,
                winYReturn: IntByReference?,
                maskReturn: IntByReference?,
            ): Int = 0
        }
    }
}
