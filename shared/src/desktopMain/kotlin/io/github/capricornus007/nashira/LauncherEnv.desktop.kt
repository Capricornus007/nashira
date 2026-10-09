package io.github.capricornus007.nashira

/**
 * 叫外部程式之前，把 jpackage 啟動器塞進行程環境的自家條子擦掉。
 *
 * 啟動器拉起 JVM 時會留兩個東西在環境裡：
 * - `_JPACKAGE_LAUNCHER=0`：啟動器用它認「我是不是已經在啟動流程裡」。
 *   托盤「重新啟動」就是被這個害的——殼帶著它再叫啟動器，啟動器跳過讀
 *   `lib/app/nashira.cfg`，java 參數拼出空的，退化成裸 `java` 印使用說明
 *   （用戶 2026-10-09 回報「按了只關掉」，查了四輪）。
 * - `LD_LIBRARY_PATH=:/opt/nashira/lib/app`：**開頭那個空元件＝目前工作目錄**。
 *   等於叫 ffmpeg／ffplay 先到 cwd 與 app 目錄去找 `.so`，而 app 目錄裝的是
 *   skiko／JBR 的原生庫，名稱與系統的 libavcodec／libswscale 撞車就是載到錯版本，
 *   表現會是解碼失敗、花屏之類查不出原因的東西。
 *
 * 所以：**凡是派外部程式，一律走這個擴充**。自己這進程當然照舊帶著它們（那是
 * 啟動器認自己用的），只有孩子要清乾淨。
 *
 * 寫法是 `environment()` 而不是 `environment`／`getEnvironment()`：
 * `ProcessBuilder` 那個方法就叫 `environment()`，同時又有一個同名**私有欄位**，
 * 用屬性語法會被 Kotlin 解析到欄位（編譯報 `it is private`），
 * 而 `getEnvironment()` 根本不存在。
 */
fun ProcessBuilder.withoutLauncherEnv(): ProcessBuilder = apply {
    environment().remove("_JPACKAGE_LAUNCHER")
    environment().remove("LD_LIBRARY_PATH")
}
