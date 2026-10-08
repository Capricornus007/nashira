package io.github.capricornus007.nashira

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.Dp

/**
 * 全螢幕檢視器裡的**內嵌影片播放**（Telegram／Discord 式：圖片與影片共用同一個殼，
 * 控制列疊在畫面上，不另開外部視窗）。
 *
 * 使用者 2026-10-08 的原話：「爲什麼要變成點開 mpv 窗口啊……我點好幾次它延遲好幾秒
 * 然後點開好幾個窗口，就不能弄的跟 tg discord 那種嗎？點開圖片視頻都是有統一 ui 的內置」。
 *
 * 桌面實作走 ffmpeg 解格＋JVM 自己的音訊輸出，不加任何依賴、也不捆 FFmpeg 函式庫
 * （那顆 jar 解開 129MB，量過、淘汰）。
 * 其他平台這輪先顯示首格（手機該走系統內建的播放器，列在待辦 #96）。
 */
@Composable
expect fun EmbeddedVideoPlayer(
    /**
     * 可直接串流的公網網址（`mxc://` 換成的 download URL），有就優先用它：
     * 點開立刻能播，不用等整檔下載完（用戶 2026-10-08「憑什麼視頻非得那麼久」）。
     */
    url: String?,
    /** 已經抓在手上的整份檔案（檢視器為了「下載」抓著它）；[url] 不可用時用它落地再播。 */
    bytes: ByteArray?,
    /** 還解不出格時先墊這張（通常是後端給的封面或首格）。 */
    poster: ImageBitmap?,
    /** 畫面要塞進多大的框；解格時照它縮，避免整幀原解析度進記憶體。 */
    boxWidth: Dp,
    boxHeight: Dp,
    modifier: Modifier = Modifier,
)
