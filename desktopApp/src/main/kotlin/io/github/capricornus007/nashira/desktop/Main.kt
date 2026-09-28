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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import kotlin.math.roundToInt

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
        val trayImage = remember {
            // 檔內拿不到「MainKt」這個類名，改用執行緒的 class loader 找 classpath 根目錄的圖
            val url = Thread.currentThread().contextClassLoader?.getResource("nashira-icon.png")
            runCatching { url?.let { javax.imageio.ImageIO.read(it) } }.getOrNull()?.let { src ->
                // 原圖是 512x512。直接丟給 TrayIcon 的話，AWT 的 isImageAutoSize 只會
                // 一次性雙線性縮到托盤那格（十幾個像素），結果就是糊成一團
                // （用戶 2026-09-28：後台圖標沒修 —— 前一版 commit 訊息寫了縮 44x44，
                // 但原始碼裡那段根本沒進去，這次是真的做）。
                // 逐次減半縮到 44，每一步放大倍率都 ≤1/2，品質才留得住；
                // 面板若要更小再自己收，那一步已經很小不會糊。
                var cur = src
                while (cur.width > 44) {
                    val nextSide = (cur.width / 2).coerceAtLeast(44)
                    val next = java.awt.image.BufferedImage(
                        nextSide,
                        nextSide,
                        java.awt.image.BufferedImage.TYPE_INT_ARGB,
                    )
                    val g = next.createGraphics()
                    g.setRenderingHint(
                        java.awt.RenderingHints.KEY_INTERPOLATION,
                        java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR,
                    )
                    g.setRenderingHint(
                        java.awt.RenderingHints.KEY_RENDERING,
                        java.awt.RenderingHints.VALUE_RENDER_QUALITY,
                    )
                    g.drawImage(cur, 0, 0, nextSide, nextSide, null)
                    g.dispose()
                    cur = next
                }
                cur
            }
        }
        // 載不到圖就當作沒有托盤：寧可關窗即退，也不要收進去之後沒有圖示能召回。
        val trayAvailable = java.awt.SystemTray.isSupported() && trayImage != null
        val strings = stringsFor(ui.language)
        var mainWindow by remember { mutableStateOf<java.awt.Window?>(null) }

        fun showMainWindow() {
            val w = mainWindow ?: return
            w.isVisible = true
            w.toFront()
            w.requestFocus()
            activateWindow()
        }

        fun toggleMainWindow() {
            val w = mainWindow ?: return
            if (w.isVisible) w.isVisible = false else showMainWindow()
        }

        // 全域快捷鍵（app 自行註冊，XGrabKey）：Ctrl+Alt+N 切換主視窗。
        // 桌面限定；X11 不可用（純 Wayland）時靜默跳過。
        LaunchedEffect(Unit) {
            // X11 keysym：ASCII 可打印字元＝其字碼（'n' = 0x6E）
            val ok = GlobalHotkey.register(0x6E) { toggleMainWindow() }
            if (!ok) println("NASHIRA_HOTKEY: global hotkey unavailable (X11 grab failed)")
        }

        if (trayAvailable && trayImage != null) {
            // 也不設 tooltip：懸浮跳出白底「Nashira」那一塊，用戶 2026-09-28 點名要拿掉。
            // remember：TrayIcon 必須全程同一個實例——下面只 add 一次，若每次重組都 new
            // 一顆，托盤就會被疊成一排（用戶 2026-09-25 照片：右鍵一次多一顆）。
            val trayIconAwt = remember(trayImage) {
                java.awt.TrayIcon(trayImage).apply { isImageAutoSize = true }
            }
            // AWT 字體渲染：系統屬性在 JVM 啟動時設定（main() 最前面），
            // 這裡只設字體本身。抗鋸齒/LCD 子像素由 awt.useSystemAAFontSettings 控制。
            // AWT PopupMenu 在 Linux 上不用系統字體渲染管線（FreeType），
            // 中文缺筆畫、抗鋸齒差（2026-09-14 用戶對比 fcitx5 截圖）。
            // 改用 Compose Popup：跟 app 主體同渲染管線，字體/抗鋸齒一致。
            var trayMenuOpen by remember { mutableStateOf(false) }
            // 右鍵時記錄鼠標位置：菜單錨在托盤圖示上方（PlatformDefault 在
            // bspwm 會把窗口丟到屏幕頂部，2026-09-14 用戶截圖回報位置錯）。
            var trayMenuScreenPos by remember { mutableStateOf(java.awt.Point(0, 0)) }
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
                runCatching { tray.add(trayIconAwt) }
                onDispose {
                    runCatching { tray.remove(trayIconAwt) }
                    runCatching { trayIconAwt.removeMouseListener(listener) }
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
                val menuW = 200
                val menuH = 118
                val scr = java.awt.Toolkit.getDefaultToolkit().screenSize
                val menuX = (trayMenuScreenPos.x - menuW + 8).coerceIn(0, (scr.width - menuW).coerceAtLeast(0))
                // 托盤在螢幕上半部 → 選單往下長；在下半部才往上長
                val menuY = (
                    if (trayMenuScreenPos.y < scr.height / 2) trayMenuScreenPos.y + 6
                    else trayMenuScreenPos.y - menuH - 6
                ).coerceIn(0, (scr.height - menuH).coerceAtLeast(0))
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
                            setBounds(menuX, menuY, menuW, menuH)
                        }
                    },
                    dispose = { it.dispose() },
                ) {
                    // override-redirect 窗口 WM 不會給焦點，要自己討；討不到也不影響點擊。
                    LaunchedEffect(window) { window.requestFocus() }
                    // 焦點丟失＝點了別處＝收起菜單。
                    LaunchedEffect(window) {
                        window.addWindowFocusListener(object : java.awt.event.WindowAdapter() {
                            override fun windowLostFocus(e: java.awt.event.WindowEvent?) {
                                trayMenuOpen = false
                            }
                        })
                    }
                    // 選單是另一個 Window，吃不到 App() 裡算好的主題；不自己套同一套
                    // 配色就會「主窗深色、選單淺色」（2026-09-28 用戶截圖）。
                    NashiraTheme(colorScheme = rememberNashiraColorScheme(systemDark)) {
                        val density = LocalDensity.current
                        var contentSize by remember { mutableStateOf(androidx.compose.ui.unit.IntSize.Zero) }
                        // 窗口高度跟著內容收：create 時那個 118 是含舊版 10dp 外距的數字，
                        // 現在實心同尺寸就用不完了（用戶：選單還是偏大）。
                        // Compose 量到的 px 是實體像素，AWT 吃使用者空間（= 實體 / density），
                        // 不換算就會差一倍。
                        LaunchedEffect(contentSize) {
                            val w = (contentSize.width / density.density).roundToInt()
                            val h = (contentSize.height / density.density).roundToInt()
                            if (w > 0 && h > 0 && (w != window.width || h != window.height)) {
                                window.setSize(w, h)
                            }
                        }
                        Surface(
                            modifier = Modifier
                                .wrapContentSize()
                                .onSizeChanged { contentSize = it },
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        ) {
                            // 條目用 fillMaxWidth 填的是「窗口寬」（200），所以右側永遠掛一片
                            // 空白。Column 先按最長那條文字收緊，fillMaxWidth 填的才是內容寬。
                            Column(modifier = Modifier.width(IntrinsicSize.Min)) {
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
                if (trayAvailable) mainWindow?.isVisible = false else exitApplication()
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
 * 喚起主視窗。平鋪式 WM（bspwm/i3）對 AWT toFront() 的 XRaiseWindow 無感、
 * 更不會切工作區；EWMH 的 _NET_ACTIVE_WINDOW 才會（xdotool windowactivate
 * 走的就是它，2026-09-14 bspwm 實測會正確跳工作區）。xdotool 不在時回退
 * toFront()（浮動 WM 夠用）。用 WM_CLASS 定位（比標題精確，不會誤中瀏覽器
 * 分頁等同名視窗）。
 */
private fun activateWindow() {
    val activated = runCatching {
        val process = ProcessBuilder(
            "xdotool", "search", "--class", "io-github-capricornus007-nashira",
            "windowactivate", "--sync",
        ).redirectErrorStream(true).start()
        val output = process.inputStream.bufferedReader().readText()
        process.waitFor() == 0 && output.isNotBlank()
    }.getOrDefault(false)
    if (!activated) runCatching { java.awt.Window.getWindows().forEach { it.toFront() } }
}
