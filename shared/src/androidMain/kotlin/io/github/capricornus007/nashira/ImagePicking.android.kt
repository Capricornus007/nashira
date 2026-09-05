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
