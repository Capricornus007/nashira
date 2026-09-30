package io.github.capricornus007.nashira.desktop

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.awt.SwingWindow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.window.Tray
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import io.github.capricornus007.nashira.LocalUiState
import io.github.capricornus007.nashira.i18n.stringsFor
import io.github.capricornus007.nashira.App
import io.github.capricornus007.nashira.rememberNashiraColorScheme
import io.github.capricornus007.nashira.theme.NashiraTheme
import io.github.capricornus007.nashira.theme.readXdgColorSchemeDark
import io.github.capricornus007.nashira.theme.xdgColorSchemeDarkFlow
import androidx.compose.runtime.snapshotFlow
import io.github.capricornus007.nashira.AppNotifications
import io.github.capricornus007.nashira.DesktopNotifications
import io.github.capricornus007.nashira.DesktopSingleInstance
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

// 托盤選單的「第一幀」估計尺寸：真實尺寸由內容量出來，這裡只求初始窗口不要太離譜。
private const val TRAY_MENU_EST_W = 200
private const val TRAY_MENU_EST_H = 118

// 選單左緣對到滑標左邊一點（≈圖示那一格的左沿），選單往右長——
// 跟 fcitx5／微信一樣是「向右展開」。用戶 2026-09-29 明確否決了往左長的寫法：
// 「我並沒有讓你把窗口新建方向從向右變成向左吧？」。太靠螢幕右緣時才夾回來。
private const val TRAY_MENU_LEFT_PAD = 10
private const val TRAY_MENU_GAP = 6

/** 選單 x：左緣貼滑標左邊一點，並夾在螢幕內。單位是 AWT 使用者空間（本機 = 實體 / uiScale）。 */
private fun trayMenuX(pointerX: Int, menuWidth: Int, screenWidth: Int): Int =
    (pointerX - TRAY_MENU_LEFT_PAD).coerceIn(0, (screenWidth - menuWidth).coerceAtLeast(0))

/** 選單 y：托盤在螢幕上半部就往下長，在下半部才往上長。 */
private fun trayMenuY(pointerY: Int, menuHeight: Int, screenHeight: Int): Int {
    val below = pointerY < screenHeight / 2
    val y = if (below) pointerY + TRAY_MENU_GAP else pointerY - menuHeight - TRAY_MENU_GAP
    return y.coerceIn(0, (screenHeight - menuHeight).coerceAtLeast(0))
}

/** 問不到托盤那一格多大時退回的邊長：AWT 自己假設的托盤尺寸就是 24。 */
private const val TRAY_ICON_FALLBACK_PX = 24

/** X11 ButtonPress 事件裡的滑鼠鍵編號。 */
private const val MouseButton1 = 1
private const val MouseButton3 = 3



/**
 * 把圖示收成托盤那一格「真的」尺寸。
 *
 * AWT 畫 TrayIcon 時還要再乘一次 uiScale，所以目標邊長 = X 量到的實體尺寸 ÷ uiScale
 * （本機 33 ÷ 2 → 16）。舊寫法固定餵 44px 又開著 isImageAutoSize，AWT 拿它自己假設的
 * 24 去算，圖被放大兩倍、只畫出左上角那一塊，看上去就是圖標跑到右下角
 * （用戶 2026-09-29 點名；64Gram／fcitx5／微信照格子實際尺寸畫，所以不錯位）。
 *
 * 問不到尺寸就維持原樣，不因為探測失敗把圖示弄不見。
 */
private fun fitTrayIcon(icon: java.awt.TrayIcon, src: java.awt.image.BufferedImage) {
    val slotPx = traySlotSizePx() ?: return
    val scale = awtUiScale()
    // 無條件舍去，不是四捨五入：湊整會畫得比畫布大（33/1.75→19→33.25px），
    // 多出來的那一點從右與下被裁掉，看上去就是圖貼到格子右下角。
    val side = (slotPx / scale).toInt().coerceIn(8, 64)
    java.awt.EventQueue.invokeLater {
        icon.isImageAutoSize = false
        icon.setImage(awtTrayImage(src, side))
    }
}

