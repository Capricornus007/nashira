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

    fun label(language: AppLanguage): String = when (language) {
        AppLanguage.ZH_TW -> labelZh
        AppLanguage.EN -> labelEn
        AppLanguage.JA -> JapaneseLabels[name] ?: labelEn
        AppLanguage.KO -> KoreanLabels[name] ?: labelEn
    }

    private companion object {
        val JapaneseLabels = mapOf(
            "PINK" to "ピンク", "RED" to "赤", "ORANGE" to "オレンジ", "AMBER" to "アンバー",
            "YELLOW" to "黄", "LIME" to "ライム", "GREEN" to "緑", "TEAL" to "ティール",
            "CYAN" to "シアン", "LIGHT_BLUE" to "ライトブルー", "BLUE" to "青", "INDIGO" to "インディゴ",
            "PURPLE" to "紫", "DEEP_PURPLE" to "濃い紫", "BLUE_GREY" to "ブルーグレー", "GREY" to "グレー",
        )
        val KoreanLabels = mapOf(
            "PINK" to "분홍색", "RED" to "빨간색", "ORANGE" to "주황색", "AMBER" to "호박색",
            "YELLOW" to "노란색", "LIME" to "라임색", "GREEN" to "초록색", "TEAL" to "청록색",
            "CYAN" to "시안색", "LIGHT_BLUE" to "하늘색", "BLUE" to "파란색", "INDIGO" to "남색",
            "PURPLE" to "보라색", "DEEP_PURPLE" to "진한 보라색", "BLUE_GREY" to "청회색", "GREY" to "회색",
        )
    }
}
