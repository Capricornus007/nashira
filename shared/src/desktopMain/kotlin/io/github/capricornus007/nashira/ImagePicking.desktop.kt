package io.github.capricornus007.nashira

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

/**
 * 桌面選圖：Swing 的 JFileChooser。
 *
 * 必須跑在 AWT 之外的執行緒之外——`showOpenDialog` 是阻塞的模態對話框，
 * 直接在 Compose 的 UI 執行緒呼叫會把整個視窗凍住，所以丟到 IO 派發器。
 * 尺寸用 ImageIO 讀出來，讓 m.image 的 info 帶上 w/h（缺了對端無法先留版位）。
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
    val chooser = JFileChooser().apply {
        dialogTitle = "選擇檔案"
        isMultiSelectionEnabled = false
    }
    if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return null
    val file = chooser.selectedFile ?: return null
    val bytes = runCatching { file.readBytes() }.getOrNull() ?: return null
    return PickedFile(
        bytes = bytes,
        mimeType = runCatching { java.nio.file.Files.probeContentType(file.toPath()) }.getOrNull()
            ?: "application/octet-stream",
        fileName = file.name,
    )
}

private fun chooseImage(): PickedImage? {
    val chooser = JFileChooser().apply {
        dialogTitle = "選擇圖片"
        isMultiSelectionEnabled = false
        fileFilter = FileNameExtensionFilter("圖片 (png, jpg, jpeg, gif, webp)", "png", "jpg", "jpeg", "gif", "webp")
    }
    if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return null
    val file = chooser.selectedFile ?: return null
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

private fun mimeTypeOf(file: File): String = when (file.extension.lowercase()) {
    "png" -> "image/png"
    "gif" -> "image/gif"
    "webp" -> "image/webp"
    else -> "image/jpeg"
}
