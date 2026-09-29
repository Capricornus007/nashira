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

/**
 * 這個平台的軟鍵盤會不會「把佈局頂起來」（安卓會；桌面不會）。
 *
 * 桌面必須是 false：面板高度原本是照「輸入列被頂上去多少」量出來並存進設定的，
 * 桌面根本沒有鍵盤，那個數會被視窗被 i3 重新切尺寸之類的佈局位移誤寫成一个大數，
 * 然後長期沿用 → 窄窗口（半屏）時貼圖面板撐到快全屏（用戶 2026-09-29 點名 #67）。
 */
expect val softKeyboardShiftsComposer: Boolean
