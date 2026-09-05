package io.github.capricornus007.nashira

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.capricornus007.nashira.appContext

@Composable
actual fun rememberImagePickerLauncher(
    onPicked: (PickedImage) -> Unit,
): (() -> Unit)? {
    val context = appContext ?: return null
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        val picked = readImage(context, uri) ?: return@rememberLauncherForActivityResult
        onPicked(picked)
    }
    return remember(launcher) { { launcher.launch("image/*") } }
}

private fun readImage(context: android.content.Context, uri: Uri): PickedImage? = runCatching {
    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
    val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opts)
    val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
    PickedImage(
        bytes = bytes,
        mimeType = mimeType,
        fileName = uri.lastPathSegment ?: "image",
        width = opts.outWidth.takeIf { it > 0 },
        height = opts.outHeight.takeIf { it > 0 },
    )
}.getOrNull()

@Composable
actual fun rememberFilePickerLauncher(
    onPicked: (PickedFile) -> Unit,
): (() -> Unit)? {
    val context = appContext ?: return null
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        val picked = readFile(context, uri) ?: return@rememberLauncherForActivityResult
        onPicked(picked)
    }
    return remember(launcher) { { launcher.launch("*/*") } }
}

/** SAF 的 uri 不一定有檔名，先問 DISPLAY_NAME 再退回 lastPathSegment。 */
private fun readFile(context: android.content.Context, uri: Uri): PickedFile? = runCatching {
    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
    val name = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        if (index >= 0 && cursor.moveToFirst()) cursor.getString(index) else null
    }
    PickedFile(
        bytes = bytes,
        mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream",
        fileName = name ?: uri.lastPathSegment ?: "file",
    )
}.getOrNull()
