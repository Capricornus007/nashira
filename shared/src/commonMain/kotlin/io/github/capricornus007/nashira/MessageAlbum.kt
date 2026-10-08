package io.github.capricornus007.nashira

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.connect2x.trixnity.client.MatrixClient
import io.github.capricornus007.nashira.matrix.MediaSource
import io.github.capricornus007.nashira.matrix.MessageBody

/**
 * 相簿區塊比單張寬一點點：單張上限 264.dp 是為了不擠掉發送者名字，
 * 但三欄時 264 会让每格只剩 85dp、根本看不出內容，所以放到 320.dp；再寬就頂到同一條限制。
 */
private val AlbumBlockWidth = 320.dp
private val AlbumCellGap = 2.dp

/**
 * 每列幾格。規則照 tdesktop 的 `ui/grouped_layout.cpp`：
 * 兩張＝併排（兩張都很扁時改成直排，原碼對 wide 就是往下堆）；三、四張＝兩欄；
 * 五張以上＝三欄；**一組最多 10 張**（`history_view_media_grouped.h:22 kMaxSize = 10`）。
 */
internal fun albumRows(items: List<MessageBody.Image>): List<Int> {
    val n = items.size
    if (n <= 1) return listOf(n)
    if (n == 2) {
        val bothWide = items.all { it.ratioForLayout() > 1.7f }
        return if (bothWide) listOf(1, 1) else listOf(2)
    }
    if (n <= 4) return if (n == 3) listOf(2, 1) else listOf(2, 2)
    val rowCount = (n + 2) / 3
    val base = n / rowCount
    val rem = n % rowCount
    return (0 until rowCount).map { if (it < rem) base + 1 else base }
}

/** 事件沒帶長寬時一律當 1:1 排，先讓版位站穩（載到圖之後也不會跳太多）。 */
private fun MessageBody.Image.ratioForLayout(): Float {
    val w = width?.toFloat() ?: 0f
    val h = height?.toFloat() ?: 0f
    if (w <= 0f || h <= 0f) return 1f
    // tdesktop 把比例摺進 [1/1.5, 2.75]（`ComplexLayouter`，原碼 444 行）：
    // 不然一張長條圖會把整列壓到看不見。
    return (w / h).coerceIn(1f / 1.5f, 2.75f)
}

/** 一列的高度：整列寬度除以「這列各格長寬比之和」，就是讓每格都剛好塞滿又等高的那個高度。 */
private fun albumRowHeight(row: List<MessageBody.Image>, columns: Int): androidx.compose.ui.unit.Dp {
    val sum = row.sumOf { it.ratioForLayout().toDouble() }.toFloat()
    if (sum <= 0f) return 96.dp
    val available = AlbumBlockWidth - AlbumCellGap * (columns - 1).coerceAtLeast(0)
    return (available / sum).coerceIn(64.dp, 200.dp)
}

/** 時間線裡的相簿區塊：整組共用一則說明文字、圖片併排。
 *
 * 每格按自己的長寬比拿權重，同一列因此**自動等高**——這正是 Telegram 格子的算法
 * （列內等高、超出部分裁切），所以不必寫死任何高度。點任一格都開全螢幕檢視器，
 * 並從那一格開始連翻整組。
 */
@Composable
internal fun MessageAlbum(
    client: MatrixClient,
    album: MessageBody.Album,
    strings: io.github.capricornus007.nashira.i18n.Strings,
    onOpen: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ui = LocalUiState.current
    Column(modifier.widthIn(max = AlbumBlockWidth), verticalArrangement = Arrangement.spacedBy(AlbumCellGap)) {
        val columns = albumRows(album.items).maxOrNull() ?: 1
        var offset = 0
        albumRows(album.items).forEach { rowSize ->
            val row = album.items.subList(offset, (offset + rowSize).coerceAtMost(album.items.size))
            val first = offset
            offset += row.size
            val height = albumRowHeight(row, columns)
            Row(horizontalArrangement = Arrangement.spacedBy(AlbumCellGap)) {
                row.forEachIndexed { col, item ->
                    val mxc: String? = when (val s = item.source) {
                        is MediaSource.Plain -> s.mxcUrl
                        is MediaSource.Encrypted -> s.file.url
                    }
                    val hidden = mxc != null && mxc in ui.hiddenMedia
                    Box(Modifier.weight(item.ratioForLayout()).height(height)) {
                        MessageImage(
                            client = client,
                            source = item.source,
                            width = item.width,
                            height = item.height,
                            isSticker = false,
                            caption = item.caption,
                            mimeType = item.mimeType,
                            // 相簿裡的影片只給封面＋播放鈕：整組一起連播會把頻寬吃光
                            //（他這條鏈路實測 150–280KB/s、影片本身 570KB/s），
                            // Telegram 的相簿格也不自動播。
                            durationMs = null,
                            sizeBytes = null,
                            insideAlbumCell = true,
                            onOpen = if (hidden) {
                                { if (mxc != null) ui.hiddenMedia = ui.hiddenMedia - mxc }
                            } else {
                                { onOpen(first + col) }
                            },
                            hiddenLabel = if (hidden) strings.hiddenImage else null,
                        )
                    }
                }
            }
        }
        if (album.caption.isNotBlank()) {
            // 整組一則說明、摆在格子下面——Telegram 就是這個位置（不是每張各配一份）。
            Text(
                text = pangu(album.caption),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
