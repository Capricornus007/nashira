package io.github.capricornus007.nashira.theme

import androidx.compose.ui.graphics.Color
import io.github.capricornus007.nashira.i18n.AppLanguage

/**
 * 主題顏色（種子色覆寫）：Material 經典 16 色系。
 * 選擇後以該色為種子生成完整配色（Material You 管線，兩端可用）；
 * null（預設）= Android 動態取色時跟隨桌布種子，否則用品牌 Arcaea 配色。
 */
enum class ThemeAccent(
    val color: Color,
    private val labelZh: String,
    private val labelEn: String,
) {
    PINK(Color(0xFFE91E63), "粉紅色", "Pink"),
    RED(Color(0xFFF44336), "紅色", "Red"),
    ORANGE(Color(0xFFFF9800), "橙色", "Orange"),
    AMBER(Color(0xFFFFC107), "琥珀色", "Amber"),
    YELLOW(Color(0xFFFFEB3B), "黃色", "Yellow"),
    LIME(Color(0xFFCDDC39), "萊姆色", "Lime"),
    GREEN(Color(0xFF4CAF50), "綠色", "Green"),
    TEAL(Color(0xFF009688), "青色", "Teal"),
    CYAN(Color(0xFF00BCD4), "藍綠色", "Cyan"),
    LIGHT_BLUE(Color(0xFF03A9F4), "淺藍色", "Light blue"),
    BLUE(Color(0xFF2196F3), "藍色", "Blue"),
    INDIGO(Color(0xFF3F51B5), "靛藍色", "Indigo"),
    PURPLE(Color(0xFF9C27B0), "紫色", "Purple"),
    DEEP_PURPLE(Color(0xFF673AB7), "深紫色", "Deep purple"),
    BLUE_GREY(Color(0xFF607D8B), "藍灰色", "Blue grey"),
    GREY(Color(0xFF9E9E9E), "灰色", "Grey");

    fun label(language: AppLanguage): String = if (language == AppLanguage.ZH_TW) labelZh else labelEn
}
