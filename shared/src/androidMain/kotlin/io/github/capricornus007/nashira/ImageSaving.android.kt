package io.github.capricornus007.nashira

import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 寫進 MediaStore 的 Pictures/Nashira：下載即入相簿，不用申請儲存權限
 * （分區儲存下走 MediaStore API 就是正道）。
 */
@Composable
actual fun rememberImageSaver(): suspend (bytes: ByteArray, fileName: String, mimeType: String) -> Result<String> =
    remember {
        saver@{ bytes, fileName, mimeType ->
            withContext(Dispatchers.IO) {
                runCatching {
                    val context = appContext ?: error("no app context")
                    val resolver = context.contentResolver
                    val sanitized = fileName.ifBlank { "nashira_${System.currentTimeMillis()}" }
                    val values = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, sanitized)
                        val type = if (mimeType.startsWith("image/")) mimeType else "image/png"
                        put(MediaStore.Images.Media.MIME_TYPE, type)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Nashira")
                            put(MediaStore.Images.Media.IS_PENDING, 1)
                        }
                    }
                    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                        ?: error("MediaStore insert failed")
                    resolver.openOutputStream(uri)?.use { it.write(bytes) }
                        ?: error("cannot open output stream")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        values.clear()
                        values.put(MediaStore.Images.Media.IS_PENDING, 0)
                        resolver.update(uri, values, null, null)
                    }
                    "Pictures/Nashira/$sanitized"
                }
            }
        }
    }
