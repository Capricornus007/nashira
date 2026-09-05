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
    val sticker: String
    val stickerEmpty: String
    val notifImage: String
    val notifSticker: String
    val notifUndecryptable: String
    val sendImage: String
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
    val chatList: String
    val personalization: String
    val appearanceHint: String
    val chatListHint: String
    val unreadIndicators: String
    val unreadIndicatorsHint: String
    val messagePreview: String
    val messagePreviewHint: String
    val stickerPanelPosition: String
    val stickerPanelAbove: String
    val stickerPanelBelow: String
    val sessionLogoutViaAccountPage: String
    val messageSendFailed: String
    val attachPhoto: String
    val attachSticker: String
    val attachTitle: String
    val attachFile: String
    val attach: String
    val spaceHome: String
    val notifications: String
    val backgroundSync: String
    val backgroundSyncHint: String
    val actionReply: String
    val actionCopyText: String
    val actionCopyLink: String
    val actionDelete: String
    val actionMarkUnread: String
    val actionFavourite: String
    val actionLowPriority: String
    val actionInvite: String
    val actionLeave: String
    val inviteHint: String
    val copiedToClipboard: String
    val replyingTo: String
    val verificationIncoming: String
    val verificationIncomingHint: String
    val cancelVerification: String
    val membersCount: String
    val imageMessage: String
    val stickerMessage: String
    val undecryptable: String
    val clearSearch: String
    val noSearchResults: String
    val showSecret: String
    val hideSecret: String
    val acceptInvite: String
    val declineInvite: String
    val invited: String
    val loadMore: String
    val loadingMore: String
    val verificationDoneHint: String
    val verificationLoading: String
    val verificationNotReady: String
    val verificationCancelled: String
    val verificationInProgressShort: String
    val verificationWaitingOtherDevice: String
    val deviceUnverified: String
    val deviceUnverifiedHint: String
    val verifyWithRecoveryKey: String
    val verifyWithRecoveryKeyHint: String
    val verifyWithPassphrase: String
    val verifyWithPassphraseHint: String
    val verifyWithOtherDevice: String
    val verifyWithOtherDeviceHint: String
    val crossSigningMissing: String
    val crossSigningMissingHint: String
    val bootstrapCrossSigning: String
    val bootstrapCrossSigningHint: String
    val bootstrapFailed: String
    val recoveryKey: String
    val passphrase: String
    val recoveryKeyCreated: String
    val recoveryKeyCreatedHint: String
    val recoveryKeySaved: String
    val compareEmojiHint: String
    val sessions: String
    val sessionsLoadFailed: String
    val sessionLogoutFailed: String
    val sessionVerified: String
    val sessionUnverified: String
    val sessionBlocked: String
    val sessionUnknown: String
    val sessionCurrent: String
    val verifySession: String
    val logoutSession: String
    val cancel: String
    val loginUsername: String
    val today: String
    val yesterday: String
    val justNow: String
    val minutesAgo: String
    val hoursAgo: String
    val daysAgo: String
    val monthsAgo: String
    val yearsAgo: String
    /** 日期分隔線的完整日期，各語言自行排列年月日 */
    fun formatDate(year: Int, month: Int, day: Int): String
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
    override val sticker = "貼圖"
    override val stickerEmpty = "還沒有貼圖包"
    override val notifImage = "[圖片]"
    override val notifSticker = "[貼圖]"
    override val notifUndecryptable = "[無法解密]"
    override val sendImage = "發送圖片"
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
    override val chatList = "聊天室清單"
    override val unreadIndicators = "未讀提示"
    override val unreadIndicatorsHint = "側欄顯示未讀白條，清單顯示紅圈數字"
    override val messagePreview = "訊息預覽"
    override val personalization = "個人化"
    override val appearanceHint = "主題模式、動態顏色與色板"
    override val chatListHint = "清單樣式、未讀提示、訊息預覽與貼圖面板"
    override val messagePreviewHint = "清單第二行顯示最後一則訊息"
    override val stickerPanelPosition = "貼圖面板位置"
    override val stickerPanelAbove = "浮在輸入列上方，不推動輸入列"
    override val stickerPanelBelow = "釘在輸入列下方"
    override val sessionLogoutViaAccountPage = "此伺服器改由帳戶管理頁登出裝置，已在瀏覽器開啟"
    override val messageSendFailed = "送出失敗"
    override val attachPhoto = "照片"
    override val attachSticker = "貼圖"
    override val attachTitle = "傳送"
    override val attachFile = "檔案"
    override val attach = "附件"
    override val spaceHome = "聊天空間首頁"
    override val notifications = "通知"
    override val backgroundSync = "背景同步"
    override val backgroundSyncHint = "常駐連線，離開 app 也收得到新訊息通知"
    override val actionReply = "回覆"
    override val actionCopyText = "複製文字"
    override val actionCopyLink = "複製連結"
    override val actionDelete = "刪除訊息"
    override val actionMarkUnread = "標記為未讀"
    override val actionFavourite = "收藏"
    override val actionLowPriority = "低優先"
    override val actionInvite = "邀請"
    override val actionLeave = "離開"
    override val inviteHint = "輸入要邀請的 Matrix ID（@user:server）"
    override val copiedToClipboard = "已複製"
    override val replyingTo = "回覆 %s"
    override val verificationIncoming = "工作階段驗證請求"
    override val verificationIncomingHint = "另一個工作階段要求驗證。接受後兩邊會比對一組表情符號。"
    override val cancelVerification = "取消驗證"
    override val specM3 = "Material 3 (2021)"
    override val specExpressive = "Expressive (2025)"
    override val themeColor = "主題顏色"
    override val themeColorDefault = "預設"
    override val spaceIconMode = "Space 圖示"
    override val spaceAvatar = "顯示 Space 頭像"
    override val spaceRoomAvatars = "顯示子房間頭像預覽"
    override val restoringSession = "正在載入本機資料…"
    override val membersCount = "成員 · %d"
    override val imageMessage = "圖片"
    override val stickerMessage = "貼圖"
    override val undecryptable = "⚠ 這則訊息無法解密"
    override val clearSearch = "清除搜尋"
    override val noSearchResults = "找不到符合的聊天室"
    override val showSecret = "顯示內容"
    override val hideSecret = "隱藏內容"
    override val acceptInvite = "接受邀請"
    override val declineInvite = "拒絕"
    override val invited = "邀請你加入"
    override val loadMore = "載入更早的訊息"
    override val loadingMore = "正在載入…"
    override val verificationDoneHint = "此裝置已交叉簽署，其他客戶端會顯示為已驗證。"
    override val verificationLoading = "正在讀取驗證狀態…"
    override val verificationNotReady = "尚未就緒"
    override val verificationCancelled = "驗證已取消"
    override val verificationInProgressShort = "驗證進行中…"
    override val verificationWaitingOtherDevice = "已發出請求，請在另一台裝置確認。"
    override val deviceUnverified = "此裝置尚未驗證"
    override val deviceUnverifiedHint = "驗證後才能讀取加密歷史訊息，其他客戶端也不再顯示警告。"
    override val verifyWithRecoveryKey = "用復原金鑰驗證"
    override val verifyWithRecoveryKeyHint = "貼上建立帳戶安全備份時取得的金鑰"
    override val verifyWithPassphrase = "用安全密語驗證"
    override val verifyWithPassphraseHint = "輸入設定安全備份時自訂的密語"
    override val verifyWithOtherDevice = "用另一台裝置驗證"
    override val verifyWithOtherDeviceHint = "在已驗證的裝置上比對表情符號"
    override val crossSigningMissing = "帳戶尚未啟用交叉簽署"
    override val crossSigningMissingHint = "先在此裝置初始化，會產生一次性的復原金鑰。"
    override val bootstrapCrossSigning = "建立交叉簽署與安全備份"
    override val bootstrapCrossSigningHint = "產生復原金鑰並簽署此裝置"
    override val bootstrapFailed = "建立交叉簽署失敗"
    override val recoveryKey = "復原金鑰"
    override val passphrase = "安全密語"
    override val recoveryKeyCreated = "已建立復原金鑰"
    override val recoveryKeyCreatedHint = "請立刻抄下來並妥善保存——關閉後就看不到了。"
    override val recoveryKeySaved = "我已保存"
    override val compareEmojiHint = "確認兩台裝置顯示的表情符號完全相同。"
    override val sessions = "工作階段"
    override val sessionsLoadFailed = "無法讀取工作階段清單"
    override val sessionLogoutFailed = "登出此工作階段失敗"
    override val sessionVerified = "已驗證"
    override val sessionUnverified = "未驗證"
    override val sessionBlocked = "已封鎖"
    override val sessionUnknown = "狀態未知"
    override val sessionCurrent = "目前裝置"
    override val verifySession = "驗證此裝置"
    override val logoutSession = "登出"
    override val cancel = "取消"
    override val loginUsername = "用戶名"
    override val homeserver = "Homeserver"
    override val loginPassword = "密碼"
    override val loginSubmit = "登入"
    override val today = "今天"
    override val yesterday = "昨天"
    override val justNow = "剛剛"
    override val minutesAgo = "%d 分鐘前"
    override val hoursAgo = "%d 小時前"
    override val daysAgo = "%d 天前"
    override val monthsAgo = "%d 個月前"
    override val yearsAgo = "%d 年前"
    override fun formatDate(year: Int, month: Int, day: Int) = "${year} 年 ${month} 月 ${day} 日"
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
    override val verificationHint = "用另一個已登入 Nashira 或其他 Matrix 客戶端的裝置確認 SAS，驗證事件會透過加密的 to-device 通道傳送。"
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
    override val sticker = "Stickers"
    override val stickerEmpty = "No sticker packs yet"
    override val notifImage = "[Image]"
    override val notifSticker = "[Sticker]"
    override val notifUndecryptable = "[Undecryptable]"
    override val sendImage = "Send image"
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
    override val membersCount = "Members · %d"
    override val imageMessage = "Image"
    override val stickerMessage = "Sticker"
    override val undecryptable = "⚠ Unable to decrypt this message"
    override val clearSearch = "Clear search"
    override val noSearchResults = "No matching rooms"
    override val showSecret = "Show"
    override val hideSecret = "Hide"
    override val acceptInvite = "Accept"
    override val declineInvite = "Decline"
    override val invited = "Invited you"
    override val loadMore = "Load earlier messages"
    override val loadingMore = "Loading…"
    override val verificationDoneHint = "This device is cross-signed and shows as verified elsewhere."
    override val verificationLoading = "Loading verification state…"
    override val verificationNotReady = "Not ready yet"
    override val verificationCancelled = "Verification cancelled"
    override val verificationInProgressShort = "Verification in progress…"
    override val verificationWaitingOtherDevice = "Request sent. Confirm it on the other device."
    override val deviceUnverified = "This device is not verified"
    override val deviceUnverifiedHint = "Verify to read encrypted history and clear warnings in other clients."
    override val verifyWithRecoveryKey = "Verify with recovery key"
    override val verifyWithRecoveryKeyHint = "Paste the key from your account's secure backup"
    override val verifyWithPassphrase = "Verify with security passphrase"
    override val verifyWithPassphraseHint = "Enter the passphrase you set for secure backup"
    override val verifyWithOtherDevice = "Verify with another device"
    override val verifyWithOtherDeviceHint = "Compare emoji on an already verified device"
    override val crossSigningMissing = "Cross-signing is not set up"
    override val crossSigningMissingHint = "Set it up here first; a one-time recovery key will be generated."
    override val bootstrapCrossSigning = "Set up cross-signing and backup"
    override val bootstrapCrossSigningHint = "Generates a recovery key and signs this device"
    override val bootstrapFailed = "Failed to set up cross-signing"
    override val recoveryKey = "Recovery key"
    override val passphrase = "Security passphrase"
    override val recoveryKeyCreated = "Recovery key created"
    override val recoveryKeyCreatedHint = "Write it down now and keep it safe — it cannot be shown again."
    override val recoveryKeySaved = "I saved it"
    override val compareEmojiHint = "Check that both devices show exactly the same emoji."
    override val sessions = "Sessions"
    override val sessionsLoadFailed = "Could not load sessions"
    override val sessionLogoutFailed = "Could not sign out this session"
    override val sessionVerified = "Verified"
    override val sessionUnverified = "Unverified"
    override val sessionBlocked = "Blocked"
    override val sessionUnknown = "Unknown"
    override val sessionCurrent = "This device"
    override val verifySession = "Verify"
    override val logoutSession = "Sign out"
    override val cancel = "Cancel"
    override val loginUsername = "Username"
    override val chatList = "Chat list"
    override val unreadIndicators = "Unread indicators"
    override val unreadIndicatorsHint = "Show the unread bar in the rail and count badges in the list"
    override val messagePreview = "Message preview"
    override val messagePreviewHint = "Show the latest message on the list's second line"
    override val stickerPanelPosition = "Sticker panel position"
    override val stickerPanelAbove = "Floats above the composer"
    override val stickerPanelBelow = "Docks below the composer"
    override val sessionLogoutViaAccountPage = "This server logs out devices on its account page; opened in your browser"
    override val messageSendFailed = "Failed to send"
    override val attachPhoto = "Photo"
    override val attachSticker = "Sticker"
    override val attachTitle = "Send"
    override val attachFile = "File"
    override val attach = "Attach"
    override val spaceHome = "Space home"
    override val notifications = "Notifications"
    override val backgroundSync = "Background sync"
    override val backgroundSyncHint = "Keeps the connection open so notifications arrive in the background"
    override val actionReply = "Reply"
    override val actionCopyText = "Copy text"
    override val actionCopyLink = "Copy link"
    override val actionDelete = "Remove message"
    override val actionMarkUnread = "Mark as unread"
    override val actionFavourite = "Favourite"
    override val actionLowPriority = "Low priority"
    override val actionInvite = "Invite"
    override val actionLeave = "Leave"
    override val inviteHint = "Enter the Matrix ID to invite (@user:server)"
    override val copiedToClipboard = "Copied"
    override val replyingTo = "Replying to %s"
    override val verificationIncoming = "Session verification request"
    override val verificationIncomingHint = "Another session asked to verify. After accepting, both sides compare a set of emoji."
    override val cancelVerification = "Cancel verification"
    override val personalization = "Personalization"
    override val appearanceHint = "Theme mode, dynamic color, and palette"
    override val chatListHint = "List style, unread indicators, previews, and sticker panel"
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
    override val today = "Today"
    override val yesterday = "Yesterday"
    override val justNow = "Just now"
    override val minutesAgo = "%d min ago"
    override val hoursAgo = "%d h ago"
    override val daysAgo = "%d d ago"
    override val monthsAgo = "%d mo ago"
    override val yearsAgo = "%d y ago"
    override fun formatDate(year: Int, month: Int, day: Int) = "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
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
