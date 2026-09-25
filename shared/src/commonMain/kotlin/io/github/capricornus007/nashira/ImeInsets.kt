package io.github.capricornus007.nashira

import androidx.compose.runtime.Composable

/**
 * 輸入法鍵盤的高度（畫素，已扣掉導航列），**鍵盤沒彈出來也問得到**。
 *
 * 貼圖面板要和鍵盤等高，光靠「量輸入列被頂上去多少」得先見過鍵盤一次；第一次開面板
 * 時那個量還是 0，面板就比鍵盤矮一大截（用戶拿實機點名過）。安卓端問系統要 ime 的
 * ignoring-visibility 內距（realme UI 實測：鍵盤沒彈出來過會直接丟例外，那種情況回 0），
 * 其他平台也回 0，呼叫端自己退回預估值。
 */
@Composable
internal expect fun systemImeHeightPx(): Int
