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
                val picked = withContext(Dispatchers.IO) { chooseImage() }
                if (picked != null) onPicked(picked)
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
    val file = chooseWithDialog {
        JFileChooser().apply {
            dialogTitle = "選擇檔案"
            isMultiSelectionEnabled = false
        }
    } ?: return null
    val bytes = runCatching { file.readBytes() }.getOrNull() ?: return null
    return PickedFile(
        bytes = bytes,
        mimeType = runCatching { java.nio.file.Files.probeContentType(file.toPath()) }.getOrNull()
            ?: "application/octet-stream",
        fileName = file.name,
    )
}

private fun chooseImage(): PickedImage? {
    val file = chooseWithDialog {
        JFileChooser().apply {
            dialogTitle = "選擇圖片"
            isMultiSelectionEnabled = false
            fileFilter = FileNameExtensionFilter("圖片 (png, jpg, jpeg, gif, webp)", "png", "jpg", "jpeg", "gif", "webp")
        }
    } ?: return null
    val bytes = runCatching { file.readBytes() }.getOrNull() ?: return null
    val size = runCatching { ImageIO.read(file) }.getOrNull()
    return PickedImage(
        bytes = bytes,
        mimeType = mimeTypeOf(file),
        fileName = file.name,
        width = size?.width,
        height = size?.height,
    )
}

private fun chooseWithDialog(factory: () -> JFileChooser): File? {
    var selected: File? = null
    val show = {
        val chooser = factory()
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selected = chooser.selectedFile
        }
    }
    if (EventQueue.isDispatchThread()) show() else EventQueue.invokeAndWait(show)
    return selected
}

private fun mimeTypeOf(file: File): String = when (file.extension.lowercase()) {
    "png" -> "image/png"
    "gif" -> "image/gif"
    "webp" -> "image/webp"
    else -> "image/jpeg"
}
