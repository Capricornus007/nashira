package io.github.capricornus007.nashira.i18n

// 全語言骨架：先收 zh-TW / en，之後每加一門語言就是一個 object + 一行註冊，
// 規則：全語言都要翻、專有名詞不翻、文案要短。

enum class AppLanguage(val displayName: String, val tag: String) {
    ZH_TW("繁體中文", "zh-TW"),
    EN("English", "en"),
}

interface Strings {
    val appName: String
    val settings: String
    val back: String
    val allRooms: String
    val rooms: String
    val findOrStartConversation: String
    val syncingRooms: String
    val noRooms: String
    val privateMessage: String
    val accountAndSecurity: String
    val accountAndSecurityHint: String
    val search: String
    val members: String
    val more: String
    val roomBeginning: String
    val sendTo: String
    val add: String
    val send: String
    val syncStatus: String
    val initialSyncDone: String
    val verified: String
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
    val spaceIconMode: String
    val spaceAvatar: String
    val spaceRoomAvatars: String
    val restoringSession: String
    val updatingRooms: String
    val membersCount: String
    val loginUsername: String
    val homeserver: String
    val loginPassword: String
    val loginSubmit: String
    val logout: String
    val noRoomSelected: String
    val sendFailed: String
    val account: String
    val accountId: String
    val deviceId: String
    val security: String
    val deviceVerification: String
    val verifyThisDevice: String
    val verificationCreated: String
    val verificationState: String
    val noVerification: String
    val securityHint: String
    val verificationHint: String
    val targetDeviceId: String
    val targetDeviceIdExample: String
    val verificationFailed: String
    val logoutDevice: String
    val verificationDone: String
    val verificationInProgress: String
    val waitingAnotherDevice: String
    val acceptVerification: String
    val startSasVerification: String
    val acceptSas: String
    val match: String
    val noMatch: String
}

val StringsMap: Map<AppLanguage, Strings> = mapOf(
    AppLanguage.ZH_TW to ZhTwStrings,
    AppLanguage.EN to EnStrings,
)

fun stringsFor(language: AppLanguage): Strings = StringsMap[language] ?: EnStrings

object ZhTwStrings : Strings {
    override val appName = "Nashira"
    override val settings = "設定"
    override val back = "返回"
    override val allRooms = "全部聊天室"
    override val rooms = "聊天室"
    override val findOrStartConversation = "尋找或開始對話"
    override val syncingRooms = "正在同步房間…"
    override val noRooms = "目前沒有可顯示的房間"
    override val privateMessage = "私人訊息"
    override val accountAndSecurity = "帳戶與安全性"
    override val accountAndSecurityHint = "裝置、驗證與登出"
    override val search = "搜尋"
    override val members = "成員"
    override val more = "更多"
    override val roomBeginning = "這裡是 %s 的開始。"
    override val sendTo = "傳送訊息至 %s"
    override val add = "新增"
    override val send = "傳送"
    override val syncStatus = "同步狀態"
    override val initialSyncDone = "已完成初始同步"
    override val verified = "已驗證"
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
    override val spaceIconMode = "Space 圖示"
    override val spaceAvatar = "顯示 Space 頭像"
    override val spaceRoomAvatars = "顯示子房間頭像預覽"
    override val restoringSession = "正在載入本機資料…"
    override val updatingRooms = "正在更新訊息…"
    override val membersCount = "成員 · %d"
    override val loginUsername = "用戶名"
    override val homeserver = "Homeserver"
    override val loginPassword = "密碼"
    override val loginSubmit = "登入"
    override val logout = "登出"
    override val noRoomSelected = "選擇一個房間開始"
    override val sendFailed = "發送失敗"
    override val account = "帳戶"
    override val accountId = "帳戶 ID"
    override val deviceId = "裝置 ID"
    override val security = "安全性"
    override val deviceVerification = "裝置驗證"
    override val verifyThisDevice = "要求驗證此裝置"
    override val verificationCreated = "驗證工作階段已建立，請在另一個裝置確認。"
    override val verificationState = "驗證狀態"
    override val noVerification = "目前沒有進行中的驗證。"
    override val securityHint = "Nashira 使用 Trixnity vodozemac。驗證後才會把此裝置標記為可信，並用於解密歷史訊息。"
    override val verificationHint = "用另一個已登入的 Matrix 裝置確認 SAS，驗證事件會透過加密的 to-device 通道傳送。"
    override val targetDeviceId = "要驗證的裝置 ID"
    override val targetDeviceIdExample = "例如 ABCDEFGHIJ"
    override val verificationFailed = "驗證要求失敗"
    override val logoutDevice = "登出此裝置"
    override val verificationDone = "此裝置已完成驗證。"
    override val verificationInProgress = "驗證工作階段進行中：%s"
    override val waitingAnotherDevice = "等待另一個裝置"
    override val acceptVerification = "接受驗證要求"
    override val startSasVerification = "開始 SAS 驗證"
    override val acceptSas = "接受 SAS"
    override val match = "相符"
    override val noMatch = "不相符"
}

object EnStrings : Strings {
    override val appName = "Nashira"
    override val settings = "Settings"
    override val back = "Back"
    override val allRooms = "All rooms"
    override val rooms = "Rooms"
    override val findOrStartConversation = "Find or start a conversation"
    override val syncingRooms = "Syncing rooms…"
    override val noRooms = "No rooms to show"
    override val privateMessage = "Direct message"
    override val accountAndSecurity = "Account and security"
    override val accountAndSecurityHint = "Devices, verification, and sign out"
    override val search = "Search"
    override val members = "Members"
    override val more = "More"
    override val roomBeginning = "This is the beginning of %s."
    override val sendTo = "Send a message to %s"
    override val add = "Add"
    override val send = "Send"
    override val syncStatus = "Sync status"
    override val initialSyncDone = "Initial sync complete"
    override val verified = "Verified"
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
    override val spaceIconMode = "Space icon"
    override val spaceAvatar = "Show the Space avatar"
    override val spaceRoomAvatars = "Show child-room avatar previews"
    override val restoringSession = "Loading local data…"
    override val updatingRooms = "Updating messages…"
    override val membersCount = "Members · %d"
    override val loginUsername = "Username"
    override val homeserver = "Homeserver"
    override val loginPassword = "Password"
    override val loginSubmit = "Log in"
    override val logout = "Log out"
    override val noRoomSelected = "Select a room to start"
    override val sendFailed = "Send failed"
    override val account = "Account"
    override val accountId = "Account ID"
    override val deviceId = "Device ID"
    override val security = "Security"
    override val deviceVerification = "Device verification"
    override val verifyThisDevice = "Verify this device"
    override val verificationCreated = "Verification session created. Confirm it on another device."
    override val verificationState = "Verification state"
    override val noVerification = "No verification is currently in progress."
    override val securityHint = "Nashira uses Trixnity vodozemac. Verification marks this device as trusted for decrypting message history."
    override val verificationHint = "Confirm the SAS from another signed-in Matrix device. Verification events use the encrypted to-device channel."
    override val targetDeviceId = "Device ID to verify"
    override val targetDeviceIdExample = "For example ABCDEFGHIJ"
    override val verificationFailed = "Verification request failed"
    override val logoutDevice = "Sign out this device"
    override val verificationDone = "This device is verified."
    override val verificationInProgress = "Verification in progress: %s"
    override val waitingAnotherDevice = "Waiting for another device"
    override val acceptVerification = "Accept verification request"
    override val startSasVerification = "Start SAS verification"
    override val acceptSas = "Accept SAS"
    override val match = "Matches"
    override val noMatch = "Does not match"
}
