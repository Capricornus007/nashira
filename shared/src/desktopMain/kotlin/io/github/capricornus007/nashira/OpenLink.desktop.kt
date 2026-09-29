package io.github.capricornus007.nashira

import java.awt.Desktop
import java.net.URI

/**
 * 開外部連結。
 *
 * 只走 AWT 的 `Desktop.browse` 會「第二次點沒反應」：它內部只起一個桌面整合輔助
 * 流程，失敗時丟 IOException，而我們原本連都沒接、也沒備援 → 點了什麼都沒發生，
 * 也查不出原因（用戶 2026-09-29 在「帳戶與安全性」連點登出連結時點名）。
 * 這裡改成：先問 Desktop，不行（或丟例外）就直接叫 `xdg-open`，兩條路都失敗才留一行 log。
 */
actual fun openLink(url: String) {
    val uri = runCatching { URI(url) }.getOrNull() ?: return
    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
        runCatching { Desktop.getDesktop().browse(uri) }.onSuccess { return }
    }
    runCatching {
            ProcessBuilder("xdg-open", uri.toString()).redirectErrorStream(true).start().let { process ->
                // 吃掉結束、別擋著呼叫端（點擊常常在 EDT 上）
                Thread { runCatching { process.waitFor() } }.apply { isDaemon = true }.start()
            }
    }.onFailure {
        println("NASHIRA_OPENLINK: 開不了 $url（${it::class.simpleName} ${it.message}）")
    }
}
