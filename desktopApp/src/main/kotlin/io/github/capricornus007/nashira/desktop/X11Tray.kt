package io.github.capricornus007.nashira.desktop

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.NativeLong
import com.sun.jna.Pointer
import com.sun.jna.Structure
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.LongByReference
import com.sun.jna.ptr.PointerByReference

/**
 * libX11 的最小綁定：只宣告真的用得到的幾個呼叫。
 * JNA-platform 裡沒有 `XGetSelectionOwner` 這類函式，所以自己開一個 interface。
 */
internal interface X11Lib : Library {
    fun XOpenDisplay(name: String?): Pointer?
    fun XCloseDisplay(display: Pointer?): Int
    fun XInternAtom(display: Pointer?, name: String, onlyIfExists: Boolean): Long
    fun XGetSelectionOwner(display: Pointer?, selection: Long): Long
    fun XQueryTree(
        display: Pointer?,
        window: Long,
        rootReturn: LongByReference?,
        parentReturn: LongByReference?,
        childrenReturn: PointerByReference?,
        nChildrenReturn: IntByReference?,
    ): Int

    fun XGetGeometry(
        display: Pointer?,
        drawable: Long,
        rootReturn: LongByReference?,
        xReturn: IntByReference?,
        yReturn: IntByReference?,
        widthReturn: IntByReference?,
        heightReturn: IntByReference?,
        borderWidthReturn: IntByReference?,
        depthReturn: IntByReference?,
    ): Int

    fun XGetClassHint(display: Pointer?, window: Long, hint: X11ClassHint): Int
    fun XFree(data: Pointer?): Int
    fun XDefaultRootWindow(display: Pointer?): Long

    fun XQueryPointer(
        display: Pointer?,
        window: Long,
        rootReturn: LongByReference?,
        childReturn: LongByReference?,
        rootXReturn: IntByReference?,
        rootYReturn: IntByReference?,
        winXReturn: IntByReference?,
        winYReturn: IntByReference?,
        maskReturn: IntByReference?,
    ): Int

    // ── 自己畫托盤要用的那幾條（見 X11TrayIcon.kt）──────────────────────────
    fun XDefaultScreen(display: Pointer?): Int
    fun XDefaultDepth(display: Pointer?, screen: Int): Int
    fun XDefaultVisual(display: Pointer?, screen: Int): Pointer?
    fun XRootWindow(display: Pointer?, screen: Int): Long
    fun XVisualIDFromVisual(visual: Pointer?): Long
    fun XGetVisualInfo(display: Pointer?, vinfoMask: NativeLong, template: Pointer?, nitemsReturn: IntByReference?): Pointer?
    fun XCreateSimpleWindow(
        display: Pointer?,
        parent: Long,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        borderWidth: Int,
        border: NativeLong,
        background: NativeLong,
    ): Long

    fun XSelectInput(display: Pointer?, window: Long, events: NativeLong): Int
    fun XStoreName(display: Pointer?, window: Long, name: String): Int
    fun XChangeWindowAttributes(display: Pointer?, window: Long, valuemask: NativeLong, attrs: Pointer?): Int
    fun XChangeProperty(
        display: Pointer?,
        window: Long,
        property: Long,
        type: Long,
        format: Int,
        mode: Int,
        data: Pointer?,
        nelements: Int,
    ): Int

    fun XMapWindow(display: Pointer?, window: Long): Int
    fun XUnmapWindow(display: Pointer?, window: Long): Int
    fun XSendEvent(display: Pointer?, window: Long, propagate: Int, mask: NativeLong, event: Pointer?): Int
    fun XFlush(display: Pointer?): Int
    fun XPending(display: Pointer?): Int
    fun XNextEvent(display: Pointer?, event: Pointer?): Int
    fun XCreateGC(display: Pointer?, drawable: Long, valuemask: NativeLong, attrs: Pointer?): Pointer?
    fun XCreateImage(
        display: Pointer?,
        visual: Pointer?,
        depth: Int,
        format: Int,
        offset: Int,
        data: Pointer?,
        width: Int,
        height: Int,
        bitmapPad: Int,
        bytesPerLine: Int,
    ): Pointer?

    /** 注意：XPutImage 沒有 plane_mask 參數（那是 XCopyPlane 的），多宣告一個會把呼叫弄歪。 */
    fun XPutImage(
        display: Pointer?,
        drawable: Long,
        gc: Pointer?,
        image: Pointer?,
        srcX: Int,
        srcY: Int,
        destX: Int,
        destY: Int,
        width: Int,
        height: Int,
    ): Int
}

/** X11 的 `XClassHint`：兩個 C 字串，JNA 要按順序才知道哪欄是哪欄。 */
internal class X11ClassHint : Structure() {
    @JvmField
    var res_name: Pointer? = null

    @JvmField
    var res_class: Pointer? = null

    override fun getFieldOrder(): List<String> = listOf("res_name", "res_class")
}

