package io.github.capricornus007.nashira

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 桌面通知：走 freedesktop 的 org.freedesktop.Notifications，用 `notify-send` 當前端。
 *
 * 為什麼不直接接 D-Bus：JVM 沒有內建 D-Bus 綁定，拉 dbus-java 只為了發通知不值得；
 * `notify-send`（libnotify）在任何有通知服務的桌面環境都在，取不到就整個停用（回 no-op）。
 *
 * 同一個房間的後續通知用 `--replace-id` 覆蓋前一則，跟 Android 端以同一個
 * notificationId 更新的行為一致，不會把同一個房間堆成一排。
 */
object DesktopNotifications : NotificationPlatform {

    /** 視窗有焦點時不發通知（使用者正在看）。由 Main.kt 依視窗焦點更新。 */
    private val foregroundFlow = MutableStateFlow(true)

    override val foreground: StateFlow<Boolean> get() = foregroundFlow

    fun setForeground(value: Boolean) {
        foregroundFlow.value = value
    }

    /** roomKey → 上一則通知的 id，用來覆蓋而不是堆疊。 */
    private val notificationIds = mutableMapOf<String, String>()

    private val available: Boolean by lazy {
        runCatching {
            ProcessBuilder("notify-send", "--version")
                .redirectErrorStream(true)
                .start()
                .waitFor() == 0
        }.getOrDefault(false)
    }

    override fun show(roomKey: String, title: String, body: String, mentionCount: Int) {
        if (!available) return
        val command = buildList {
            add("notify-send")
            add("--app-name=Nashira")
            add("--urgency=normal")
            notificationIds[roomKey]?.let { add("--replace-id=$it") }
            // 要拿回 id 才能下次覆蓋
            add("--print-id")
            add(title)
            add(body)
        }
        runCatching {
            val process = ProcessBuilder(command).redirectErrorStream(true).start()
            val output = process.inputStream.bufferedReader().use { it.readText() }.trim()
            process.waitFor()
            output.lineSequence().lastOrNull { line -> line.toIntOrNull() != null }
                ?.let { notificationIds[roomKey] = it }
        }
    }
}
