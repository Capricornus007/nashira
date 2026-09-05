package io.github.capricornus007.nashira.theme

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

/**
 * Linux 桌面的深淺判定，給「跟隨系統」用（Compose Desktop 的 isSystemInDarkTheme 在 JVM 恆 false）。
 *
 * 優先級：
 * 1. xdg-desktop-portal 的 `org.freedesktop.appearance color-scheme`（KDE/GNOME 標準，gdbus 呼叫）
 * 2. GTK3/4 設定檔的 `gtk-application-prefer-dark-theme` 或主題名帶 `-dark`
 * 3. 都拿不到 → null，呼叫端自己決定退路
 *
 * portal 沒跑（bspwm 這類淨桌面）時 gdbus 會失敗或掛起，所以設了逾時。
 */
fun readXdgColorSchemeDark(): Boolean? =
    portalColorScheme()?.let { return it == 1 }
        ?: gtkIniPrefersDark()?.let { return it }

/** portal 定義：0 無偏好、1 深色、2 淺色。 */
private fun portalColorScheme(): Int? = runCatching {
    val process = ProcessBuilder(
        "gdbus", "call", "--session",
        "--dest", "org.freedesktop.portal.Desktop",
        "--object-path", "/org/freedesktop/portal/desktop",
        "--method", "org.freedesktop.portal.Settings.ReadOne",
        "org.freedesktop.appearance", "color-scheme",
    ).start()
    try {
        if (!process.waitFor(3, java.util.concurrent.TimeUnit.SECONDS)) {
            process.destroyForcibly()
            return@runCatching null
        }
        Regex("uint32\\s+(\\d+)").find(process.inputStream.bufferedReader().readText())?.groupValues?.get(1)?.toIntOrNull()
    } finally {
        process.destroy()
    }
}.getOrNull()

private fun gtkIniPrefersDark(): Boolean? = runCatching {
    val home = System.getProperty("user.home")
    for (dir in listOf("gtk-4.0", "gtk-3.0")) {
        val file = File(home, ".config/$dir/settings.ini")
        if (!file.isFile) continue
        val text = file.readText()
        Regex("""gtk-application-prefer-dark-theme\s*=\s*(true|1)""", RegexOption.IGNORE_CASE)
            .containsMatchIn(text)
            .let { if (it) return@runCatching true }
        // 主題名帶 -dark 也算深色（Layan-dark 之類）
        Regex("""gtk-theme-name\s*=\s*(\S+)""", RegexOption.IGNORE_CASE)
            .find(text)?.groupValues?.get(1)
            ?.contains("-dark", ignoreCase = true)
            ?.let { if (it) return@runCatching true }
    }
    null
}.getOrNull()

/** 週期輪詢的深淺流：使用者中途切系統主題，App 在「跟隨系統」下會跟著翻面。 */
fun xdgColorSchemeDarkFlow(intervalMillis: Long = 15_000): Flow<Boolean> = flow {
    while (true) {
        emit(readXdgColorSchemeDark() ?: false)
        delay(intervalMillis)
    }
}.flowOn(Dispatchers.IO)