internal fun loadX11(): X11Lib? = runCatching { Native.load("X11", X11Lib::class.java) }.getOrNull()

internal fun x11Children(x11: X11Lib, display: Pointer?, window: Long): List<Long> {
    val root = LongByReference()
    val parent = LongByReference()
    val children = PointerByReference()
    val count = IntByReference()
    if (x11.XQueryTree(display, window, root, parent, children, count) == 0) return emptyList()
    val pointer = children.value ?: return emptyList()
    val n = count.value.coerceIn(0, 256)
    val ids = try {
        pointer.getLongArray(0, n)
    } finally {
        x11.XFree(pointer)
    }
    return ids.toList()
}

/**
 * 問 X：「我們這顆托盤圖示的格子實際多大」（**實體像素**）。
 *
 * 為什麼不問 AWT：`TrayIcon.getSize()` 回報的是 AWT 自己假設的 24x24（使用者空間），
 * 跟 i3bar 真的給那一格（本機實測 33x33 實體）完全是兩碼事。照 AWT 的數字出圖，
 * 圖會被放大兩倍再從左上角裁掉，看見的就是圖標「偏到右下角」——用戶 2026-09-29 點的
 * 正是這個。64Gram／fcitx5／微信不會錯位，因為它們是直接照那一格的實體尺寸畫的。
 *
 * 找法：從 root 往下掃，認 WM_CLASS 是我们的、邊長落在圖示範圍（8..96）的那顆。
 * 不走「托盤 selection 擁有者的子窗口」：i3bar 會把 socket 再嵌到 bar 窗口底下，
 * 擁有者自己的子窗口清單是空的（2026-09-29 實測掃不到，所以才改成從 root 掃）。
 * 問不到回 null，呼叫端維持舊行為，不會因為探測失敗把圖示弄不見。
 */
internal fun traySlotSizePx(): Int? = runCatching {
    val x11 = loadX11() ?: return@runCatching null
    val display = x11.XOpenDisplay(null) ?: return@runCatching null
    try {
        val queue = ArrayDeque(listOf(x11.XDefaultRootWindow(display)))
        var visited = 0
        while (queue.isNotEmpty() && visited < 4000) {
            val window = queue.removeFirst()
            visited++
            queue.addAll(x11Children(x11, display, window))
            val hint = X11ClassHint()
            if (x11.XGetClassHint(display, window, hint) == 0) continue
            val klass = hint.res_class?.getString(0) ?: continue
            if (!klass.startsWith("io-github-capricornus007-nashira")) continue
            val width = IntByReference()
            val height = IntByReference()
            if (x11.XGetGeometry(display, window, LongByReference(), IntByReference(), IntByReference(), width, height, IntByReference(), IntByReference()) == 0) {
                continue
            }
            val side = minOf(width.value, height.value)
            if (side in 8..96) return@runCatching side
        }
        null
    } finally {
        x11.XCloseDisplay(display)
    }
}.getOrNull()

/** X11/X.h 的滑鼠鍵掩碼：Button1..Button5（bit 8..12）。 */
private const val ButtonMaskAny = (1 shl 8) or (1 shl 9) or (1 shl 10) or (1 shl 11) or (1 shl 12)

/**
 * 「滑鼠現在有沒有按著鍵」的探針。
 *
 * 為什麼要問 X 而不是 AWT：這臺 JDK 的 `java.awt.PointerInfo` 只有位置、沒有按鍵狀態；
 * 而托盤選單是 override-redirect 窗口，WM 不給它焦點，「丟焦點＝點了別處」這條
 * 平常最好用的路在 i3／bspwm 上永遠不會觸發（用戶 2026-09-29 點名「點別處不會收」）。
 * 剩下能判斷「外面有一下點擊」的来源只有 X 自己的 keyMask。
 */
internal class PointerButtonWatch : AutoCloseable {
    private val opened: Pair<X11Lib, Pointer>? = runCatching {
        val lib = loadX11() ?: return@runCatching null
        val d = lib.XOpenDisplay(null) ?: return@runCatching null
        lib to d
    }.getOrNull()

    private val root: Long = opened?.let { (lib, d) ->
        runCatching { lib.XDefaultRootWindow(d) }.getOrDefault(0L)
    } ?: 0L

    /** true = 有鍵按著。問不到一律回 false：寧可不收選單，也不要憑空收掉。 */
    fun pressed(): Boolean {
        val (lib, d) = opened ?: return false
        if (root == 0L) return false
        return runCatching {
            val mask = IntByReference()
            val sameScreen = LongByReference()
            val child = LongByReference()
            val rootX = IntByReference()
            val rootY = IntByReference()
            val winX = IntByReference()
            val winY = IntByReference()
            lib.XQueryPointer(d, root, sameScreen, child, rootX, rootY, winX, winY, mask) != 0 &&
                (mask.value and ButtonMaskAny) != 0
        }.getOrDefault(false)
    }

    override fun close() {
        val (lib, d) = opened ?: return
        runCatching { lib.XCloseDisplay(d) }
    }
}
