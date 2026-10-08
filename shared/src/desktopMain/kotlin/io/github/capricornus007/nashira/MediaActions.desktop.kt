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
