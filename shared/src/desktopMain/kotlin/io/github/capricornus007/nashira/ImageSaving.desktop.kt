package io.github.capricornus007.nashira

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

/** 桌面：彈 JFileChooser 選儲存路徑，取消視爲失敗（訊息安靜收掉）。 */
@Composable
actual fun rememberImageSaver(): suspend (bytes: ByteArray, fileName: String, mimeType: String) -> Result<String> =
    remember {
        saver@{ bytes, fileName, _ ->
            withContext(Dispatchers.IO) {
                val chooser = JFileChooser().apply {
                    selectedFile = File(fileName.ifBlank { "nashira-image.png" })
                    val ext = fileName.substringAfterLast('.', "png")
                    fileFilter = FileNameExtensionFilter("Images", ext)
                }
                val choice = chooser.showSaveDialog(null)
                if (choice != JFileChooser.APPROVE_OPTION) {
                    return@withContext Result.failure(CancellationException("cancelled"))
                }
                runCatching {
                    val target = chooser.selectedFile
                    target.writeBytes(bytes)
                    target.absolutePath
                }
            }
        }
    }

private class CancellationException(message: String) : Exception(message)
