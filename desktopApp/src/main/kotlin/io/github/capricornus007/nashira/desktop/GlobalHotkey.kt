package io.github.capricornus007.nashira.desktop

import com.sun.jna.Callback
import com.sun.jna.Function
import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.unix.X11

/**
 * 全域快捷鍵（X11 XGrabKey）——app 自行註冊，不依賴 WM/sxhkd 配置
 * （Firefox ctrl+q 同款哲學）。抓 Ctrl+Alt+N → [onHotkey]（切換主視窗）。
 *
 * 實現：JNA 直連 Xlib。**必須開獨立的 XOpenDisplay 連線**——AWT 已占用
 * 預設 display 的事件佇列，混用會打架。XGrabKey 是對 root window 的全域
 * 鍵盤搶佔（只在按鍵按下瞬間攔截，不影響其他按鍵）。
 *
 * numlock（Mod2）會讓 grab 失配：註冊帶/不帶 Mod2 兩種組合。
 * Wayland 下 XGrabKey 無效：XOpenDisplay 會失敗或 grab 不生效，啟動
 * 時靜默跳過（Nashira 桌面目前只支援 X11）。
 */
object GlobalHotkey {
    private const val ANY_KEYCODE = 0 // XGrabKey 用 keycode 0 = AnyKey
    private const val GRAB_MODE_ASYNC = 1
    private const val CONTROL_MASK = 1 shl 2 // X11.ControlMask
    private const val MOD1_MASK = 1 shl 3 // X11.Mod1Mask (Alt)
    private const val MOD2_MASK = 1 shl 5 // NumLock
    private const val KEY_PRESS = 2

    private interface XLib : Library {
        fun XOpenDisplay(name: String?): Pointer?
        fun XDefaultRootWindow(dpy: Pointer?): Pointer?
        // X11 的 XGrabKey 回傳 void（錯誤走異步 error handler）；JNA 宣告
        // Int 會讀到返回暫存器的垃圾值——先前的 `== 0` 檢查因此隨機誤報
        // 「grab failed」（2026-09-14 重啟後 NASHIRA_HOTKEY 假失敗）。
        fun XGrabKey(
            dpy: Pointer?, ownerEvents: Int, keycode: Int, modifiers: Int,
            grabWindow: Pointer?, pointerMode: Int, keyboardMode: Int,
        )
        fun XKeysymToKeycode(dpy: Pointer?, keysym: Long): Int
        fun XSelectInput(dpy: Pointer?, w: Pointer?, eventMask: Long): Int
        fun XPending(dpy: Pointer?): Int
        fun XNextEvent(dpy: Pointer?, event: Pointer?): Int
        fun XCloseDisplay(dpy: Pointer?): Int
        fun XFree(data: Pointer?): Int
    }

    private var xlib: XLib? = null
    private var display: Pointer? = null
    @Volatile private var running = false
    private var thread: Thread? = null

    /**
     * 註冊全域快捷鍵。[keysym] 用 X11 鍵碼符號（如 X11.Keysym.N），
     * [modifiers] 額外修飾鍵（預設 Control+Alt）。成功回 true。
     * 重複呼叫先解除舊的。
     */
    fun register(keysym: Long, modifiers: Int = CONTROL_MASK or MOD1_MASK, onHotkey: () -> Unit): Boolean {
        unregister()
        val lib = try {
            Native.load("X11", XLib::class.java)
        } catch (_: Throwable) {
            return false // 無 X11（Wayland 純環境）——快捷鍵靜默不可用
        }
        val dpy = lib.XOpenDisplay(null) ?: return false
        val root = lib.XDefaultRootWindow(dpy) ?: run {
            lib.XCloseDisplay(dpy); return false
        }
        val keycode = lib.XKeysymToKeycode(dpy, keysym)
        if (keycode == 0) {
            lib.XCloseDisplay(dpy); return false
        }
        // 帶/不帶 numlock（Mod2）各註冊一次。衝突（BadAccess，如他客戶端已
        // 搶佔同一組合）由 Xlib 默認 error handler 打到 stderr——不以此判定成敗。
        sequenceOf(modifiers, modifiers or MOD2_MASK).forEach { mods ->
            lib.XGrabKey(dpy, 0, keycode, mods, root, GRAB_MODE_ASYNC, GRAB_MODE_ASYNC)
        }
        // KeyPressMask（1<<0）——只訂閱按鍵按下
        lib.XSelectInput(dpy, root, 1L shl 0)

        xlib = lib
        display = dpy
        running = true
        thread = Thread({
            // XEvent 聯合體最大 ~192 bytes；只讀 type（offset 0）
            val event = com.sun.jna.Memory(192)
            while (running) {
                try {
                    // XPending 阻塞前 flush；循環讀事件
                    if (lib.XPending(dpy) == 0) {
                        Thread.sleep(20) // 沒事件時讓出 CPU（X11 沒有 blocking next）
                        continue
                    }
                    lib.XNextEvent(dpy, event)
                    if (event.getInt(0) == KEY_PRESS) {
                        // X 事件線程 → AWT EDT（isVisible 等 AWT 調用不是線程安全的）
                        java.awt.EventQueue.invokeLater(onHotkey)
                    }
                } catch (e: Throwable) {
                    if (!running) break
                    System.err.println("Nashira global hotkey loop error: $e")
                }
            }
        }, "nashira-global-hotkey").apply {
            isDaemon = true
            start()
        }
        return true
    }

    fun unregister() {
        running = false
        thread?.interrupt()
        thread = null
        display?.let { d -> xlib?.XCloseDisplay(d) }
        display = null
        xlib = null
    }
}
