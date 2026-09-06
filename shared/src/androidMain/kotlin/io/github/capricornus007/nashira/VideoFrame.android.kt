package io.github.capricornus007.nashira

import android.media.MediaDataSource
import android.media.MediaMetadataRetriever
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * MediaMetadataRetriever 直接吃記憶體位元組（MediaDataSource，API 23+），
 * 不必先落地成檔案。取第 0 微秒最接近的一格。
 */
actual fun decodeVideoFrame(bytes: ByteArray, maxDimension: Int): ImageBitmap? = runCatching {
    val retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(ByteArrayMediaDataSource(bytes))
        val frame = retriever.getFrameAtTime(0)
            ?: retriever.frameAtTime
            ?: return@runCatching null
        val longest = maxOf(frame.width, frame.height)
        if (maxDimension <= 0 || longest <= maxDimension) {
            frame.asImageBitmap()
        } else {
            val scale = maxDimension.toFloat() / longest
            android.graphics.Bitmap.createScaledBitmap(
                frame,
                (frame.width * scale).toInt().coerceAtLeast(1),
                (frame.height * scale).toInt().coerceAtLeast(1),
                true,
            ).asImageBitmap()
        }
    } finally {
        runCatching { retriever.release() }
    }
}.getOrNull()

private class ByteArrayMediaDataSource(private val data: ByteArray) : MediaDataSource() {
    override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
        if (position >= data.size) return -1
        val length = minOf(size.toLong(), data.size - position).toInt()
        data.copyInto(buffer, offset, position.toInt(), position.toInt() + length)
        return length
    }

    override fun getSize(): Long = data.size.toLong()

    override fun close() = Unit
}
