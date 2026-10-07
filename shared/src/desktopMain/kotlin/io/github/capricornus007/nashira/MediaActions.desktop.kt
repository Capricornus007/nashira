package io.github.capricornus007.nashira

import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO

/**
 * 桌面：把圖片放進剪貼簿。
 *
 * 同時給「檔案」與「影像」兩種口味，不是貪心：AWT 的 X11 剪貼簿是**按需交付**，
 * 接收端要哪種才給哪種。只給 imageFlavor 的話，貼進很多檔案型接收端（檔管、部分
 * 編輯器）會貼出空白；只給檔案的話，貼進聊天室又拿不到圖。
 * 我們自己的 `clipboardHasImages()` 兩種都認（ImagePicking.desktop.kt），
 * 所以「複製圖片 → 在 Nashira 裡貼上」可以直接自我驗證。
 */
actual fun copyImageToClipboard(bytes: ByteArray, mimeType: String): Boolean {
    val image = runCatching { ImageIO.read(ByteArrayInputStream(bytes)) }.getOrNull() ?: return false
    val file = writeTempMedia(bytes, "nashira-clipboard", mimeType) ?: return false
    return runCatching {
        val transferable = object : Transferable {
            private val flavors = listOf(DataFlavor.javaFileListFlavor, DataFlavor.imageFlavor)

            override fun getTransferDataFlavors(): Array<DataFlavor> = flavors.toTypedArray()

            override fun isDataFlavorSupported(flavor: DataFlavor?): Boolean = flavor in flavors

            override fun getTransferData(flavor: DataFlavor?): Any = when (flavor) {
                DataFlavor.javaFileListFlavor -> listOf(file)
                DataFlavor.imageFlavor -> image
                else -> throw UnsupportedFlavorException(flavor)
            }
        }
        Toolkit.getDefaultToolkit().systemClipboard.setContents(transferable, null)
        true
    }.getOrDefault(false)
}

/** 桌面：落地成暫存檔再交給系統預設程式（i3 上走 xdg-open）。 */
actual fun openMediaExternally(bytes: ByteArray, fileName: String, mimeType: String): Boolean {
    val file = writeTempMedia(bytes, fileName.ifBlank { "nashira-media" }, mimeType) ?: return false
    return runCatching {
        if (java.awt.Desktop.isDesktopSupported() &&
            java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.OPEN)
        ) {
            java.awt.Desktop.getDesktop().open(file)
        } else {
            ProcessBuilder("xdg-open", file.absolutePath).redirectErrorStream(true).start()
        }
        true
    }.getOrDefault(false)
}

/**
 * 桌面：把網址交給**影片播放器**自己串流（秒開，不用等整檔下載）。
 *
 * ⚠️ 這裡絕對不能用 `xdg-open <https://…>`。實測（用戶 2026-10-08 #147）
 * 那樣做是**在瀏覽器裡打開影片**：xdg-open 對帶 scheme 的網址是照「協定」選程式，
 * `https` 就給瀏覽器，跟檔案類型無關。要照媒體類型選，得自己問 xdg-mime、
 * 再照它給的 .desktop 起程式。
 */
actual fun openMediaUrlExternally(url: String, mimeType: String): Boolean {
    val command = playerCommandFor(mimeType) ?: return false
    return runCatching {
        ProcessBuilder(command + url).redirectErrorStream(true).start()
        true
    }.getOrDefault(false)
}

/** 問到就照它起；問不到退回幾個常見播放器。回 null＝這臺上沒有能播的程式。 */
private fun playerCommandFor(mimeType: String): List<String>? {
    val desktop = runCatching {
        val probe = ProcessBuilder("xdg-mime", "query", "default", mimeType).redirectErrorStream(true).start()
        val out = probe.inputStream.use { it.readBytes() }.decodeToString().trim()
        if (probe.waitFor() == 0 && out.endsWith(".desktop")) out else null
    }.getOrNull()
    if (desktop != null) {
        // .desktop 的 Exec 裡 %U/%F/%f 這些欄位碼要自己拿掉，網址接在後面
        val exec = sequenceOf(
            java.nio.file.Path.of("/usr/share/applications", desktop),
            java.nio.file.Path.of(System.getProperty("user.home") + "/.local/share/applications", desktop),
        ).mapNotNull { path ->
            runCatching {
                java.nio.file.Files.readAllLines(path).firstOrNull { it.startsWith("Exec=") }
                    ?.removePrefix("Exec=")
            }.getOrNull()
        }.firstOrNull()
        if (exec != null) {
            val cleaned = exec.replace(Regex("%[uUfFdDnN]"), " ").trim()
            if (cleaned.isNotEmpty()) return cleaned.split(Regex("\\s+"))
        }
    }
    // 找不到 MIME 對應的 .desktop：直接找PATH 裡有的播放器（mpv 能吃網址）
    return listOf(listOf("mpv"), listOf("vlc"), listOf("mpv-git"), listOf("celluloid")).firstOrNull { candidate ->
        runCatching {
            val which = ProcessBuilder("which", candidate.first()).redirectErrorStream(true).start()
            which.inputStream.use { it.readBytes() }
            which.waitFor() == 0
        }.getOrDefault(false)
    }
}

private fun writeTempMedia(bytes: ByteArray, fileName: String, mimeType: String): File? = runCatching {
    val dir = Files.createTempDirectory("nashira-media").toFile()
    File(dir, fileName.substringAfterLast('/').ifBlank { "media" } + extensionOf(mimeType)).apply {
        writeBytes(bytes)
        deleteOnExit()
    }
}.getOrNull()

private fun extensionOf(mimeType: String): String = when {
    mimeType.contains("png") -> ".png"
    mimeType.contains("jpeg") || mimeType.contains("jpg") -> ".jpg"
    mimeType.contains("gif") -> ".gif"
    mimeType.contains("webp") -> ".webp"
    mimeType.contains("webm") -> ".webm"
    else -> ""
}
