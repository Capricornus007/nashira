package io.github.capricornus007.nashira.desktop

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.NativeLong
import com.sun.jna.Pointer
import com.sun.jna.Structure
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.NativeLongByReference
import com.sun.jna.ptr.PointerByReference
import java.awt.AWTEvent
import java.awt.Component
import java.awt.EventQueue
import java.awt.KeyboardFocusManager
import java.awt.Point
import java.awt.Rectangle
import java.awt.Toolkit
import java.awt.Window
import java.awt.event.AWTEventListener
import java.awt.im.InputMethodRequests
import java.io.Closeable
import javax.swing.Timer
import kotlin.math.roundToInt

/**
 * Stock-OpenJDK X11 input-method bridge.
 *
 * OpenJDK creates the active XIC with `XIMPreeditCallbacks`. Compose can report its caret through
 * [InputMethodRequests], but that XIC style has no `XNSpotLocation`, so fcitx5/IBus place their
 * candidate window at the client-window fallback. This bridge replaces only the active XIC with
 * the standard `XIMPreeditPosition | XIMStatusNothing` style and updates its spot location. Commit
 * lookup, focus changes, reset and disposal remain in OpenJDK's existing XInputMethod code because
 * the replacement pointer is installed in the same native X11InputMethodData structure.
 *
 * The three leading pointer fields of X11InputMethodData (`current_ic`, `ic_active`, `ic_passive`)
 * are the stable OpenJDK ABI used here. No vendor runtime API or vendor-only class is involved.
 */
internal object XimCandidatePosition {
    private const val XIM_PREEDIT_POSITION = 0x0004L
    private const val XIM_STATUS_NOTHING = 0x0400L
    private const val XIM_STYLE = XIM_PREEDIT_POSITION or XIM_STATUS_NOTHING

    private const val XN_CLIENT_WINDOW = "clientWindow"
    private const val XN_FOCUS_WINDOW = "focusWindow"
    private const val XN_INPUT_STYLE = "inputStyle"
    private const val XN_PREEDIT_ATTRIBUTES = "preeditAttributes"
    private const val XN_FONT_SET = "fontSet"
    private const val XN_SPOT_LOCATION = "spotLocation"

    fun install(window: Window): Closeable {
        if (!isSupportedSession()) return Closeable {}
        val tracker = Tracker(window)
        tracker.start()
        return tracker
    }

    private fun isSupportedSession(): Boolean =
        System.getProperty("os.name")?.contains("linux", ignoreCase = true) == true &&
            System.getenv("DISPLAY") != null

    private class Tracker(private val window: Window) : Closeable {
        private var updateQueued = false
        private var lastXic = 0L
        private var lastSpot: Point? = null
        private val settleTimer = Timer(40) { updateNow() }.apply { isRepeats = false }
        private val listener = AWTEventListener { event ->
            val component = event.source as? Component ?: return@AWTEventListener
            if (belongsToWindow(component, window)) requestUpdate()
        }

        fun start() {
            val mask = AWTEvent.KEY_EVENT_MASK or
                AWTEvent.MOUSE_EVENT_MASK or
                AWTEvent.MOUSE_MOTION_EVENT_MASK or
                AWTEvent.MOUSE_WHEEL_EVENT_MASK or
                AWTEvent.FOCUS_EVENT_MASK or
                AWTEvent.COMPONENT_EVENT_MASK or
                AWTEvent.WINDOW_EVENT_MASK or
                AWTEvent.WINDOW_FOCUS_EVENT_MASK
            Toolkit.getDefaultToolkit().addAWTEventListener(listener, mask)
            requestUpdate()
        }

        override fun close() {
            Toolkit.getDefaultToolkit().removeAWTEventListener(listener)
            settleTimer.stop()
        }

        private fun requestUpdate() {
            if (updateQueued) return
            updateQueued = true
            EventQueue.invokeLater {
                updateQueued = false
                updateNow()
                // Compose can finish a scroll/re-layout one frame after the AWT input event.
                settleTimer.restart()
            }
        }

