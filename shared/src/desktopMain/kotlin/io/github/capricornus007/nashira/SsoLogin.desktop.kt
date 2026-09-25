package io.github.capricornus007.nashira

import io.github.capricornus007.nashira.matrix.MatrixEngine
import io.ktor.http.encodeURLParameter
import java.io.File

/**
 * Desktop SSO：**只走 nashira:// scheme**（與 Android 的 deep link 同構）。
 *
 * redirectUrl 是 nashira://sso/callback。瀏覽器認證完成後跳轉 scheme 連結，
 * xdg-open 依 .desktop 的 MimeType 啟動本程式，次實例 handleLaunch() 偵測到
 * 主實例在跑，把 URL 經 [DesktopSingleInstance] 的本地 socket 轉發過去即刻退出。
 *
 * 沒有 localhost HTTP server 回退路徑，兩個理由：
 * 1. loopback 的先天缺陷（佔埠、逾時後 token 打進死埠、地址欄殘留 loginToken）
 *    早已在 2026-09-14 桌面實測復現並據此改成 scheme；
 * 2. jpackage 出的執行時映像根本沒帶 `jdk.httpserver` 模組，留這條回退在發行包
 *    裡就是彈一顆 `NoClassDefFoundError: com/sun/net/httpserver/HttpServer` 的
 *    系統錯誤對話框（0.1.6 實測踩到），使用者只會看到「為什麼又要啟動服務器」。
 *
 * scheme 沒註冊時**自己補註冊**（寫使用者層級的 .desktop ＋ xdg-mime default），
 * 而不是退回那台伺服器；補不上才回錯誤訊息給登入頁顯示。
 */
actual suspend fun startSsoLogin(homeserver: String): Result<Unit>? {
    if (!isSchemeHandlerRegistered() && !registerSchemeHandler()) {
        // 訊息內容是給 i18n/LoginErrors.kt 比對的代碼，使用者看到的是那邊翻好的人話
        return Result.failure(IllegalStateException("NASHIRA_SSO_SCHEME_MISSING"))
    }
    return startSsoLoginViaScheme(homeserver)
}

/** xdg-mime 查得到 x-scheme-handler/nashira 代表有接收端。 */
private fun isSchemeHandlerRegistered(): Boolean = runCatching {
    val process = ProcessBuilder("xdg-mime", "query", "default", "x-scheme-handler/nashira")
        .redirectErrorStream(true)
        .start()
    val output = process.inputStream.bufferedReader().readText().trim()
    process.waitFor()
    output.isNotBlank()
}.getOrDefault(false)

/**
 * 使用者層級註冊：`~/.local/share/applications/nashira.desktop` 指向**正在跑的這個
 * 執行檔**，再 `xdg-mime default`。已經有別的接收端（例如開發用的 nashira-dev.desktop）
 * 時上面那道檢查就會通過、不會走到這裡，所以不會蓋掉用戶自己設的預設程式。
 *
 * 就算瀏覽器最後啟動的是「另一個」nashira 實體也無妨：次實例一律先把 URL 經
 * 47832 埠轉發給正在跑的主實例再退出，跨開發版／安裝版都接得起來。
 */
private fun registerSchemeHandler(): Boolean = runCatching {
    val exe = currentExecutable() ?: return@runCatching false
    val dir = File(System.getProperty("user.home"), ".local/share/applications")
    if (!dir.isDirectory && !dir.mkdirs()) return@runCatching false
    val desktop = File(dir, "nashira.desktop")
    desktop.writeText(
        """
        [Desktop Entry]
        Type=Application
        Version=1.0
        Name=Nashira
        Exec="$exe" %u
        Terminal=false
        NoDisplay=true
        MimeType=x-scheme-handler/nashira;

        """.trimIndent() + "\n",
        Charsets.UTF_8,
    )
    val process = ProcessBuilder("xdg-mime", "default", "nashira.desktop", "x-scheme-handler/nashira")
        .redirectErrorStream(true)
        .start()
    process.waitFor() == 0 && isSchemeHandlerRegistered()
}.getOrDefault(false)

/** jpackage 會設 `jpackage.app-path`；從終端機直接跑時退回 /proc/self/exe。 */
private fun currentExecutable(): String? = runCatching {
    System.getProperty("jpackage.app-path")?.takeIf { File(it).canExecute() }
        ?: File("/proc/self/exe").canonicalFile.takeIf { it.canExecute() }?.absolutePath
}.getOrNull()

private suspend fun startSsoLoginViaScheme(homeserver: String): Result<Unit>? {
    val callback = "nashira://sso/callback"
    openLink("$homeserver/_matrix/client/v3/login/sso/redirect?redirectUrl=${callback.encodeURLParameter()}")

    // scheme 方案下 app 端零資源佔用，瀏覽器分頁停留多久都行；15 分鐘是
    // 「使用者八成已放棄」的量級，逾時訊息會留在登入頁上。
    val callbackData = DesktopSingleInstance.awaitSsoCallback(timeoutMillis = 15 * 60 * 1000L)
    return if (callbackData != null) {
        MatrixEngine.loginWithToken(homeserver, callbackData.first)
    } else {
        Result.failure(IllegalStateException("SSO login timed out"))
    }
}
