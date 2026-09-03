package io.github.capricornus007.nashira.i18n

// 全語言骨架：先收 zh-TW / en，之後每加一門語言就是一個 object + 一行註冊，
// 規則：全語言都要翻、專有名詞不翻、文案要短。

enum class AppLanguage(val displayName: String, val tag: String) {
    ZH_TW("繁體中文", "zh-TW"),
    EN("English", "en"),
}

interface Strings {
    val appName: String
    val tagline: String
    val appearance: String
    val darkTheme: String
    val lightTheme: String
    val language: String
    val about: String
    val version: String
    val engine: String
    val encryption: String
    val license: String
    val sourceCode: String
    val themeMode: String
    val followSystem: String
    val dynamicColor: String
    val dynamicColorHint: String
    val paletteStyle: String
    val expressive: String
    val colorSpec: String
    val specM3: String
    val specExpressive: String
    val themeColor: String
    val themeColorDefault: String
}

val StringsMap: Map<AppLanguage, Strings> = mapOf(
    AppLanguage.ZH_TW to ZhTwStrings,
    AppLanguage.EN to EnStrings,
)

fun stringsFor(language: AppLanguage): Strings = StringsMap[language] ?: EnStrings

object ZhTwStrings : Strings {
    override val appName = "Nashira"
    override val tagline = "γ Capricorni · 報佳音之星"
    override val appearance = "外觀"
    override val darkTheme = "深色"
    override val lightTheme = "淺色"
    override val language = "語言"
    override val about = "關於"
    override val version = "版本"
    override val engine = "引擎"
    override val encryption = "加密"
    override val license = "授權條款"
    override val sourceCode = "原始碼"
    override val themeMode = "主題模式"
    override val followSystem = "追隨系統主題"
    override val dynamicColor = "動態顏色"
    override val dynamicColorHint = "基於桌布的主題顏色（Material You）"
    override val paletteStyle = "調色盤樣式"
    override val expressive = "Expressive"
    override val colorSpec = "顏色規格"
    override val specM3 = "Material 3 (2021)"
    override val specExpressive = "Expressive (2025)"
    override val themeColor = "主題顏色"
    override val themeColorDefault = "預設"
}

object EnStrings : Strings {
    override val appName = "Nashira"
    override val tagline = "γ Capricorni, the bringer of good news"
    override val appearance = "Appearance"
    override val darkTheme = "Dark"
    override val lightTheme = "Light"
    override val language = "Language"
    override val about = "About"
    override val version = "Version"
    override val engine = "Engine"
    override val encryption = "Encryption"
    override val license = "License"
    override val sourceCode = "Source code"
    override val themeMode = "Theme mode"
    override val followSystem = "Follow system theme"
    override val dynamicColor = "Dynamic color"
    override val dynamicColorHint = "Wallpaper-based theme colors (Material You)"
    override val paletteStyle = "Palette style"
    override val expressive = "Expressive"
    override val colorSpec = "Color spec"
    override val specM3 = "Material 3 (2021)"
    override val specExpressive = "Expressive (2025)"
    override val themeColor = "Theme color"
    override val themeColorDefault = "Default"
}
