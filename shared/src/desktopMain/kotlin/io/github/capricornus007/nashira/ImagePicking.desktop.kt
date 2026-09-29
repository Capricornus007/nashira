package io.github.capricornus007.nashira

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.EventQueue
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

/**
 * 桌面選圖：Swing 的 JFileChooser。
 *
 * 對話框建立與顯示必須在 AWT EDT；檔案讀取與影像尺寸探測仍在 IO dispatcher，
 * 避免模態選擇器或大型檔案操作阻塞 Compose UI 執行緒。
 */
@Composable
actual fun rememberImagePickerLauncher(
    onPicked: (PickedImage) -> Unit,
): (() -> Unit)? {
    val scope = rememberCoroutineScope()
    return remember(onPicked) {
        {
            scope.launch {
                // 對話框是多選的：一次圈幾張就逐張回調。原本寫死 isMultiSelectionEnabled=false，
                // 「一次發多張圖」在這臺機器上根本選不出來（用戶 2026-09-29 問「多圖訊息傳不出去」）。
                val picked = withContext(Dispatchers.IO) { chooseImages() }
                picked.forEach(onPicked)
            }
            Unit
        }
    }
}

@Composable
actual fun rememberFilePickerLauncher(
    onPicked: (PickedFile) -> Unit,
): (() -> Unit)? {
    val scope = rememberCoroutineScope()
    return remember(onPicked) {
        {
            scope.launch {
                val picked = withContext(Dispatchers.IO) { chooseFile() }
                if (picked != null) onPicked(picked)
            }
            Unit
        }
    }
}

private fun chooseFile(): PickedFile? {
    val file = chooseFilesWithDialog {
        JFileChooser().apply {
            dialogTitle = "選擇檔案"
            isMultiSelectionEnabled = false
        }
    }.firstOrNull() ?: return null
    val bytes = runCatching { file.readBytes() }.getOrNull() ?: return null
    return PickedFile(
        bytes = bytes,
        mimeType = runCatching { java.nio.file.Files.probeContentType(file.toPath()) }.getOrNull()
            ?: "application/octet-stream",
        fileName = file.name,
    )
}

private fun chooseImages(): List<PickedImage> {
    val files = chooseFilesWithDialog {
        JFileChooser().apply {
            dialogTitle = "選擇圖片"
            isMultiSelectionEnabled = true
            fileFilter = FileNameExtensionFilter("圖片 (png, jpg, jpeg, gif, webp)", "png", "jpg", "jpeg", "gif", "webp")
        }
    }
    return files.mapNotNull { file ->
        val bytes = runCatching { file.readBytes() }.getOrNull() ?: return@mapNotNull null
        val size = runCatching { ImageIO.read(file) }.getOrNull()
        PickedImage(
            bytes = bytes,
            mimeType = mimeTypeOf(file),
            fileName = file.name,
            width = size?.width,
            height = size?.height,
        )
    }
}

private fun chooseFilesWithDialog(factory: () -> JFileChooser): List<File> {
    var selected: List<File> = emptyList()
    val show = {
        val chooser = factory()
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            // 單選對話框 selectedFiles 只有一個；開多選時這裡拿到的就是全選的那幾張
            selected = chooser.selectedFiles.toList()
        }
    }
    if (EventQueue.isDispatchThread()) show() else EventQueue.invokeAndWait(show)
    return selected
}

private fun mimeTypeOf(file: File): String = when (file.extension.lowercase()) {
    "png" -> "image/png"
    "gif" -> "image/gif"
    "webp" -> "image/webp"
    "bmp" -> "image/bmp"
    else -> "image/jpeg"
}

actual val clipboardImagePasteSupported: Boolean = true

/**
 * 只問「有沒有這兩種格式」，不把資料搬過來。
 *
 * Compose 桌面端的 UI 執行緒就是 AWT 的 EDT，`isDispatchThread()` 那條分支會直接原地讀，
 * 不會自己等自己（invokeAndWait 在 EDT 上是會死鎖的）。
 */
actual fun clipboardHasImages(): Boolean {
    var ok = false
    val peek = {
        ok = runCatching {
            val transferable = java.awt.Toolkit.getDefaultToolkit().systemClipboard.getContents(null)
            transferable != null && (
                transferable.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.javaFileListFlavor) ||
                    transferable.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.imageFlavor)
                )
        }.getOrDefault(false)
    }
    if (EventQueue.isDispatchThread()) peek() else EventQueue.invokeAndWait(peek)
    return ok
}

/**
 * 桌面讀剪貼簿圖片。
 *
 * AWT 的 `getContents`/`getTransferData` 走 X11 選取內容協定，跟 JFileChooser 一樣
 * 要在 EDT 上碰（本檔既有的慣例就是 `EventQueue.invokeAndWait`）；編碼與尺寸探測
 * 留在 IO dispatcher，避免大圖卡住 UI。
 */
actual suspend fun readClipboardImages(): List<PickedImage> = withContext(Dispatchers.IO) {
    val files = clipboardData(java.awt.datatransfer.DataFlavor.javaFileListFlavor) as? List<*>
    val imageFiles = files?.filterIsInstance<File>()?.filter { isImageFile(it) }.orEmpty()
    if (imageFiles.isNotEmpty()) return@withContext imageFiles.mapNotNull(::pickedImageOf)
    // 沒有檔案才看影像本身（截圖工具、從瀏覽器「複製圖片」通常是這條）
    val raw = clipboardData(java.awt.datatransfer.DataFlavor.imageFlavor) as? java.awt.Image
    listOfNotNull(raw?.let(::pickedImageOf))
}

private fun isImageFile(file: File): Boolean =
    file.extension.lowercase() in setOf("png", "jpg", "jpeg", "gif", "webp", "bmp")

private fun clipboardData(flavor: java.awt.datatransfer.DataFlavor): Any? {
    var data: Any? = null
    val read = {
        data = runCatching {
            val clip = java.awt.Toolkit.getDefaultToolkit().systemClipboard
            val transferable = clip.getContents(null)
            if (transferable?.isDataFlavorSupported(flavor) == true) transferable.getTransferData(flavor) else null
        }.getOrNull()
    }
    if (EventQueue.isDispatchThread()) read() else EventQueue.invokeAndWait(read)
    return data
}

private fun pickedImageOf(file: File): PickedImage? {
    val bytes = runCatching { file.readBytes() }.getOrNull() ?: return null
    val dims = runCatching { ImageIO.read(file) }.getOrNull()
    return PickedImage(
        bytes = bytes,
        mimeType = mimeTypeOf(file),
        fileName = file.name,
        width = dims?.width,
        height = dims?.height,
    )
}

/** 剪貼簿裡的影像本身一律編成 PNG：它是已解開的位圖，沒有「保留原編碼」這回事。 */
private fun pickedImageOf(image: java.awt.Image): PickedImage? {
    val width = image.getWidth(null)
    val height = image.getHeight(null)
    if (width <= 0 || height <= 0) return null
    val bmp = java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB)
    val g = bmp.createGraphics()
    g.drawImage(image, 0, 0, null)
    g.dispose()
    val out = java.io.ByteArrayOutputStream()
    if (!ImageIO.write(bmp, "png", out)) return null
    return PickedImage(
        bytes = out.toByteArray(),
        mimeType = "image/png",
        fileName = "pasted-${System.currentTimeMillis() / 1000}.png",
        width = width,
        height = height,
    )
}
