package io.github.capricornus007.nashira.desktop

import com.sun.jna.Memory
import com.sun.jna.NativeLong
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.LongByReference

/**
 * 自己當托盤客戶端（freedesktop System Tray Protocol + XEmbed），不再用 AWT 的 TrayIcon。
 *
 * 為什麼要自己來（2026-09-29 逐像素實測）：AWT 是 `XTrayIconPeer$IconCanvas.paint` 先把圖畫進
 * 一張「使用者空間尺寸」的 BufferedImage，再整塊丟上視窗。本機 uiScale=2、i3bar 那一格 33
 * 實體像素 → AWT 只有 16 使用者像素，實測截圖（.cache/nsh/fullC.png）裡每 2×2 區塊數值
 * 完全相同，就是硬複製放大，餵多清楚的原始圖都會糊；而且 32 塞在 33 的格子裡必然右、下
 * 各空 1px（用戶點名的「偏左」）。64Gram／fcitx5／微信不糊，因為它們直接照那一格的實體尺寸畫。
 *
 * 這裡的做法：開自己的 X 連線、建一顆窗口、送 SYSTEM_TRAY_REQUEST_DOCK，收到
 * XEMBED_EMBEDDED_NOTIFY 後 map，再用 XPutImage 一個像素一個像素畫；尺寸以 bar 送來的
 * ConfigureNotify 為準（本機 33×33），滑鼠鍵從自己的事件迴圈拿。
 *
 * 兩條紀律：
 * 1) 只有事件執行緒會碰 [display]（Xlib 預設執行緒不安全），所以關閉時不去打斷
 *    XNextEvent，而是用 XPending 輪詢；
 * 2) 全程不呼叫 XDestroyWindow，也不對「可能被 bar 重啟弄死」的窗口做請求：
 *    對已死窗口做任何請求都會丟 X error，而这臺 JDK 的 AWT error handler 會把它彈成
 *    「Unknown error」視窗（2026-09-29 踩過同款）。窗口交給 X server 在行程結束時收走。
 */
