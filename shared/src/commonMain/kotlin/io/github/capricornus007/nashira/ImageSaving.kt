package io.github.capricornus007.nashira

import androidx.compose.runtime.Composable

/**
 * 把媒體位元組存到使用者可見的位置（下載）。
 *
 * Android 寫進 MediaStore（相簿）；桌面彈 JFileChooser 選路徑。
 * 回傳給使用者看的說明（例如存到哪裡），失敗時 Result.failure。
 */
@Composable
expect fun rememberImageSaver(): suspend (bytes: ByteArray, fileName: String, mimeType: String) -> Result<String>