        private fun updateNow() {
            if (!window.isShowing || !window.isFocused) return
            val focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().focusOwner ?: return
            if (!belongsToWindow(focusOwner, window)) return

            val context = focusOwner.inputContext ?: return
            val xic = OpenJdkXimBridge.ensurePositionXic(context) ?: return
            val client = OpenJdkXimBridge.clientComponent(context) ?: focusOwner
            if (!client.isShowing) return
            val requests = client.inputMethodRequests ?: focusOwner.inputMethodRequests ?: return
            val clientOnScreen = runCatching { client.locationOnScreen }.getOrNull() ?: return
            val windowOnScreen = runCatching { window.locationOnScreen }.getOrNull() ?: return
            val clientBounds = Rectangle(clientOnScreen.x, clientOnScreen.y, client.width, client.height)
            if (clientBounds.isEmpty) return
            val windowBounds = Rectangle(windowOnScreen.x, windowOnScreen.y, window.width, window.height)
            val caret = runCatching { requests.getTextLocation(null) }.getOrNull()
                ?: Rectangle(clientBounds.x, clientBounds.y + clientBounds.height - 1, 1, 1)
            val transform = client.graphicsConfiguration?.defaultTransform
                ?: window.graphicsConfiguration?.defaultTransform
            val spot = XimCandidateCoordinateContract.spot(
                caretScreenBounds = caret,
                clientScreenBounds = clientBounds,
                windowScreenBounds = windowBounds,
                scaleX = transform?.scaleX ?: 1.0,
                scaleY = transform?.scaleY ?: 1.0,
            )
            if (xic == lastXic && spot == lastSpot) return
            if (OpenJdkXimBridge.setSpot(xic, spot)) {
                lastXic = xic
                lastSpot = spot
            }
        }
    }

    private fun belongsToWindow(component: Component, window: Window): Boolean {
        var current: Component? = component
        while (current != null) {
            if (current === window) return true
            current = current.parent
        }
        return false
    }

    private object OpenJdkXimBridge {
        private val pointerSize = Native.POINTER_SIZE.toLong()
        private val inputMethodField by lazy {
            Class.forName("sun.awt.im.InputContext").getDeclaredField("inputMethod").apply { isAccessible = true }
        }
        private val clientComponentField by lazy {
            Class.forName("sun.awt.im.InputContext").getDeclaredField("currentClientComponent").apply { isAccessible = true }
        }
        private val nativeDataField by lazy {
            Class.forName("sun.awt.X11InputMethodBase").getDeclaredField("pData").apply { isAccessible = true }
        }
        private val awtLock by lazy {
            Class.forName("sun.awt.SunToolkit").getMethod("awtLock")
        }
        private val awtUnlock by lazy {
            Class.forName("sun.awt.SunToolkit").getMethod("awtUnlock")
        }
        private val xlib by lazy { Native.load("X11", Xlib::class.java) }

        private var installedData = 0L
        private var installedXic = 0L
        @Suppress("unused")
        private var retainedFontSet: Pointer? = null
        private var reportedFailure = false

        fun clientComponent(context: java.awt.im.InputContext): Component? =
            runCatching { clientComponentField.get(context) as? Component }.getOrNull()

        fun ensurePositionXic(context: java.awt.im.InputContext): Long? = runCatching {
            val inputMethod = inputMethodField.get(context) ?: return null
            if (!nativeDataField.declaringClass.isInstance(inputMethod)) return null
            val dataAddress = nativeDataField.getLong(inputMethod)
            if (dataAddress == 0L) return null

            withAwtLock {
                val data = Pointer(dataAddress)
                val active = data.getPointer(pointerSize) ?: return@withAwtLock null
                val activeAddress = Pointer.nativeValue(active)
                if (dataAddress == installedData && activeAddress == installedXic) return@withAwtLock installedXic

                val current = data.getPointer(0)
                val passive = data.getPointer(pointerSize * 2)
                val windowId = NativeLongByReference()
                val getError = xlib.XGetICValues(active, XN_CLIENT_WINDOW, windowId, null)
                if (getError != null || windowId.value.toLong() == 0L) return@withAwtLock null

                val xim = xlib.XIMOfIC(active) ?: return@withAwtLock null
                val display = xlib.XDisplayOfIM(xim) ?: return@withAwtLock null
                val fontSet = createFontSet(display) ?: return@withAwtLock null
                val initialSpot = XPoint(0.toShort(), 0.toShort()).apply { write() }
                val preedit = xlib.XVaCreateNestedList(
                    0,
                    XN_FONT_SET, fontSet,
                    XN_SPOT_LOCATION, initialSpot.pointer,
                    null,
                ) ?: return@withAwtLock null

                val replacement = try {
                    xlib.XCreateIC(
                        xim,
                        XN_CLIENT_WINDOW, windowId.value,
                        XN_INPUT_STYLE, NativeLong(XIM_STYLE),
                        XN_PREEDIT_ATTRIBUTES, preedit,
                        null,
                    )
                } finally {
                    xlib.XFree(preedit)
                } ?: return@withAwtLock null

                xlib.XSetICValues(replacement, XN_FOCUS_WINDOW, windowId.value, null)
                val replacingCurrent = current != null && Pointer.nativeValue(current) == activeAddress
                if (replacingCurrent) xlib.XUnsetICFocus(active)
                data.setPointer(pointerSize, replacement)
                if (passive != null && Pointer.nativeValue(passive) == activeAddress) {
                    data.setPointer(pointerSize * 2, replacement)
                }
                if (replacingCurrent) {
                    data.setPointer(0, replacement)
                    xlib.XSetICFocus(replacement)
                }
                xlib.XDestroyIC(active)

                installedData = dataAddress
                installedXic = Pointer.nativeValue(replacement)
                retainedFontSet = fontSet
                installedXic
            }
        }.onFailure(::reportFailure).getOrNull()

