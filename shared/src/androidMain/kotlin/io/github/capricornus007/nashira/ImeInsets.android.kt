package io.github.capricornus007.nashira

import android.os.Build
import android.view.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView

// 不 remember：rootWindowInsets 第一次組合時可能還是 null，記下來會永遠卡在 0。
// 這裡只在面板打開的那次重組讀一次，本身就是即取即用的屬性，沒有成本。
@Composable
internal actual fun systemImeHeightPx(): Int {
    if (Build.VERSION.SDK_INT < 30) return 0
    val insets = LocalView.current.rootWindowInsets ?: return 0
    // 會直接丟 IllegalArgumentException("Unable to query the maximum insets for IME")
    // 的 ROM 確實存在（realme UI 實測：鍵盤沒彈出來過就查不到），所以整段包起來。
    return runCatching {
        val ime = insets.getInsetsIgnoringVisibility(WindowInsets.Type.ime()).bottom
        val nav = insets.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars()).bottom
        // 面板放在 Scaffold 的 bottomBar 裡，那個位置本身已經被導航列抬高一次了，
        // 所以鍵盤高度要扣掉導航列，否則面板會比鍵盤高出整整一條導航列。
        (ime - nav).coerceAtLeast(0)
    }.getOrDefault(0)
}