/**
 * AWT 使用者空間與 X 實體像素之間那把尺（本機 2.0）。
 *
 * 問 AWT 實際用的縮放，不是讀 sun.java2d.uiScale：那個屬性會被後端改寫
 *（本機我們設 2，跑起來 AWT 回報 1.75——Xft.dpi=168 走的是小數縮放），
 * 照屬性算就會算錯尺寸。
 */
private fun awtUiScale(): Float =
    runCatching {
        java.awt.Window.getWindows()
            .mapNotNull { it.graphicsConfiguration?.defaultTransform?.scaleX }
            .firstOrNull { it > 0.0 }
            ?.toFloat()
    }.getOrNull() ?: System.getProperty("sun.java2d.uiScale")?.toFloatOrNull() ?: 1f

@OptIn(ExperimentalComposeUiApi::class)
fun main(args: Array<String>) {
    // AWT 字體渲染優化：LCD 子像素抗鋸齒（預設灰階在深色主題下像「關閉優化的 Windows」）
    // 必須在 AWT 初始化前設定。
    System.setProperty("awt.useSystemAAFontSettings", "lcd")
    System.setProperty("swing.aatext", "true")
    // 單實例＋nashira:// 連結閘門：SSO scheme 回調會以本程式＋URL 參數再次
    // 啟動；有主實例在跑就把 URL 轉發過去後退出（純重複啟動同理），避免兩個
    // 實例共享同一個 Room DB 互相踩踏。
    if (!DesktopSingleInstance.handleLaunch(args)) return
    // 必須在 application {} 與任何 AWT 觸碰之前：非重親 WM 修正與 HiDPI 縮放
    // 都只在 toolkit 初始化前設定才生效。
    configureX11Platform()
    // 通知走 freedesktop 的 notify-send；視窗有焦點時不發（使用者正在看）
    AppNotifications.platform = DesktopNotifications
    application {
        // Linux 無動態取色；「跟隨系統」用 xdg portal（fallback GTK 設定檔）判深淺，
        // 啟動先取一次快照，之後輪詢跟隨中途切換。
        var systemDark by remember { mutableStateOf(readXdgColorSchemeDark() ?: false) }
        LaunchedEffect(Unit) {
            xdgColorSchemeDarkFlow().collect { systemDark = it }
        }

        // ── 背景常駐（系統托盤）───────────────────────────────────────────
        // Telegram/Discord 桌面慣例：關窗＝收進托盤繼續同步，托盤選單/圖示
        // 點擊開回視窗／真正退出。托盤不可用（罕見 WM）時維持關窗即退。
        //
        // 喚回必須「直接」操作 AWT 可見性，不能經 Compose 狀態：隱藏中的窗口
        // 重組會暫停，hiddenToTray=false 寫了狀態也沒有重組去消費——關窗後
        // nashira:// 連結喚不回的實測根因（2026-09-14，X 層 windowmap 可救
        // 證明窗口沒死、是 Compose 層沒跑）。
        val ui = LocalUiState.current
        // 托盤圖標直接用 app 圖標本體：這裡原本是手畫一顆 Arcaea 金星，跟主窗圖示
        // （nashira-icon.png）完全是兩個東西（用戶 2026-09-28：後台圖標要跟應用程式圖標統一）。
        // 原圖 512x512；要縮到多大不寫死，由 X 那一格的實際尺寸算出來（見 fitTrayIcon）。
        val traySource = remember {
            // 檔內拿不到「MainKt」這個類名，改用執行緒的 class loader 找 classpath 根目錄的圖
            runCatching {
                val url = Thread.currentThread().contextClassLoader?.getResource("nashira-icon.png")
                url?.let { javax.imageio.ImageIO.read(it) }
            }.getOrNull()
        }
        // 載不到圖就當作沒有托盤：寧可關窗即退，也不要收進去之後沒有圖示能召回。
        // 圖示「真的掛上去了」才算數：bar 正在重建時嵌入會失敗，掛不上卻以為掛上了，
        // 關窗就會藏進一個不存在的托盤、叫不回來（用戶 2026-09-29：後臺圖示不見）。
        var trayAttached by remember { mutableStateOf(false) }
        val strings = stringsFor(ui.language)
        var mainWindow by remember { mutableStateOf<java.awt.Window?>(null) }

        fun showMainWindow() {
            // 可見性一定要排回 EDT 做：這裡同時被托盤監聽器（EDT）與 nashira:// 的協程
            // （非 EDT）呼叫，直接改 isVisible 在兩條線上程之間會打架。
            java.awt.EventQueue.invokeLater {
                val w = mainWindow ?: return@invokeLater
                if (!w.isVisible) w.isVisible = true
                w.toFront()
                w.requestFocus()
                // 喚起是「另外開行程問 WM」，絕不能留在 EDT 上等：
                // 2026-09-29 用戶點名「一按打開 nashira 電腦就卡住、窗口消失但依舊佔位」——
                // 舊寫法 `xdotool search --class … windowactivate --sync` 在這裡同步等，
                // 而 --class 同時命中那顆 1x1 的工具窗口（它永遠不會被激活），
                // --sync 就永遠不返回；EDT 一凍結，窗口已經 map 但不再重繪，
                // 看起來就是「人不见了、位子還佔著」。
                activateWindowAsync(w)
            }
        }

        // 全域快捷鍵（XGrabKey）**拿掉了**（用戶 2026-09-30：「務必別，我不需要它……
        // 總覺得可能會跟其他軟件衝突到」）。他的擔憂是技術事實，不是偏好問題：
        // XGrabKey 是把鍵從正常派送裡搶走，註冊成功後**別的程式就再也收不到這組鍵**，
        // 誰先搶到誰贏、別人無從察覺。一個「叫回視窗」的便利功能不值得去動全域鍵盤。
        // 收回來的方式已經夠用：點托盤圖示（左鍵）或托盤選單的「開啟 Nashira」。
        // 之後若要提供這種熱鍵，條件是：設定頁預設關閉＋用戶自己按一個組合＋
        // 可一鍵解除註冊（見待辦 #85）。

        // 候選窗口「從外面搬 fcitx 的窗口」這條試過並**撤掉**了（ImCandidateMover 先留檔不啟動）：
        // fcitx5 每按一個鍵就自己擺一次位置，我們 40ms 後再拖一次，實測就是
        // 「打一個字閃一下、越打越不停地閃」（用戶 2026-09-29 點名）。
        // 要不吃架只能讓「搬行動作發生在 XIM 回調之內」——那是 JBR 內建的
        // ClientComponentCaretPositionTracker 的位置，外面模擬不出來。

        if (traySource != null) {
            // 選單用 Compose 視窗，不用 AWT PopupMenu：後者在 Linux 上不走系統字體渲染
            // 管線（FreeType），中文缺筆畫、抗鋸齒差（2026-09-14 用戶對比 fcitx5 截圖）。
            var trayMenuOpen by remember { mutableStateOf(false) }
            // 右鍵時記錄鼠標位置：菜單錨在托盤圖示上方（PlatformDefault 在
            // bspwm 會把窗口丟到屏幕頂部，2026-09-14 用戶截圖回報位置錯）。
            var trayMenuScreenPos by remember { mutableStateOf(java.awt.Point(0, 0)) }

            // 圖示優先走原生 XEmbed（自己照實體像素畫，見 X11TrayIcon）；建不起來才退回
            // AWT TrayIcon —— 那條路的畫布只有 16 使用者空間再硬Dup兩倍，必然糊。
            val nativeTray = remember(traySource) {
                X11TrayIcon.create(
                    render = { width, height -> trayArgb(traySource, width, height) },
                    onButton = { button, rootX, rootY ->
                        when (button) {
                            MouseButton3 -> {
                                // X 報的是實體像素，選單窗口吃的是 AWT 使用者空間，要換算
                                val scale = awtUiScale()
                                trayMenuScreenPos = java.awt.Point(
                                    (rootX / scale).roundToInt(),
                                    (rootY / scale).roundToInt(),
                                )
                                trayMenuOpen = true
                            }
                            MouseButton1 -> showMainWindow()
                        }
                    },
                )
            }
            if (nativeTray != null) {
                // 嵌入與否是事件執行緒回報的，用輪詢收；順帶盯 bar 重啟（換擁有者就重新 Dock）
                LaunchedEffect(nativeTray) {
                    val watch = TrayOwnerWatch()
                    try {
                        var owner = watch.owner()
                        while (true) {
                            val attached = nativeTray.isEmbedded
                            if (trayAttached != attached) trayAttached = attached
                            val now = watch.owner()
                            if (now > 0L && (now != owner || !attached)) {
                                println("NASHIRA_TRAY: 重新 Dock（embedded=$attached owner=$owner→$now）")
                                owner = now
                                nativeTray.redock(now)
                            }
                            delay(2_000L)
                        }
                    } finally {
                        watch.close()
                    }
                }
                DisposableEffect(nativeTray) {
                    onDispose {
                        nativeTray.close()
                        trayAttached = false
                    }
                }
            }
            if (nativeTray == null) {
                // 也不設 tooltip：懸浮跳出白底「Nashira」那一塊，用戶 2026-09-28 點名要拿掉。
                // remember：TrayIcon 必須全程同一個實例——下面只 add 一次，若每次重組都 new
                // 一顆，托盤就會被疊成一排（用戶 2026-09-25 照片：右鍵一次多一顆）。
                val trayIconAwt = remember(traySource) {
                    java.awt.TrayIcon(awtTrayImage(traySource, TRAY_ICON_FALLBACK_PX)).apply {
                        isImageAutoSize = true
                    }
                }
                // AWT 字體渲染：系統屬性在 JVM 啟動時設定（main() 最前面），
                // 這裡只設字體本身。抗鋸齒/LCD 子像素由 awt.useSystemAAFontSettings 控制。
                // 監聽器與托盤註冊都收進 DisposableEffect：原本兩行直接寫在 Composable 本體，
                // **每次重組就再 add 一次**——托盤被右鍵疊成一排、一次右鍵同時觸發好幾個監聽器
                // （用戶 2026-09-25 照片）。一進一出，離開時也把圖示拿掉，不留孤兒。
                DisposableEffect(trayIconAwt) {
                    val listener = object : java.awt.event.MouseAdapter() {
                        override fun mousePressed(e: java.awt.event.MouseEvent) {
                            when (e.button) {
                                java.awt.event.MouseEvent.BUTTON3 -> {
                                    trayMenuScreenPos = java.awt.MouseInfo.getPointerInfo().location
                                    trayMenuOpen = true
                                }
                                java.awt.event.MouseEvent.BUTTON1 -> showMainWindow()
                            }
                        }
                    }
                    val tray = java.awt.SystemTray.getSystemTray()
                    trayIconAwt.addMouseListener(listener)
                    onDispose {
                        if (trayAttached) runCatching { tray.remove(trayIconAwt) }
                        runCatching { trayIconAwt.removeMouseListener(listener) }
                        trayAttached = false
                    }
                }
                // 挂托盤＋自癒：
                // 1) add 失敗（bar 正在重建、那一瞬間沒人擁有托盤）就每 2 秒重試。原本這裡是
                //    `runCatching { tray.add(...) }` 把例外吞掉，結果行程活著、X 那邊卻完全沒有
                //    圖示視窗，而且程式還以為自己有托盤——關窗就藏進不存在的托盤，叫不回來。
                // 2) bar 被重建時（i3-msg restart / bar mode toggle / 換 WM），X 會把掛在舊 bar 下的
                //    圖示視窗一併銷掉，而 AWT 永遠不會重新嵌入。所以自己盯 `_NET_SYSTEM_TRAY_S0`
                //    的擁有者，換人就重掛。
                LaunchedEffect(trayIconAwt) {
                    val tray = java.awt.SystemTray.getSystemTray()
                    val watch = TrayOwnerWatch()
                    var owner = watch.owner()
                    // 診斷：JNA 拿不到 X 連線時 owner() 回 -1，自癒條件永遠不成立，
                    // 必須看得見才發現（用戶 2026-09-29：圖示不見、log 卻一聲不響）。
                        try {
                        while (true) {
                            if (!trayAttached) {
                                val attached = runCatching {
                                    tray.add(trayIconAwt)
                                    true
                                }.getOrDefault(false)
                                    if (attached) {
                                    trayAttached = true
                                    owner = watch.owner()
                                    // 掛上去之後才問得到那一格多大（socket 這時才存在）
                                    fitTrayIcon(trayIconAwt, traySource)
                                }
                            } else {
                                val now = watch.owner()
                                if (now > 0L && owner > 0L && now != owner) {
                                    println("NASHIRA_TRAY: 托盤擁有者換了 $owner -> $now，重新嵌入")
                                    runCatching { tray.remove(trayIconAwt) }
                                    trayAttached = false
                                    owner = now
                                }
                            }
                            delay(2_000L)
                        }
                    } finally {
                        watch.close()
                    }
                }
            }

            // 平鋪式 WM（i3／bspwm）會無視 setLocation，選單位置由 WM 說、不由我們說。
            // 解法是讓 X11 以 override-redirect 建這個窗口——WM 根本不會接管它：
            // `Window()` 有個 `create:` 多載（實驗性 API），把「顯示之前」那個時機
            // 交出來，才能在窗口還沒 displayable 時改 `type = POPUP`。
            // 2026-09-28 實測（i3、無合成器）：探針窗口 `WM_STATE: not found`、
            // 不在 `_NET_CLIENT_LIST`、i3 樹查無，`setLocation(700,480)` 原地不動。
            // 教訓：`type` 只能在尚未 displayable 時改——先前在 LaunchedEffect 裡改
            // （那時窗口早已 displayable）異常會被吞掉，位置照舊錯。POPUP 也要和
            // undecorated 同時設定，X11 後端才給 override-redirect。
            if (trayMenuOpen) {
                // AWT 座標系是「使用者空間」而非實體像素：本機 uiScale=2 時
                // `Toolkit.screenSize` 回報 1120x700（實體 2240x1400），`MouseInfo` 的滑標位置、
                // `setBounds` 吃的都是這套單位。實測教訓（2026-09-28）：拿 `200.dp.toPx()`
                // （=400 實體像素）去 setBounds，選單直接大两倍。
                // 而 Compose 的邏輯 dp 與 AWT 使用者空間在本機是同一個尺度（都 = 實體/2），
                // 所以 dp 數字原樣傳給 AWT 就對了。
                val scr = java.awt.Toolkit.getDefaultToolkit().screenSize
                val menuX = trayMenuX(trayMenuScreenPos.x, TRAY_MENU_EST_W, scr.width)
                // 托盤在螢幕上半部 → 選單往下長；在下半部才往上長
                val menuY = trayMenuY(trayMenuScreenPos.y, TRAY_MENU_EST_H, scr.height)
                SwingWindow(
                    create = {
                        // ComposeWindow 是 final，繼承不了；但 `create` 給的就是
                        // 「建立後、顯示前」那一段，apply 裡改 type 還趕得上。
                        ComposeWindow(
                            java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
                                .defaultScreenDevice.defaultConfiguration,
                        ).apply {
                            isUndecorated = true
                            type = java.awt.Window.Type.POPUP
                            isResizable = false
                            setBounds(menuX, menuY, TRAY_MENU_EST_W, TRAY_MENU_EST_H)
                        }
                    },
                    dispose = { it.dispose() },
                ) {
                    // override-redirect 窗口 WM 不會給焦點，要自己討；討不到也不影響點擊。
                    LaunchedEffect(window) { window.requestFocus() }
                    // 焦點丟失＝點了別處＝收起菜單（有 WM 管的時候才有效）。
                    LaunchedEffect(window) {
                        window.addWindowFocusListener(object : java.awt.event.WindowAdapter() {
                            override fun windowLostFocus(e: java.awt.event.WindowEvent?) {
                                trayMenuOpen = false
                            }
                        })
                    }
                    // 但 override-redirect 的窗口 WM 根本不接管，i3／bspwm 上「丟焦點」永遠不會
                    // 發生，於是點外面選單不收（用戶 2026-09-29 點名）。改成自己盯滑鼠：
                    // 指標在窗口外、而且是「新按下」任何鍵 → 收起。
                    // 按鍵狀態只能問 X（這臺 JDK 的 PointerInfo 沒有 getMouseButtons()），
                    // 位置則用 AWT 的：窗口邊界也是 AWT 使用者空間，兩邊同一把尺才比得準。
                    // 只認放開→按下這條上升緣：右鍵按下去開選單的那一下還沒放，
                    // 不這樣判會在剛打開的瞬間被自己關掉。
                    LaunchedEffect(window) {
                        val buttons = PointerButtonWatch()
                        try {
                            var pressedBefore = buttons.pressed()
                            while (true) {
                                delay(40L)
                                val location = runCatching {
                                    java.awt.MouseInfo.getPointerInfo()?.location
                                }.getOrNull() ?: continue
                                val pressed = buttons.pressed()
                                val inside = location.x >= window.x && location.x < window.x + window.width &&
                                    location.y >= window.y && location.y < window.y + window.height
                                if (pressed && !pressedBefore && !inside) {
                                    trayMenuOpen = false
                                    break
                                }
                                pressedBefore = pressed
                            }
                        } finally {
                            buttons.close()
                        }
                    }
                    // 選單是另一個 Window，吃不到 App() 裡算好的主題；不自己套同一套
                    // 配色就會「主窗深色、選單淺色」（2026-09-28 用戶截圖）。
                    NashiraTheme(colorScheme = rememberNashiraColorScheme(systemDark)) {
                        val density = LocalDensity.current
                        val menuBackground = MaterialTheme.colorScheme.surfaceContainerHigh
                        // 窗口寬高是「Compose 量到的實體 px ÷ density」取整數，會比畫面少掉
                        // 不到 1px；那條縫露出的是 AWT 窗口自己的底色（預設白）→
                        // 選單右側一條白線（用戶 2026-09-29 點名）。把底色塗成選單同色就看不見了。
                        LaunchedEffect(window, menuBackground) {
                            window.background = java.awt.Color(menuBackground.red, menuBackground.green, menuBackground.blue)
                        }
                        var contentSize by remember { mutableStateOf(androidx.compose.ui.unit.IntSize.Zero) }
                        // 窗口高度跟著內容收：create 時那個 118 是含舊版 10dp 外距的數字，
                        // 現在實心同尺寸就用不完了（用戶：選單還是偏大）。
                        // Compose 量到的 px 是實體像素，AWT 吃使用者空間（= 實體 / density），
                        // 不換算就會差一倍。
                        LaunchedEffect(contentSize) {
                            val w = (contentSize.width / density.density).roundToInt()
                            val h = (contentSize.height / density.density).roundToInt()
                            if (w <= 0 || h <= 0) return@LaunchedEffect
                            // 量到真實寬高之後必須連 x/y 一起重算：只 setSize 不移窗口，
                            // 窗口左上角還是用估計寬 200 算出來的，選單就偏到圖示左邊。
                            val scr = java.awt.Toolkit.getDefaultToolkit().screenSize
                            val x = trayMenuX(trayMenuScreenPos.x, w, scr.width)
                            val y = trayMenuY(trayMenuScreenPos.y, h, scr.height)
                            if (w != window.width || h != window.height) window.setSize(w, h)
                            if (x != window.x || y != window.y) window.setLocation(x, y)
                        }
                        // Surface 一定要 fillMaxSize：窗口寬高是「內容實體 px ÷ 縮放」取整數，
                        // 兩邊一定差不到 1px。若 Surface 只包內容大小，那條縫露出的是 Skia
                        // 面板自己的底（塗窗口背景色沒用），就是用戶點了幾輪的「右鍵選單右側白線」。
                        // 改成填滿整個窗口後，多出來的那一點是同一個底色，看不見。
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        ) {
                            // 條目用 fillMaxWidth 填的是「窗口寬」（200），所以右側永遠掛一片
                            // 空白。Column 先按最長那條文字收緊，fillMaxWidth 填的才是內容寬。
                            // 注意要用 IntrinsicSize.**Max**：Min 會收到「最長那個詞」，
                            //「開啟 Nashira」就被掰成兩行（用戶 2026-09-29 照片）。
                            // wrapContentSize(unbounded) 是「選單下方一大塊空白」的根治（用戶 2026-09-30 #49/#50）：
                            // 上面 Surface 的 fillMaxSize 讓窗口尺寸變成 Column 的**最小約束**，
                            // 實測 contentSize 永遠回報 400x236（＝窗口原尺寸 est 200x118 的兩倍實體 px），
                            // 於是「照內容縮窗口」那圈永遠縮不下去。unbounded 讓它按真實內容量。
                            Column(
                                modifier = Modifier
                                    .wrapContentSize(Alignment.TopStart, unbounded = true)
                                    .width(IntrinsicSize.Max)
                                    .onSizeChanged { contentSize = it },
                            ) {
                                Text(
                                    strings.trayOpen,
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            trayMenuOpen = false
                                            // 先收菜單再喚主窗：選單若還在，會搶走喚起後的焦點
                                            //（「開啟 Nashira 沒那麼好用」的根因）。
                                            java.awt.EventQueue.invokeLater { showMainWindow() }
                                        }
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    strings.trayQuit,
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            trayMenuOpen = false
                                            java.awt.EventQueue.invokeLater { exitApplication() }
                                        }
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    }
                }
            }
        }

        Window(
            onCloseRequest = {
                // 用「掛上了沒有」判斷，不是用「平台支援嗎」：圖示沒掛成功就藏窗，
                // 等於把程式關進一個叫不回來的地方。
                if (trayAttached) mainWindow?.isVisible = false else exitApplication()
            },
            title = "Nashira",
            icon = painterResource("nashira-icon.png"),
            // 官方 Window() composable：Skia 面板與 Compose 環境由它全權管理。
            // position 交給 PlatformDefault，讓平鋪式 WM 全權定位。
            // 教訓：不要手動建 ComposeWindow 再插手 isVisible——會產生不受管理的面板。
            // （上面那條教訓指「繞過 Window() 自建框架」；托盤收放是持有官方窗口
            // 的引用在 WindowScope 外做顯隱，安全。）
            state = rememberWindowState(
                position = WindowPosition.PlatformDefault,
                size = DpSize(1040.dp, 720.dp),
            ),
        ) {
            LaunchedEffect(window) { mainWindow = window }
            // window.isFocused 只在 WindowScope 內拿得到；焦點變化即時反映到通知抑制
            LaunchedEffect(window) {
                snapshotFlow { window.isFocused }.collect { DesktopNotifications.setForeground(it) }
            }
            // nashira:// 連結到達（SSO 回調/喚回）：直接顯示＋跳工作區
            LaunchedEffect(window) {
                DesktopSingleInstance.ssoLinkEvents.collect { showMainWindow() }
            }
            App(defaultDark = systemDark)
        }
    }
}