internal class X11TrayIcon private constructor(
    private val x11: X11Lib,
    private val display: Pointer,
    private val window: Long,
    private val trayOwner: Long,
    private val visual: Pointer?,
    private val depth: Int,
    private val render: (width: Int, height: Int) -> IntArray?,
    private val onButton: (button: Int, rootX: Int, rootY: Int) -> Unit,
) : AutoCloseable {

    /** bar 真的把我們嵌進去了（收到 XEMBED_EMBEDDED_NOTIFY）才算掛上。 */
    @Volatile
    var isEmbedded: Boolean = false
        private set

    @Volatile
    private var stopped = false

    private var gc: Pointer? = null
    private var image: Pointer? = null
    private var pixels: Memory? = null
    private var width = 0
    private var height = 0
    private var redShift = 16
    private var greenShift = 8
    private var blueShift = 0
    private var loop: Thread? = null


    private val atomXembed by lazy { x11.XInternAtom(display, AtomXembed, false) }
    private val atomXembedInfo by lazy { x11.XInternAtom(display, AtomXembedInfo, false) }
    private val atomOpcode by lazy { x11.XInternAtom(display, AtomSystemTrayOpcode, false) }

    private fun start() {
        installChannelMasks()
        requestBackingStore()
        x11.XStoreName(display, window, TrayWindowName)
        selectEvents()
        writeEmbedInfo(mapped = true)
        // 注意：嵌入之前絕對不能 map。這顆窗口建在 root 底下，一 map 出去 i3 就把它
        // 當成一般應用窗口接管（實測：被塞進容器、隨後連窗口一起被收掉 → 托盤沒圖示，
        // 而且 XGetGeometry 開始丟 BadWindow）。XEmbed 的順序是「送 dock 請求 →
        // 收到 EMBEDDED_NOTIFY（這時已經被 reparent 進 socket）→ 才 map」。
        requestDock(trayOwner)
        x11.XFlush(display)
        loop = Thread(::pump, "nashira-x11-tray").apply {
            isDaemon = true
            start()
        }
    }

    /**
     * 要求 X 幫我們保存畫面（backing_store = WhenMapped）。
     *
     * 沒有這個會怎樣：i3 的 bar 是 `mode hide`，隱藏時 bar 窗口整個 unmap，我們的窗口
     * 雖然 map 著但「不可見」。X 預設不保存不可見窗口的畫面，於是嵌入當下那次重畫
     * 直接被丟掉；等 bar 再出現，i3bar 抓我們的窗口 pixmap 來貼，抓到的就是空的
     * （實測：xwd 自己的窗口整塊 0，用戶看到的是「後臺圖標透明/黑了，但右鍵能用」）。
     * WhenMapped = 2（X.h），CWBackingStore 掩碼 = 1<<13，欄位在結構偏移 40（LP64）。
     */
    private fun requestBackingStore() {
        val attrs = Memory(112)
        attrs.setInt(BackingStoreOffset, BackingWhenMapped)
        x11.XChangeWindowAttributes(display, window, NativeLong(CWBackingStore), attrs)
    }

    /**
     * 選事件。嵌入之後必須**再選一次**：i3bar 在 reparent 客戶窗口時會用自己的
     * 事件遮罩覆寫我們的（XCB_CW_EVENT_MASK 是取代，不是加上），實測嵌入後
     * Exposure / VisibilityChange 一個都不送進來 → bar 重新可見時我們不重畫，
     * 那一格就永遠是黑的（xwd 我們自己的窗口：整塊 0）。
     */
    private fun selectEvents() {
        x11.XSelectInput(
            display,
            window,
            NativeLong((MaskExposure or MaskButtonPress or MaskButtonRelease or MaskVisibilityChange or MaskStructureNotify).toLong()),
        )
    }

    /** bar 重啟（擁有者換人）之後，拿同一顆窗口再 Dock 一次：不重建、不洩漏窗口。 */
    fun redock(newOwner: Long) {
        isEmbedded = false
        writeEmbedInfo(mapped = true)
        requestDock(newOwner)
        x11.XFlush(display)
    }

    /** 讀 visual 的紅綠藍遮罩算移位；寫死 16/8/0 一旦遇到非典型 visual 就是紅藍對調。 */
    private fun installChannelMasks() {
        val template = Memory(VisualInfoSize)
        template.setLong(VisualInfoIdOffset, x11.XVisualIDFromVisual(visual))
        val count = IntByReference()
        val info = x11.XGetVisualInfo(display, NativeLong(VisualIdMask.toLong()), template, count) ?: return
        try {
            if (count.value <= 0) return
            redShift = shiftOf(info.getLong(VisualInfoRedMaskOffset))
            greenShift = shiftOf(info.getLong(VisualInfoGreenMaskOffset))
            blueShift = shiftOf(info.getLong(VisualInfoBlueMaskOffset))
        } finally {
            x11.XFree(info)
        }
    }

    private fun shiftOf(mask: Long): Int {
        if (mask <= 0L) return 0
        var m = mask
        var shift = 0
        while ((m and 1L) == 0L) {
            m = m shr 1
            shift++
        }
        return shift
    }

    private fun writeEmbedInfo(mapped: Boolean) {
        // _XEMBED_INFO 是兩個 CARD32，但 format=32 時 Xlib 吃的是 long 陣列（8 -byte 間隔）
        val data = Memory(16)
        data.setLong(0, 0L)
        data.setLong(8, if (mapped) XembedMapped else 0L)
        x11.XChangeProperty(
            display, window, atomXembedInfo, atomXembedInfo, 32, PropModeReplace, data, 2,
        )
    }

    private fun requestDock(target: Long) {
        sendClientMessage(
            target = target,
            messageType = atomOpcode,
            data = longArrayOf(0L, SystemTrayRequestDock, window, 0L, 0L),
        )
    }

    private fun sendClientMessage(target: Long, messageType: Long, data: LongArray) {
        val event = Memory(EventSize)
        event.setInt(0, EventClientMessage)
        event.setInt(16, 1) // send_event = True
        event.setPointer(24, display)
        event.setLong(OffWindow, window)
        event.setLong(OffMessageType, messageType)
        event.setInt(OffFormat, 32)
        data.forEachIndexed { index, value -> event.setLong(OffData + index * 8L, value) }
        x11.XSendEvent(display, target, 0, NativeLong(0L), event)
    }

    private fun pump() {
        val event = Memory(EventSize)
        try {
            while (!stopped) {
                while (x11.XPending(display) > 0) {
                    x11.XNextEvent(display, event)
                    handle(event)
                }
                Thread.sleep(60L)
            }
        } catch (error: Throwable) {
            println("NASHIRA_TRAY: X 事件迴圈結束 ${error::class.simpleName} ${error.message}")
        }
    }

    private fun handle(event: Pointer) {
        val type = event.getInt(0)
        when (type) {
            // bar 平时是 unmap 的（i3 的 mode hide）。窗口不可見時畫的內容會被 X 丟掉，
            // 而 bar 重新 map 時送給我們的不是 Expose，是 VisibilityNotify——
            // 只等 Expose 就會永遠是一格黑（2026-09-29 實測：窗口自己 xwd 出來全黑）。
            EventExpose, EventVisibilityNotify, EventConfigureNotify -> {
                refreshGeometry()
                redraw()
            }

            EventConfigureNotify -> {
                refreshGeometry()
                redraw()
            }

            EventButtonPress -> onButton(
                event.getInt(OffButton),
                event.getInt(OffRootX),
                event.getInt(OffRootY),
            )

            EventClientMessage -> {
                if (event.getLong(OffMessageType) != atomXembed) return
                if (event.getInt(OffFormat) != 32) return
                if (event.getLong(OffData + 8L) != XembedEmbeddedNotify) return
                isEmbedded = true
                println("NASHIRA_TRAY: 已嵌入托盤 window=0x${window.toString(16)}")
                selectEvents() // 嵌入式之後重選：見 selectEvents 的說明
                // 被 reparent 進 socket 之後才 map；map 完 bar 才會開始要畫面
                x11.XMapWindow(display, window)
                refreshGeometry()
                redraw()
            }
        }
    }

    /**
     * 尺寸一律問 XGetGeometry，不信 ConfigureNotify 裡的欄位：
     * 事件結構是 C 記憶體佈局，偏移錯一格就會把 x/y 當成 width/height（實測看過 2203×4）。
     */
    private fun refreshGeometry() {
        val w = IntByReference()
        val h = IntByReference()
        if (x11.XGetGeometry(
                display, window, LongByReference(), IntByReference(), IntByReference(), w, h,
                IntByReference(), IntByReference(),
            ) == 0
        ) {
            return
        }
        if (w.value <= 0 || h.value <= 0) return
        if (w.value != width || h.value != height) {
            println("NASHIRA_TRAY: 那一格 $width×$height → ${w.value}×${h.value}（實體像素）")
            width = w.value
            height = h.value
            image = null // 尺寸變了，XImage 要重作
        }
    }

    private fun redraw() {
        if (width <= 0 || height <= 0) return
        val argb = render(width, height) ?: return
        val count = width * height
        val buffer = pixels?.takeIf { it.size() >= count.toLong() * 4 }
            ?: Memory(count.toLong() * 4).also { pixels = it }
        for (i in 0 until count) {
            val value = argb[i]
            buffer.setInt(
                i * 4L,
                (((value shr 16) and 0xFF) shl redShift) or
                    (((value shr 8) and 0xFF) shl greenShift) or
                    ((value and 0xFF) shl blueShift),
            )
        }
        if (image == null) {
            x11.XFree(image) // 舊尺寸那張：結構是 Xlib malloc 的，交還 XFree；像素記憶體是我們自己的
            image = x11.XCreateImage(display, visual, depth, ZPixmap, 0, buffer, width, height, 32, width * 4)
        }
        val handle = image ?: return
        if (gc == null) gc = x11.XCreateGC(display, window, NativeLong(0L), null)
        x11.XPutImage(display, window, gc, handle, 0, 0, 0, 0, width, height)
        x11.XFlush(display)
    }

    override fun close() {
        stopped = true
        loop?.join(1_000L)
        loop = null
        runCatching { writeEmbedInfo(mapped = false) }
        runCatching { x11.XUnmapWindow(display, window) }
        runCatching { x11.XFlush(display) }
        isEmbedded = false
    }

    companion object {
        /**
         * 建一顆原生托盤圖示。回 null = 沒有托盤管理器／JNA 載不到，呼叫端退回 AWT 那條路。
         * 不阻塞：嵌入成功與否用 [isEmbedded] 問（事件執行緒會設）。
         */
        fun create(
            render: (width: Int, height: Int) -> IntArray?,
            onButton: (button: Int, rootX: Int, rootY: Int) -> Unit,
        ): X11TrayIcon? = runCatching {
            val x11 = loadX11() ?: return null
            val display = x11.XOpenDisplay(null) ?: return null
            val screen = x11.XDefaultScreen(display)
            val owner = x11.XGetSelectionOwner(
                display,
                x11.XInternAtom(display, "_NET_SYSTEM_TRAY_S$screen", false),
            )
            if (owner == 0L) {
                println("NASHIRA_TRAY: 這一瞬間沒有人擁有托盤（bar 正在重啟？）")
                x11.XCloseDisplay(display)
                return null
            }
            // 嵌入前的初始尺寸不要紧：i3bar 會用那一格的實際尺寸 ConfigureNotify 報給我們的
            val window = x11.XCreateSimpleWindow(
                display,
                x11.XRootWindow(display, screen),
                0,
                0,
                InitialSizePx,
                InitialSizePx,
                0,
                NativeLong(0L),
                NativeLong(0L),
            )
            if (window == 0L) {
                x11.XCloseDisplay(display)
                return null
            }
            X11TrayIcon(
                x11 = x11,
                display = display,
                window = window,
                trayOwner = owner,
                visual = x11.XDefaultVisual(display, screen),
                depth = x11.XDefaultDepth(display, screen),
                render = render,
                onButton = onButton,
            ).also { it.start() }
        }.getOrElse { error ->
            println("NASHIRA_TRAY: 原生托盤建立失敗 ${error::class.simpleName} ${error.message}")
            null
        }

        private const val InitialSizePx = 24
    }
}

