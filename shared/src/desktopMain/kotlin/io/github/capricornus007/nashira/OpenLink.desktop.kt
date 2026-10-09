package io.github.capricornus007.nashira

import java.awt.Desktop
import java.net.URI

/**
 * 開外部連結。
 *
 * Linux 上先走 `xdg-open`：AWT 的 `Desktop.browse` 只有「第一次」開得起來——它成功時
 * 也不會回報「瀏覽器其實沒開新分頁」，所以拿它當主路徑時，第二次的點擊會靜默無效
 * （用戶 2026-09-29 兩輪點名，並說明不是連點才觸發）。`xdg-open` 是桌面環境自己那套
 * 預設程式入口，Qt/GTK 應用實際用的也是它，所以放前面；`Desktop.browse` 留當備援
 * （沒有 xdg-open 的極精簡環境）。
 */
actual fun openLink(url: String) {
    val uri = runCatching { URI(url) }.getOrNull() ?: return
    if (runXdgOpen(uri.toString())) return
    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
        runCatching { Desktop.getDesktop().browse(uri) }.onFailure {
            println("NASHIRA_OPENLINK: 開不了 $url（${it::class.simpleName} ${it.message}）")
        }
        return
    }
    println("NASHIRA_OPENLINK: 開不了 $url（沒有 xdg-open，也不支援 Desktop.browse）")
}

/** 起來 xdg-open 就回 true；非阻塞——點擊常常在 EDT 上，不能等瀏覽器。 */
private fun runXdgOpen(url: String): Boolean = runCatching {
    val process = ProcessBuilder("xdg-open", url).redirectErrorStream(true).withoutLauncherEnv().start()
    Thread { runCatching { process.waitFor() } }.apply { isDaemon = true }.start()
    true
}.getOrDefault(false)
