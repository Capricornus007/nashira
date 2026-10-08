package io.github.capricornus007.nashira

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.Dp
import de.connect2x.trixnity.client.MatrixClient
import io.github.capricornus007.nashira.matrix.MediaSource

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
    /** 用它向「活的」登入憑證要一條本機串流網址（見 [mediaStreamUrl]）。 */
    client: MatrixClient,
    /** 媒體來源：`mxc://` 可以直接串流；加密的那種一定要整檔解密後才播。 */
    source: MediaSource,
    /** 已經抓在手上的整份檔案（檢視器為了「下載」抓著它）；串流不通時用它落地再播。 */
    bytes: ByteArray?,
    /** 還解不出格時先墊這張（通常是後端給的封面或首格）。 */
    poster: ImageBitmap?,
    /** 畫面要塞進多大的框；解格時照它縮，避免整幀原解析度進記憶體。 */
    boxWidth: Dp,
    boxHeight: Dp,
    /**
     * 行內模式（時間線氣泡裡那種）：**靜音、迴圈、不畫控制列**，解格也降到 15fps。
     * 這是 Telegram X 的規則（`TGMessageVideo.java:112` 那個變數乾脆就叫
     * `mutedVideoFile`——影格解碼那條路根本不建立音軌），
     * 也順勢避開「多條同時有聲音」的衝突：聲音一律留給點開的全螢幕。
     */
    inline: Boolean = false,
    /**
     * 播放／暫停的外部開關：**每按一次把這個數 +1**，播放器在裡面翻轉播放狀態。
     * 走信號而不是回調，是因為 `playing` 是播放器自己的狀態，外面不該持有它。
     * 存在的理由：tdesktop 把 Space/Enter 收在**容器層**分派
     *（`media/view/media_view_overlay_widget.cpp:5584-5591`），我們的方向鍵也在容器層，
     * 所以「播完按空格重播」要有同一個入口（用戶 2026-10-08：「放完的視頻也不會正常按照按了空格就重放」）。
     */
    playToggleTick: Int = 0,
    modifier: Modifier = Modifier,
)