/**
 * 喚起主視窗。平鋪式 WM（i3／bspwm）對 AWT 的 toFront() 無感，要發 EWMH 的
 * `_NET_ACTIVE_WINDOW` 才會連工作區一起跳過去（xdotool windowactivate 走的就是它）。
 *
 * 兩條硬規則（2026-09-29 卡死實測換來的）：
 * 1. 只能在背景線程跑。等外部行程是「會不回來」的事，放在 EDT 上就是整個界面凍住。
 * 2. 不帶 `--sync`，而且要點名到那個能顯示的窗口：只給 `--class` 會連 AWT 那顆 1x1 的
 *    工具窗口一起命中，對它做 windowactivate 是等不到結果的（舊版就卡在這裡）。
 */
private fun activateWindowAsync(target: java.awt.Window) {
    Thread {
        val ok = runCatching {
            val process = ProcessBuilder(
                "xdotool", "search", "--class", "io-github-capricornus007-nashira",
                "--name", "^Nashira$", "windowactivate",
            ).redirectErrorStream(true).start()
            val finished = process.waitFor(3, java.util.concurrent.TimeUnit.SECONDS)
            if (!finished) process.destroyForcibly()
            finished && process.exitValue() == 0
        }.getOrDefault(false)
        if (!ok) {
            // xdotool 不在、或沒命中：退回 AWT 自己的 toFront（浮動 WM 夠用）
            java.awt.EventQueue.invokeLater { if (target.isDisplayable) target.toFront() }
        }
    }.apply {
        isDaemon = true
        name = "nashira-activate"
    }.start()
}
