package io.github.capricornus007.nashira.desktop

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import io.github.capricornus007.nashira.LocalUiState
import io.github.capricornus007.nashira.i18n.stringsFor
import io.github.capricornus007.nashira.App
import io.github.capricornus007.nashira.theme.readXdgColorSchemeDark
import io.github.capricornus007.nashira.theme.xdgColorSchemeDarkFlow
import androidx.compose.runtime.snapshotFlow
import io.github.capricornus007.nashira.AppNotifications
import io.github.capricornus007.nashira.DesktopNotifications
import io.github.capricornus007.nashira.DesktopSingleInstance

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
        val trayAvailable = java.awt.SystemTray.isSupported()
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

        if (trayAvailable) {
            // 直接 AWT TrayIcon：Compose 的 Tray 用預設 AWT 字體（醜）且圖標
            // 只能傳 Painter（純色）；這裡用 BufferedImage 圖標＋自訂字體。
            // 2026-09-14 用戶回報：右鍵菜單字體醜＋圖標太純色。
            val trayIcon = java.awt.image.BufferedImage(16, 16, java.awt.image.BufferedImage.TYPE_INT_ARGB).apply {
                val g = createGraphics()
                g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON)
                // Arcaea 金星：五角星路徑
                val gold = java.awt.Color(0xF2, 0xB6, 0x3C)
                val navy = java.awt.Color(0x1F, 0x1E, 0x33)
                g.color = navy
                g.fillRoundRect(0, 0, 16, 16, 4, 4)
                g.color = gold
                val cx = 8f; val cy = 8f; val r1 = 6f; val r2 = 2.5f
                val star = java.awt.geom.Path2D.Float()
                for (i in 0 until 10) {
                    val angle = Math.PI / 2 + i * Math.PI / 5
                    val r = if (i % 2 == 0) r1 else r2
                    val x = cx + r * kotlin.math.cos(angle).toFloat()
                    val y = cy - r * kotlin.math.sin(angle).toFloat()
                    if (i == 0) star.moveTo(x, y) else star.lineTo(x, y)
                }
                star.closePath()
                g.fill(star)
                g.dispose()
            }
            val trayIconAwt = java.awt.TrayIcon(trayIcon, "Nashira")
            trayIconAwt.isImageAutoSize = true
            // AWT 字體渲染：系統屬性在 JVM 啟動時設定（main() 最前面），
            // 這裡只設字體本身。抗鋸齒/LCD 子像素由 awt.useSystemAAFontSettings 控制。
            // AWT PopupMenu 在 Linux 上不用系統字體渲染管線（FreeType），
            // 中文缺筆畫、抗鋸齒差（2026-09-14 用戶對比 fcitx5 截圖）。
            // 改用 Compose Popup：跟 app 主體同渲染管線，字體/抗鋸齒一致。
            var trayMenuOpen by remember { mutableStateOf(false) }
            trayIconAwt.addMouseListener(object : java.awt.event.MouseAdapter() {
                override fun mousePressed(e: java.awt.event.MouseEvent) {
                    if (e.button == java.awt.event.MouseEvent.BUTTON3) {
                        trayMenuOpen = true
                    } else if (e.button == java.awt.event.MouseEvent.BUTTON1) {
                        showMainWindow()
                    }
                }
            })
            java.awt.SystemTray.getSystemTray().add(trayIconAwt)

            // Compose 托盤菜單（右鍵彈出）：獨立 Window，不掛主窗口——
            // Popup 在 application{} 裡沒有 LocalHostDefaultProvider 會崩
            // （2026-09-14 實測：右鍵托盤圖標直接炸）。
            if (trayMenuOpen) {
                Window(
                    onCloseRequest = { trayMenuOpen = false },
                    undecorated = true,
                    transparent = true,
                    resizable = false,
                    alwaysOnTop = true,
                    state = rememberWindowState(
                        position = WindowPosition.PlatformDefault,
                        size = DpSize(180.dp, 96.dp),
                    ),
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shadowElevation = 8.dp,
                    ) {
                        Column {
                            Text(
                                strings.trayOpen,
                                Modifier
                                    .clickable { trayMenuOpen = false; showMainWindow() }
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                strings.trayQuit,
                                Modifier
                                    .clickable { trayMenuOpen = false; exitApplication() }
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
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
