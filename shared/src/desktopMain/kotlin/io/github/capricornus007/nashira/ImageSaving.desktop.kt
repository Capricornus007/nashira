package io.github.capricornus007.nashira

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.EventQueue
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

/** 桌面：在 AWT EDT 顯示儲存對話框，檔案寫入留在 IO dispatcher。 */
@Composable
actual fun rememberImageSaver(): suspend (bytes: ByteArray, fileName: String, mimeType: String) -> Result<String> =
    remember {
        saver@{ bytes, fileName, _ ->
            val target = withContext(Dispatchers.Main.immediate) {
                chooseSaveTarget(fileName)
            } ?: return@saver Result.failure(CancellationException("cancelled"))
            withContext(Dispatchers.IO) {
                runCatching {
                    target.writeBytes(bytes)
                    target.absolutePath
                }
            }
        }
    }

private fun chooseSaveTarget(fileName: String): File? {
    var selected: File? = null
    val show = {
        val chooser = JFileChooser().apply {
            selectedFile = File(fileName.ifBlank { "nashira-image.png" })
            val ext = fileName.substringAfterLast('.', "png")
            fileFilter = FileNameExtensionFilter("Images", ext)
        }
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            selected = chooser.selectedFile
        }
    }
    if (EventQueue.isDispatchThread()) show() else EventQueue.invokeAndWait(show)
    return selected
}

private class CancellationException(message: String) : Exception(message)