        fun setSpot(xicAddress: Long, spot: Point): Boolean = runCatching {
            withAwtLock {
                val nativeSpot = XPoint(
                    spot.x.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort(),
                    spot.y.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort(),
                ).apply { write() }
                val preedit = xlib.XVaCreateNestedList(0, XN_SPOT_LOCATION, nativeSpot.pointer, null)
                    ?: return@withAwtLock false
                val xic = Pointer(xicAddress)
                val xim = xlib.XIMOfIC(xic)
                val display = xim?.let(xlib::XDisplayOfIM)
                try {
                    xlib.XSetICValues(xic, XN_PREEDIT_ATTRIBUTES, preedit, null) == null
                } finally {
                    xlib.XFree(preedit)
                    if (display != null) xlib.XFlush(display)
                }
            }
        }.onFailure(::reportFailure).getOrDefault(false)

        private fun createFontSet(display: Pointer): Pointer? {
            val missing = PointerByReference()
            val missingCount = IntByReference()
            val defaultString = PointerByReference()
            val fontSet = xlib.XCreateFontSet(
                display,
                "-*-*-*-*-*-*-*-*-*-*-*-*-*-*",
                missing,
                missingCount,
                defaultString,
            )
            missing.value?.let(xlib::XFreeStringList)
            return fontSet
        }

        private inline fun <T> withAwtLock(block: () -> T): T {
            awtLock.invoke(null)
            return try {
                block()
            } finally {
                awtUnlock.invoke(null)
            }
        }

        private fun reportFailure(error: Throwable) {
            if (reportedFailure) return
            reportedFailure = true
            System.err.println("Nashira: stock OpenJDK XIM caret bridge unavailable: ${error.message}")
        }
    }

    private class XPoint() : Structure() {
        @JvmField var x: Short = 0
        @JvmField var y: Short = 0

        constructor(x: Short, y: Short) : this() {
            this.x = x
            this.y = y
        }

        override fun getFieldOrder(): List<String> = listOf("x", "y")
    }

    private interface Xlib : Library {
        fun XIMOfIC(ic: Pointer): Pointer?
        fun XDisplayOfIM(im: Pointer): Pointer?
        fun XGetICValues(ic: Pointer, vararg values: Any?): Pointer?
        fun XSetICValues(ic: Pointer, vararg values: Any?): Pointer?
        fun XVaCreateNestedList(unused: Int, vararg values: Any?): Pointer?
        fun XCreateIC(im: Pointer, vararg values: Any?): Pointer?
        fun XDestroyIC(ic: Pointer)
        fun XSetICFocus(ic: Pointer)
        fun XUnsetICFocus(ic: Pointer)
        fun XCreateFontSet(
            display: Pointer,
            baseFontNameList: String,
            missingCharsetList: PointerByReference,
            missingCharsetCount: IntByReference,
            defaultString: PointerByReference,
        ): Pointer?
        fun XFreeStringList(list: Pointer)
        fun XFree(data: Pointer): Int
        fun XFlush(display: Pointer?): Int
    }
}

/**
 * Coordinate contract for XIM `XNSpotLocation`.
 *
 * Inputs are AWT logical screen coordinates. The result is a device-pixel point relative to the
 * client window, located at the caret's lower edge and clamped to the visible Compose client. The
 * pure function is intentionally independent of Xlib so caret, scroll, window-move and HiDPI cases
 * can be tested without a running input-method daemon.
 */
internal object XimCandidateCoordinateContract {
    fun spot(
        caretScreenBounds: Rectangle,
        clientScreenBounds: Rectangle,
        windowScreenBounds: Rectangle,
        scaleX: Double,
        scaleY: Double,
    ): Point {
        require(clientScreenBounds.width > 0 && clientScreenBounds.height > 0)
        require(scaleX > 0.0 && scaleY > 0.0)

        val minX = clientScreenBounds.x
        val maxX = clientScreenBounds.x + clientScreenBounds.width - 1
        val minY = clientScreenBounds.y
        val maxY = clientScreenBounds.y + clientScreenBounds.height - 1
        val caretBottom = caretScreenBounds.y + caretScreenBounds.height.coerceAtLeast(1)
        val clampedX = caretScreenBounds.x.coerceIn(minX, maxX)
        val clampedY = caretBottom.coerceIn(minY, maxY)
        return Point(
            ((clampedX - windowScreenBounds.x) * scaleX).roundToInt(),
            ((clampedY - windowScreenBounds.y) * scaleY).roundToInt(),
        )
    }
}
