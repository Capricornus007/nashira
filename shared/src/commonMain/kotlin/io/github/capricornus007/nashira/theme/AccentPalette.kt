package io.github.capricornus007.nashira.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.materialkolor.ktx.from
import com.materialkolor.ktx.toneColor
import com.materialkolor.palettes.TonalPalette

/**
 * 色系基底：HCT 的色相與彩度就從這些顏色取（Material 慣用的 500 色）。
 * 宣告順序有講究——`accentPresets` 在初始化時就要讀它，写在後面會是 null。
 */
private val AccentFamilies = listOf(
    Color(0xFFF44336), // red
    Color(0xFFE91E63), // pink
    Color(0xFF9C27B0), // purple
    Color(0xFF673AB7), // deep purple
    Color(0xFF3F51B5), // indigo
    Color(0xFF2196F3), // blue
    Color(0xFF03A9F4), // light blue
    Color(0xFF00BCD4), // cyan
    Color(0xFF009688), // teal
    Color(0xFF4CAF50), // green
    Color(0xFF8BC34A), // light green
    Color(0xFFCDDC39), // lime
    Color(0xFFFFEB3B), // yellow
    Color(0xFFFFC107), // amber
    Color(0xFFFF9800), // orange
    Color(0xFFFF5722), // deep orange
    Color(0xFF795548), // brown
    Color(0xFF607D8B), // blue grey
    Color(0xFF9E9E9E), // grey
)

/** 深／中／淺三档明度（HCT tone）。 */
private val AccentTones = listOf(30, 50, 70)

/**
 * 色票（手選強調色）：19 個色系 × 3 個明度 = 57 顆。
 *
 * 明度一律**由 material-kolor 的 HCT `TonalPalette` 算**，不手寫色值：
 * 57 個數字靠記一定記錯，而且記錯不會報錯，只會變成一顆看著噁心的圓點。
 */
val accentPresets: List<Color> = AccentFamilies
    .flatMap { base ->
        val palette = TonalPalette.from(base)
        AccentTones.map { palette.toneColor(it) }
    }
    // 不同色系算出同一顆色是可能的（灰系列最容易），重複的圓點看起來像壞掉
    .distinctBy { it.toAccentHex() }

/**
 * 解析使用者輸入的色號：`#RGB` / `#RRGGBB`（大小寫不拘、`#` 可省）。
 * 其他一律回 null，呼叫端拿 null 去顯示錯誤、不套用。
 */
fun parseAccentHex(text: String): Color? {
    val body = text.trim().removePrefix("#")
    val hexChars = "0123456789abcdefABCDEF"
    val expanded = when (body.length) {
        3 -> if (body.all { it in hexChars }) body.map { "$it$it" }.joinToString("") else return null
        6 -> if (body.all { it in hexChars }) body else return null
        else -> return null
    }
    val rgb = (expanded.toULongOrNull(16) ?: return null).toInt()
    return Color(0xFF000000.toInt() or rgb)
}

/** 色號字串（`#RRGGBB` 大寫），存檔與輸入框顯示都用它。 */
fun Color.toAccentHex(): String {
    // 一律走 toArgb()：Compose 的 Color 把 ARGB 放在 ULong 的高 32 位，自己拆
    // `value and 0xFFFFFF` 會永遠得到 0（實測踩過：57 顆色票全變 #000000、去重後
    // 只剩一顆，點下去還把主題染成黑色種子生出的怪配色）。
    val rgb = (toArgb() and 0x00FFFFFF).toString(16).padStart(6, '0')
    return "#${rgb.uppercase()}"
}

/**
 * 舊版存的是 `accent` = 色系名稱（enum）。改色號制之後仍要讀得出同一顆色，
 * 所以留這張對照表（更新設定時迁移，不動使用者既有的主題）。
 */
internal fun legacyAccentHex(name: String?): String? = when (name) {
    "RED" -> "#F44336"
    "PINK" -> "#E91E63"
    "PURPLE" -> "#9C27B0"
    "DEEP_PURPLE" -> "#673AB7"
    "INDIGO" -> "#3F51B5"
    "BLUE" -> "#2196F3"
    "LIGHT_BLUE" -> "#03A9F4"
    "CYAN" -> "#00BCD4"
    "TEAL" -> "#009688"
    "GREEN" -> "#4CAF50"
    "LIME" -> "#CDDC39"
    "YELLOW" -> "#FFEB3B"
    "AMBER" -> "#FFC107"
    "ORANGE" -> "#FF9800"
    "BLUE_GREY" -> "#607D8B"
    "GREY" -> "#9E9E9E"
    else -> null
}