// ── X11 常數（X.h ／ system-tray spec ／ Xembed spec）─────────────────────────
private const val EventExpose = 12
private const val EventButtonPress = 4
private const val EventClientMessage = 33
private const val EventVisibilityNotify = 15
private const val EventConfigureNotify = 22
private const val EventSize = 192L

// X.h 的 EventMask：這些位移一個都不能憑感覺寫（實測教訓：把 ExposureMask
// 當成 1<<2 結果那兩位其實是 ButtonPress/ButtonRelease，右鍵能用、
// 但 bar 重新可見時的 Expose 一個都收不到，圖標就長期是一格黑的）
private const val MaskButtonPress = 1 shl 2
private const val MaskButtonRelease = 1 shl 3
private const val MaskVisibilityChange = 1 shl 14
private const val MaskExposure = 1 shl 15
private const val MaskStructureNotify = 1 shl 17

private const val PropModeReplace = 0
private const val ZPixmap = 2

// XEvent（64 位元）欄位偏移
private const val OffWindow = 32L
private const val OffMessageType = 40L
private const val OffFormat = 48L
private const val OffData = 56L
private const val OffButton = 84L
private const val OffRootX = 72L
private const val OffRootY = 76L


// XVisualInfo（64 位元）
private const val VisualInfoSize = 64L
private const val VisualInfoIdOffset = 8L
private const val VisualInfoRedMaskOffset = 32L
private const val VisualInfoGreenMaskOffset = 40L
private const val VisualInfoBlueMaskOffset = 48L
private const val VisualIdMask = 16

private const val AtomXembed = "_XEMBED"
private const val AtomXembedInfo = "_XEMBED_INFO"
private const val AtomSystemTrayOpcode = "_NET_SYSTEM_TRAY_OPCODE"
private const val SystemTrayRequestDock = 0L
private const val XembedEmbeddedNotify = 0L
private const val XembedMapped = 1L

private const val TrayWindowName = "Nashira"

// XSetWindowAttributes（64 位元）裡 backing_store 的偏移與值
private const val BackingStoreOffset = 40L
private const val BackingWhenMapped = 2
private const val CWBackingStore = 1L shl 13
