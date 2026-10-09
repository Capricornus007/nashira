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
            val target = withContext(Dispatchers.IO) {
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

/** 桌面「下載」：不問路徑，直接寫進 `~/Downloads`，同名就加序號，回傳實際路徑。 */
@Composable
actual fun rememberMediaDownloader(): suspend (bytes: ByteArray, fileName: String, mimeType: String) -> Result<String> =
    remember {
        downloader@{ bytes, fileName, _ ->
            withContext(Dispatchers.IO) {
                runCatching {
                    val dir = File(System.getProperty("user.home") ?: ".", "Downloads").apply { mkdirs() }
                    val wanted = fileName.ifBlank { "nashira-image.png" }
                    val base = wanted.substringBeforeLast('.', wanted)
                    val ext = wanted.substringAfterLast('.', "png")
                    var target = File(dir, wanted)
                    var index = 2
                    while (target.exists()) {
                        target = File(dir, "$base ($index).$ext")
                        index++
                    }
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
