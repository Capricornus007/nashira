package io.github.capricornus007.nashira.i18n

// 全語言骨架：先收 zh-TW / en，之後每加一門語言就是一個 object + 一行註冊，
// 規則：全語言都要翻、專有名詞不翻、文案要短。

enum class AppLanguage(val displayName: String, val tag: String) {
    ZH_TW("繁體中文（台灣）", "zh-TW"),
    ZH_HK("繁體中文（香港）", "zh-HK"),
    ZH_CN("简体中文", "zh-CN"),
    EN("English", "en"),
    JA("日本語", "ja"),
    KO("한국어", "ko"),
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
    val searchMessages: String
    val searchMessagesHint: String
    val noMessageSearchResults: String
    val messageSearchFailed: String
    val publicRoomsLoadFailed: String
    val joinRoomFailed: String
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
    val typingOne: String
    val typingTwo: String
    val typingMany: String
    val readByCount: String
    val actionEdit: String
    val editingMessage: String
    val messageEdited: String
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
    val pureBlack: String
    val pureBlackHint: String
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
    val actionCopyRoomLink: String
    val actionCopySpaceLink: String
    val notifications: String
    val backgroundSync: String
    val backgroundSyncHint: String
    val actionReply: String
    val actionCopyText: String
    val actionCopyLink: String
    val actionDelete: String
    val actionSelectMessages: String
    val actionCancelSelection: String
    val actionCopySelected: String
    val actionDeleteSelected: String
    val selectedMessages: String
    val actionMarkUnread: String
    val actionMarkRead: String
    val actionMute: String
    val actionUnmute: String
    val actionPin: String
    val actionUnpin: String
    val actionSourceUrl: String
    val actionViewSource: String
    val actionForward: String
    val forwardTo: String
    val forwardUnsupported: String
    val viewSourceFailed: String
    val actionDownload: String
    val actionHideImage: String
    val hiddenImage: String
    val downloadFailed: String
    val deleteConfirmTitle: String
    val deleteReasonHint: String
    val deleteCannotUndo: String
    val sendShortcut: String
    val sendShortcutHint: String
    val keyEnter: String
    val keyCtrlEnter: String
    val keyAltEnter: String
    val keyShiftEnter: String
    val actionFavourite: String
    val actionLowPriority: String
    val actionInvite: String
    val actionLeave: String
    val inviteHint: String
    val copiedToClipboard: String
    val copy: String
    val replyingTo: String
    val verificationIncoming: String
    val verificationIncomingHint: String
    val cancelVerification: String
    val membersCount: String
    val imageMessage: String
    val stickerMessage: String
    val voiceMessage: String
    val recording: String
    val voiceUnsupported: String
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
    val refreshSessions: String
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
    val loginMethodPassword: String
    val loginMethodSso: String
    val loginSso: String
    val loginSsoHint: String
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
    val loggingIn: String
    val actionIgnoreUser: String
    val editDisplayName: String
    val changeAvatar: String
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
    AppLanguage.ZH_HK to ZhHkStrings,
    AppLanguage.ZH_CN to ZhCnStrings,
    AppLanguage.EN to EnStrings,
    AppLanguage.JA to JaStrings,
    AppLanguage.KO to KoStrings,
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
    override val searchMessages = "搜尋訊息"
    override val searchMessagesHint = "搜尋這個聊天室的訊息"
    override val noMessageSearchResults = "找不到符合的訊息"
    override val messageSearchFailed = "訊息搜尋失敗"
    override val publicRoomsLoadFailed = "無法載入公開聊天室"
    override val joinRoomFailed = "加入聊天室失敗"
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
    override val sendImage = "傳送圖片"
    override val verified = "已驗證"
    override val typingOne = "%s 正在輸入…"
    override val typingTwo = "%s 和 %s 正在輸入…"
    override val typingMany = "好幾個人正在輸入…"
    override val readByCount = "%d 人已讀"
    override val actionEdit = "編輯訊息"
    override val editingMessage = "編輯訊息"
    override val messageEdited = "已編輯"
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
    override val pureBlack = "純黑 (AMOLED)"
    override val pureBlackHint = "深色模式改用純黑背景，OLED 省電"
    override val dynamicColor = "動態顏色"
    override val dynamicColorHint = "基於桌布的主題顏色（Material You）"
    override val paletteStyle = "調色盤樣式"
    override val expressive = "Expressive"
    override val colorSpec = "顏色規格"
    override val chatList = "聊天室清單"
    override val unreadIndicators = "未讀提示"
    override val unreadIndicatorsHint = "Space 欄顯示未讀白條，清單顯示紅圈數字"
    override val messagePreview = "訊息預覽"
    override val personalization = "個人化"
    override val appearanceHint = "主題模式、動態顏色與調色盤"
    override val chatListHint = "Space 圖示、未讀提示、訊息預覽與貼圖面板"
    override val messagePreviewHint = "清單第二行顯示最後一則訊息"
    override val stickerPanelPosition = "貼圖面板位置"
    override val stickerPanelAbove = "浮在輸入列上方，不推動輸入列"
    override val stickerPanelBelow = "固定在輸入列下方"
    override val sessionLogoutViaAccountPage = "此伺服器改由帳戶管理頁登出裝置，已在瀏覽器開啟"
    override val messageSendFailed = "送出失敗"
    override val attachPhoto = "照片"
    override val attachSticker = "貼圖"
    override val attachTitle = "傳送"
    override val attachFile = "檔案"
    override val attach = "附件"
    override val spaceHome = "Space 首頁"
    override val actionCopyRoomLink = "複製聊天室連結"
    override val actionCopySpaceLink = "複製 Space 連結"
    override val notifications = "通知"
    override val backgroundSync = "背景同步"
    override val backgroundSyncHint = "常駐連線，離開 app 也收得到新訊息通知"
    override val actionReply = "回覆"
    override val actionCopyText = "複製文字"
    override val actionCopyLink = "複製訊息連結"
    override val actionDelete = "刪除訊息"
    override val actionSelectMessages = "選取訊息"
    override val actionCancelSelection = "取消選取"
    override val actionCopySelected = "複製"
    override val actionDeleteSelected = "刪除"
    override val selectedMessages = "已選取 %d 則訊息"
    override val actionMarkUnread = "標記為未讀"
    override val actionMarkRead = "標記為已讀"
    override val actionMute = "靜音通知"
    override val actionUnmute = "取消靜音"
    override val actionPin = "釘選"
    override val actionUnpin = "取消釘選"
    override val actionSourceUrl = "來源網址"
    override val actionViewSource = "檢視原始碼"
    override val actionForward = "轉寄"
    override val forwardTo = "轉寄到…"
    override val forwardUnsupported = "這種訊息還不支援轉寄"
    override val viewSourceFailed = "讀取事件失敗"
    override val actionDownload = "下載"
    override val actionHideImage = "隱藏圖片"
    override val hiddenImage = "圖片已隱藏（點一下顯示）"
    override val downloadFailed = "下載失敗"
    override val deleteConfirmTitle = "刪除訊息"
    override val deleteReasonHint = "原因（選填）"
    override val deleteCannotUndo = "刪除後無法復原，這則訊息會對所有人消失。"
    override val sendShortcut = "送出鍵"
    override val sendShortcutHint = "命中的組合送出，其餘 Enter 換行"
    override val keyEnter = "Enter"
    override val keyCtrlEnter = "Ctrl + Enter"
    override val keyAltEnter = "Alt + Enter"
    override val keyShiftEnter = "Shift + Enter"
    override val actionFavourite = "置頂"
    override val actionLowPriority = "置底"
    override val actionInvite = "邀請"
    override val actionLeave = "離開"
    override val inviteHint = "輸入要邀請的 Matrix ID（@user:server）"
    override val copiedToClipboard = "已複製"
    override val copy = "複製"
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
    override val voiceMessage = "語音訊息"
    override val recording = "錄音中"
    override val voiceUnsupported = "此平台無法播放此格式"
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
    override val refreshSessions = "刷新工作階段"
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
    override val loginUsername = "使用者名稱"
    override val loginMethodPassword = "帳號密碼"
    override val loginMethodSso = "Matrix SSO"
    override val loginSso = "使用 Matrix SSO 登入"
    override val loginSsoHint = "將在瀏覽器開啟家伺服器的官方登入頁"
    override val homeserver = "家伺服器"
    override val loggingIn = "正在登入…"
    override val actionIgnoreUser = "屏蔽用戶"
    override val editDisplayName = "編輯顯示名稱"
    override val changeAvatar = "變更頭像"
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

/** 日文介面；Matrix／Nashira／Material 3 等專有名詞保留原名。 */
/**
 * 簡體中文：委派 zh-TW，只覆寫兩岸用語差異。
 * zh-TW 是全量維護的基底——新字串先進 zh-TW/en，zh-CN 這裡只管收窄差異，
 * 沒覆寫的鍵顯示繁體（比顯示英文好）。
 */
object ZhCnStrings : Strings by ZhTwStrings {
    override val changeAvatar = "更改头像"
    override val homeserver = "家服务器"
    override val actionIgnoreUser = "屏蔽用户"
    override val editDisplayName = "编辑显示名称"
    override val loginSsoHint = "将在浏览器打开家服务器的官方登录页面"
    override val voiceMessage = "语音消息"
    override val recording = "录音中"
    override val voiceUnsupported = "此平台无法播放此格式"
}

/**
 * 繁體中文（香港）：委派 zh-TW，目前無已知用語差異；先佔位，
 * 之後按香港慣用語（「軟件」「資料夾」等）逐鍵覆寫。
 */
object ZhHkStrings : Strings by ZhTwStrings

object JaStrings : Strings by EnStrings {
    override val settings = "設定"
    override val back = "戻る"
    override val allRooms = "すべての部屋"
    override val rooms = "部屋"
    override val findOrStartConversation = "会話を検索または開始"
    override val syncingRooms = "部屋を同期中…"
    override val noRooms = "表示できる部屋はありません"
    override val privateMessage = "ダイレクトメッセージ"
    override val accountAndSecurity = "アカウントとセキュリティ"
    override val accountAndSecurityHint = "デバイス、認証、サインアウト"
    override val search = "検索"
    override val searchMessages = "メッセージを検索"
    override val searchMessagesHint = "この部屋のメッセージを検索"
    override val noMessageSearchResults = "該当するメッセージはありません"
    override val messageSearchFailed = "メッセージの検索に失敗しました"
    override val publicRoomsLoadFailed = "公開ルームを読み込めませんでした"
    override val joinRoomFailed = "ルームへの参加に失敗しました"
    override val members = "メンバー"
    override val more = "その他"
    override val roomBeginning = "ここから %s が始まります。"
    override val sendTo = "%s にメッセージを送信"
    override val add = "追加"
    override val send = "送信"
    override val sticker = "スタンプ"
    override val stickerEmpty = "スタンプパックはまだありません"
    override val notifImage = "[画像]"
    override val notifSticker = "[スタンプ]"
    override val notifUndecryptable = "[復号できません]"
    override val sendImage = "画像を送信"
    override val verified = "認証済み"
    override val typingOne = "%s が入力中…"
    override val typingTwo = "%s と %s が入力中…"
    override val typingMany = "複数のユーザーが入力中…"
    override val readByCount = "%d 人が既読"
    override val actionEdit = "メッセージを編集"
    override val editingMessage = "メッセージを編集中"
    override val messageEdited = "編集済み"
    override val tagline = "γ Capricorni · 吉報をもたらす星"
    override val appearance = "外観"
    override val darkTheme = "ダーク"
    override val lightTheme = "ライト"
    override val language = "言語"
    override val about = "アプリについて"
    override val version = "バージョン"
    override val engine = "エンジン"
    override val encryption = "暗号化"
    override val license = "ライセンス"
    override val sourceCode = "ソースコード"
    override val themeMode = "テーマモード"
    override val followSystem = "システムテーマに合わせる"
    override val pureBlack = "完全な黒（AMOLED）"
    override val pureBlackHint = "ダークモードの背景を完全な黒にして OLED の電力を節約"
    override val dynamicColor = "ダイナミックカラー"
    override val dynamicColorHint = "壁紙を基にしたテーマカラー（Material You）"
    override val paletteStyle = "パレットスタイル"
    override val expressive = "Expressive"
    override val colorSpec = "カラースペック"
    override val specM3 = "Material 3（2021）"
    override val specExpressive = "Expressive（2025）"
    override val themeColor = "テーマカラー"
    override val themeColorDefault = "デフォルト"
    override val spaceIconMode = "Space アイコン"
    override val spaceAvatar = "Space のアバターを表示"
    override val spaceRoomAvatars = "子部屋のアバタープレビューを表示"
    override val restoringSession = "ローカルデータを読み込み中…"
    override val chatList = "チャット一覧"
    override val personalization = "パーソナライズ"
    override val appearanceHint = "テーマモード、ダイナミックカラー、パレット"
    override val chatListHint = "Space アイコン、未読表示、プレビュー、スタンプパネル"
    override val unreadIndicators = "未読表示"
    override val unreadIndicatorsHint = "レールに未読バー、一覧に件数バッジを表示"
    override val messagePreview = "メッセージプレビュー"
    override val messagePreviewHint = "一覧の 2 行目に最新メッセージを表示"
    override val stickerPanelPosition = "スタンプパネルの位置"
    override val stickerPanelAbove = "入力欄の上に浮かせる"
    override val stickerPanelBelow = "入力欄の下に固定"
    override val sessionLogoutViaAccountPage = "このサーバーではアカウント管理ページからデバイスをサインアウトします。ブラウザーで開きました"
    override val messageSendFailed = "送信に失敗しました"
    override val attachPhoto = "写真"
    override val attachSticker = "スタンプ"
    override val attachTitle = "送信"
    override val attachFile = "ファイル"
    override val attach = "添付"
    override val spaceHome = "Space ホーム"
    override val actionCopyRoomLink = "部屋のリンクをコピー"
    override val actionCopySpaceLink = "Space のリンクをコピー"
    override val notifications = "通知"
    override val backgroundSync = "バックグラウンド同期"
    override val backgroundSyncHint = "接続を維持して、バックグラウンドでも通知を受け取る"
    override val actionReply = "返信"
    override val actionCopyText = "テキストをコピー"
    override val actionCopyLink = "メッセージのリンクをコピー"
    override val actionDelete = "メッセージを削除"
    override val actionSelectMessages = "メッセージを選択"
    override val actionCancelSelection = "選択を解除"
    override val actionCopySelected = "コピー"
    override val actionDeleteSelected = "削除"
    override val selectedMessages = "%d 件を選択中"
    override val actionMarkUnread = "未読にする"
    override val actionMarkRead = "既読にする"
    override val actionMute = "通知をミュート"
    override val actionUnmute = "ミュートを解除"
    override val actionPin = "ピン留め"
    override val actionUnpin = "ピン留めを解除"
    override val actionSourceUrl = "ソース URL"
    override val actionViewSource = "ソースを表示"
    override val actionForward = "転送"
    override val forwardTo = "転送先…"
    override val forwardUnsupported = "この種類のメッセージはまだ転送できません"
    override val viewSourceFailed = "イベントを読み込めませんでした"
    override val actionHideImage = "画像を非表示"
    override val hiddenImage = "画像を非表示中（タップで表示）"
    override val downloadFailed = "ダウンロードに失敗しました"
    override val deleteConfirmTitle = "メッセージを削除"
    override val deleteReasonHint = "理由（任意）"
    override val deleteCannotUndo = "元に戻せません。メッセージは全員から消えます。"
    override val sendShortcut = "送信ショートカット"
    override val sendShortcutHint = "選択した組み合わせで送信し、それ以外の Enter は改行"
    override val keyEnter = "Enter"
    override val keyCtrlEnter = "Ctrl + Enter"
    override val keyAltEnter = "Alt + Enter"
    override val keyShiftEnter = "Shift + Enter"
    override val actionFavourite = "お気に入り"
    override val actionLowPriority = "低優先度"
    override val actionInvite = "招待"
    override val actionLeave = "退出"
    override val inviteHint = "招待する Matrix ID を入力（@user:server）"
    override val copiedToClipboard = "コピーしました"
    override val copy = "コピー"
    override val replyingTo = "%s に返信"
    override val verificationIncoming = "セッション認証リクエスト"
    override val verificationIncomingHint = "別のセッションから認証を求められています。承認後、双方で絵文字を確認します。"
    override val cancelVerification = "認証をキャンセル"
    override val membersCount = "メンバー · %d"
    override val imageMessage = "画像"
    override val stickerMessage = "スタンプ"
    override val undecryptable = "⚠ このメッセージを復号できません"
    override val clearSearch = "検索をクリア"
    override val noSearchResults = "一致する部屋はありません"
    override val voiceMessage = "ボイスメッセージ"
    override val recording = "録音中"
    override val voiceUnsupported = "このプラットフォームでは再生できません"
    override val hideSecret = "非表示"
    override val acceptInvite = "承諾"
    override val declineInvite = "拒否"
    override val invited = "あなたを招待しました"
    override val loadMore = "過去のメッセージを読み込む"
    override val loadingMore = "読み込み中…"
    override val verificationDoneHint = "このデバイスはクロス署名済みで、他のクライアントにも認証済みと表示されます。"
    override val verificationLoading = "認証状態を読み込み中…"
    override val verificationNotReady = "まだ準備できていません"
    override val verificationCancelled = "認証をキャンセルしました"
    override val verificationInProgressShort = "認証中…"
    override val verificationWaitingOtherDevice = "リクエストを送信しました。別のデバイスで確認してください。"
    override val deviceUnverified = "このデバイスは未認証です"
    override val deviceUnverifiedHint = "認証すると暗号化された履歴を読め、他のクライアントの警告も消えます。"
    override val verifyWithRecoveryKey = "復元キーで認証"
    override val verifyWithRecoveryKeyHint = "セキュアバックアップで取得したキーを貼り付け"
    override val verifyWithPassphrase = "セキュリティパスフレーズで認証"
    override val verifyWithPassphraseHint = "セキュアバックアップに設定したパスフレーズを入力"
    override val verifyWithOtherDevice = "別のデバイスで認証"
    override val verifyWithOtherDeviceHint = "認証済みデバイスと絵文字を確認"
    override val crossSigningMissing = "アカウントのクロス署名が未設定です"
    override val crossSigningMissingHint = "ここで初期化すると、一度だけ使う復元キーが生成されます。"
    override val bootstrapCrossSigning = "クロス署名とバックアップを設定"
    override val bootstrapCrossSigningHint = "復元キーを生成してこのデバイスに署名"
    override val bootstrapFailed = "クロス署名の設定に失敗しました"
    override val recoveryKey = "復元キー"
    override val passphrase = "セキュリティパスフレーズ"
    override val recoveryKeyCreated = "復元キーを作成しました"
    override val recoveryKeyCreatedHint = "今すぐ書き留めて安全に保管してください。もう表示できません。"
    override val recoveryKeySaved = "保存しました"
    override val compareEmojiHint = "両方のデバイスにまったく同じ絵文字が表示されていることを確認してください。"
    override val sessions = "セッション"
    override val refreshSessions = "セッションを更新"
    override val sessionsLoadFailed = "セッションを読み込めませんでした"
    override val sessionLogoutFailed = "このセッションをサインアウトできませんでした"
    override val sessionVerified = "認証済み"
    override val sessionUnverified = "未認証"
    override val sessionBlocked = "ブロック済み"
    override val sessionUnknown = "不明"
    override val sessionCurrent = "このデバイス"
    override val verifySession = "認証"
    override val logoutSession = "サインアウト"
    override val cancel = "キャンセル"
    override val loginUsername = "ユーザー名"
    override val loginMethodPassword = "ユーザー名とパスワード"
    override val loginMethodSso = "Matrix SSO"
    override val loginSso = "Matrix SSO で続行"
    override val loginSsoHint = "ブラウザーでホームサーバーの公式ログインページを開きます"
    override val today = "今日"
    override val yesterday = "昨日"
    override val justNow = "たった今"
    override val minutesAgo = "%d 分前"
    override val hoursAgo = "%d 時間前"
    override val daysAgo = "%d 日前"
    override val monthsAgo = "%d か月前"
    override val yearsAgo = "%d 年前"
    override fun formatDate(year: Int, month: Int, day: Int) = "${year}年${month}月${day}日"
    override val homeserver = "ホームサーバー"
    override val loggingIn = "ログイン中…"
    override val actionIgnoreUser = "ユーザーをブロック"
    override val editDisplayName = "表示名を編集"
    override val changeAvatar = "アイコンを変更"
    override val loginPassword = "パスワード"
    override val loginSubmit = "ログイン"
    override val logout = "ログアウト"
    override val noRoomSelected = "部屋を選択して開始"
    override val sendFailed = "送信に失敗しました"
    override val account = "アカウント"
    override val accountId = "アカウント ID"
    override val deviceId = "デバイス ID"
    override val security = "セキュリティ"
    override val deviceVerification = "デバイス認証"
    override val verifyThisDevice = "このデバイスを認証"
    override val verificationCreated = "認証セッションを作成しました。別のデバイスで確認してください。"
    override val verificationState = "認証状態"
    override val noVerification = "現在進行中の認証はありません。"
    override val securityHint = "Nashira は Trixnity vodozemac を使用します。認証すると、このデバイスを信頼して履歴を復号できます。"
    override val verificationHint = "ログイン済みの別の Matrix デバイスで SAS を確認してください。認証イベントは暗号化された to-device 経由で送信されます。"
    override val targetDeviceId = "認証するデバイス ID"
    override val targetDeviceIdExample = "例：ABCDEFGHIJ"
    override val verificationFailed = "認証リクエストに失敗しました"
    override val logoutDevice = "このデバイスをサインアウト"
    override val verificationDone = "このデバイスは認証済みです。"
    override val verificationInProgress = "認証セッション進行中：%s"
    override val waitingAnotherDevice = "別のデバイスを待っています"
    override val acceptVerification = "認証リクエストを承認"
    override val startSasVerification = "SAS 認証を開始"
    override val acceptSas = "SAS を承認"
    override val match = "一致"
    override val noMatch = "一致しません"
}

/** 韓文介面；Matrix／Nashira／Material 3 等專有名詞保留原名。 */
object KoStrings : Strings by EnStrings {
    override val settings = "설정"
    override val back = "뒤로"
    override val allRooms = "모든 대화방"
    override val rooms = "대화방"
    override val findOrStartConversation = "대화 찾기 또는 시작"
    override val syncingRooms = "대화방 동기화 중…"
    override val noRooms = "표시할 대화방이 없습니다"
    override val privateMessage = "개인 메시지"
    override val accountAndSecurity = "계정 및 보안"
    override val accountAndSecurityHint = "기기, 인증 및 로그아웃"
    override val search = "검색"
    override val searchMessages = "메시지 검색"
    override val searchMessagesHint = "이 대화방의 메시지 검색"
    override val noMessageSearchResults = "일치하는 메시지가 없습니다"
    override val messageSearchFailed = "메시지 검색 실패"
    override val publicRoomsLoadFailed = "공개 대화방을 불러오지 못했습니다"
    override val joinRoomFailed = "대화방에 참여하지 못했습니다"
    override val members = "멤버"
    override val more = "더보기"
    override val roomBeginning = "여기서 %s이(가) 시작됩니다."
    override val sendTo = "%s에 메시지 보내기"
    override val add = "추가"
    override val send = "보내기"
    override val sticker = "스티커"
    override val stickerEmpty = "스티커 팩이 아직 없습니다"
    override val notifImage = "[이미지]"
    override val notifSticker = "[스티커]"
    override val notifUndecryptable = "[복호화할 수 없음]"
    override val sendImage = "이미지 보내기"
    override val verified = "인증됨"
    override val typingOne = "%s님이 입력 중…"
    override val typingTwo = "%s님과 %s님이 입력 중…"
    override val typingMany = "여러 명이 입력 중…"
    override val readByCount = "%d명이 읽음"
    override val actionEdit = "메시지 수정"
    override val editingMessage = "메시지 수정 중"
    override val messageEdited = "수정됨"
    override val tagline = "γ Capricorni · 좋은 소식을 전하는 별"
    override val appearance = "모양"
    override val darkTheme = "어두운 테마"
    override val lightTheme = "밝은 테마"
    override val language = "언어"
    override val about = "정보"
    override val version = "버전"
    override val engine = "엔진"
    override val encryption = "암호화"
    override val license = "라이선스"
    override val sourceCode = "소스 코드"
    override val themeMode = "테마 모드"
    override val followSystem = "시스템 테마 따르기"
    override val pureBlack = "완전한 검정(AMOLED)"
    override val pureBlackHint = "어두운 모드에서 완전한 검정 배경을 사용해 OLED 전력 절약"
    override val dynamicColor = "동적 색상"
    override val dynamicColorHint = "배경화면 기반 테마 색상(Material You)"
    override val paletteStyle = "팔레트 스타일"
    override val expressive = "Expressive"
    override val colorSpec = "색상 사양"
    override val specM3 = "Material 3(2021)"
    override val specExpressive = "Expressive(2025)"
    override val themeColor = "테마 색상"
    override val themeColorDefault = "기본값"
    override val spaceIconMode = "Space 아이콘"
    override val spaceAvatar = "Space 아바타 표시"
    override val spaceRoomAvatars = "하위 대화방 아바타 미리보기 표시"
    override val restoringSession = "로컬 데이터 불러오는 중…"
    override val chatList = "대화 목록"
    override val personalization = "개인 설정"
    override val appearanceHint = "테마 모드, 동적 색상 및 팔레트"
    override val chatListHint = "Space 아이콘, 읽지 않음 표시, 미리보기 및 스티커 패널"
    override val unreadIndicators = "읽지 않음 표시"
    override val unreadIndicatorsHint = "레일에 읽지 않음 막대, 목록에 개수 배지 표시"
    override val messagePreview = "메시지 미리보기"
    override val messagePreviewHint = "목록의 두 번째 줄에 최신 메시지 표시"
    override val stickerPanelPosition = "스티커 패널 위치"
    override val stickerPanelAbove = "입력창 위에 띄우기"
    override val stickerPanelBelow = "입력창 아래에 고정"
    override val sessionLogoutViaAccountPage = "이 서버는 계정 관리 페이지에서 기기를 로그아웃합니다. 브라우저에서 열었습니다"
    override val messageSendFailed = "보내지 못했습니다"
    override val attachPhoto = "사진"
    override val attachSticker = "스티커"
    override val attachTitle = "보내기"
    override val attachFile = "파일"
    override val attach = "첨부"
    override val spaceHome = "Space 홈"
    override val actionCopyRoomLink = "대화방 링크 복사"
    override val actionCopySpaceLink = "Space 링크 복사"
    override val notifications = "알림"
    override val backgroundSync = "백그라운드 동기화"
    override val backgroundSyncHint = "연결을 유지해 백그라운드에서도 알림을 받습니다"
    override val actionReply = "답장"
    override val actionCopyText = "텍스트 복사"
    override val actionCopyLink = "메시지 링크 복사"
    override val actionDelete = "메시지 삭제"
    override val actionSelectMessages = "메시지 선택"
    override val actionCancelSelection = "선택 취소"
    override val actionCopySelected = "복사"
    override val actionDeleteSelected = "삭제"
    override val selectedMessages = "%d개 메시지 선택됨"
    override val actionMarkUnread = "읽지 않음으로 표시"
    override val actionMarkRead = "읽음으로 표시"
    override val actionMute = "알림 음소거"
    override val actionUnmute = "음소거 해제"
    override val actionPin = "고정"
    override val actionUnpin = "고정 해제"
    override val actionSourceUrl = "소스 URL"
    override val actionViewSource = "소스 보기"
    override val actionForward = "전달"
    override val forwardTo = "전달 대상…"
    override val forwardUnsupported = "이 메시지 유형은 아직 전달할 수 없습니다"
    override val viewSourceFailed = "이벤트를 불러오지 못했습니다"
    override val actionDownload = "다운로드"
    override val actionHideImage = "이미지 숨기기"
    override val hiddenImage = "이미지 숨김(탭하여 표시)"
    override val downloadFailed = "다운로드 실패"
    override val deleteConfirmTitle = "메시지 삭제"
    override val deleteReasonHint = "이유(선택 사항)"
    override val deleteCannotUndo = "취소할 수 없습니다. 모든 사람에게서 메시지가 사라집니다."
    override val sendShortcut = "보내기 단축키"
    override val sendShortcutHint = "선택한 조합으로 보내며, 다른 Enter 입력은 줄바꿈합니다"
    override val keyEnter = "Enter"
    override val keyCtrlEnter = "Ctrl + Enter"
    override val keyAltEnter = "Alt + Enter"
    override val keyShiftEnter = "Shift + Enter"
    override val actionFavourite = "즐겨찾기"
    override val actionLowPriority = "낮은 우선순위"
    override val actionInvite = "초대"
    override val actionLeave = "나가기"
    override val inviteHint = "초대할 Matrix ID 입력(@user:server)"
    override val copiedToClipboard = "복사됨"
    override val copy = "복사"
    override val replyingTo = "%s님에게 답장"
    override val verificationIncoming = "세션 인증 요청"
    override val verificationIncomingHint = "다른 세션에서 인증을 요청했습니다. 수락하면 양쪽에서 이모지를 확인합니다."
    override val cancelVerification = "인증 취소"
    override val membersCount = "멤버 · %d"
    override val imageMessage = "이미지"
    override val stickerMessage = "스티커"
    override val voiceMessage = "음성 메시지"
    override val recording = "녹음 중"
    override val voiceUnsupported = "이 플랫폼에서는 재생할 수 없습니다"
    override val undecryptable = "⚠ 이 메시지를 복호화할 수 없습니다"
    override val clearSearch = "검색 지우기"
    override val noSearchResults = "일치하는 대화방이 없습니다"
    override val showSecret = "표시"
    override val hideSecret = "숨기기"
    override val acceptInvite = "수락"
    override val declineInvite = "거절"
    override val invited = "님이 초대했습니다"
    override val loadMore = "이전 메시지 불러오기"
    override val loadingMore = "불러오는 중…"
    override val verificationDoneHint = "이 기기는 교차 서명되었으며 다른 클라이언트에서도 인증됨으로 표시됩니다."
    override val verificationLoading = "인증 상태 불러오는 중…"
    override val verificationNotReady = "아직 준비되지 않음"
    override val verificationCancelled = "인증 취소됨"
    override val verificationInProgressShort = "인증 진행 중…"
    override val verificationWaitingOtherDevice = "요청을 보냈습니다. 다른 기기에서 확인하세요."
    override val deviceUnverified = "이 기기는 인증되지 않았습니다"
    override val deviceUnverifiedHint = "인증하면 암호화된 기록을 읽고 다른 클라이언트의 경고를 없앨 수 있습니다."
    override val verifyWithRecoveryKey = "복구 키로 인증"
    override val verifyWithRecoveryKeyHint = "보안 백업에서 받은 키 붙여넣기"
    override val verifyWithPassphrase = "보안 암호로 인증"
    override val verifyWithPassphraseHint = "보안 백업에 설정한 암호 입력"
    override val verifyWithOtherDevice = "다른 기기로 인증"
    override val verifyWithOtherDeviceHint = "이미 인증된 기기와 이모지 비교"
    override val crossSigningMissing = "계정에 교차 서명이 설정되지 않았습니다"
    override val crossSigningMissingHint = "여기서 초기화하면 한 번만 사용할 복구 키가 생성됩니다."
    override val bootstrapCrossSigning = "교차 서명 및 백업 설정"
    override val bootstrapCrossSigningHint = "복구 키를 생성하고 이 기기에 서명"
    override val bootstrapFailed = "교차 서명 설정 실패"
    override val recoveryKey = "복구 키"
    override val passphrase = "보안 암호"
    override val recoveryKeyCreated = "복구 키 생성됨"
    override val recoveryKeyCreatedHint = "지금 적어 안전하게 보관하세요. 다시 표시할 수 없습니다."
    override val recoveryKeySaved = "저장했습니다"
    override val compareEmojiHint = "두 기기에 완전히 같은 이모지가 표시되는지 확인하세요."
    override val sessions = "세션"
    override val refreshSessions = "세션 새로고침"
    override val sessionsLoadFailed = "세션을 불러오지 못했습니다"
    override val sessionLogoutFailed = "이 세션을 로그아웃하지 못했습니다"
    override val sessionVerified = "인증됨"
    override val sessionUnverified = "인증되지 않음"
    override val sessionBlocked = "차단됨"
    override val sessionUnknown = "알 수 없음"
    override val sessionCurrent = "이 기기"
    override val verifySession = "인증"
    override val logoutSession = "로그아웃"
    override val cancel = "취소"
    override val loginUsername = "사용자 이름"
    override val loginMethodPassword = "사용자 이름 및 비밀번호"
    override val loginMethodSso = "Matrix SSO"
    override val loginSso = "Matrix SSO로 계속"
    override val loginSsoHint = "브라우저에서 홈서버 공식 로그인 페이지 열기"
    override val today = "오늘"
    override val yesterday = "어제"
    override val justNow = "방금"
    override val minutesAgo = "%d분 전"
    override val hoursAgo = "%d시간 전"
    override val daysAgo = "%d일 전"
    override val monthsAgo = "%d개월 전"
    override val yearsAgo = "%d년 전"
    override fun formatDate(year: Int, month: Int, day: Int) = "${year}년 ${month}월 ${day}일"
    override val homeserver = "홈서버"
    override val loggingIn = "로그인 중…"
    override val actionIgnoreUser = "사용자 차단"
    override val editDisplayName = "표시 이름 편집"
    override val changeAvatar = "프로필 사진 변경"
    override val loginPassword = "비밀번호"
    override val loginSubmit = "로그인"
    override val logout = "로그아웃"
    override val noRoomSelected = "대화방을 선택하여 시작"
    override val sendFailed = "보내지 못했습니다"
    override val account = "계정"
    override val accountId = "계정 ID"
    override val deviceId = "기기 ID"
    override val security = "보안"
    override val deviceVerification = "기기 인증"
    override val verifyThisDevice = "이 기기 인증"
    override val verificationCreated = "인증 세션이 생성되었습니다. 다른 기기에서 확인하세요."
    override val verificationState = "인증 상태"
    override val noVerification = "현재 진행 중인 인증이 없습니다."
    override val securityHint = "Nashira는 Trixnity vodozemac을 사용합니다. 인증하면 이 기기를 신뢰하여 기록을 복호화합니다."
    override val verificationHint = "로그인된 다른 Matrix 기기에서 SAS를 확인하세요. 인증 이벤트는 암호화된 to-device 채널을 사용합니다."
    override val targetDeviceId = "인증할 기기 ID"
    override val targetDeviceIdExample = "예: ABCDEFGHIJ"
    override val verificationFailed = "인증 요청 실패"
    override val logoutDevice = "이 기기 로그아웃"
    override val verificationDone = "이 기기는 인증되었습니다."
    override val verificationInProgress = "인증 세션 진행 중: %s"
    override val waitingAnotherDevice = "다른 기기를 기다리는 중"
    override val acceptVerification = "인증 요청 수락"
    override val startSasVerification = "SAS 인증 시작"
    override val acceptSas = "SAS 수락"
    override val match = "일치함"
    override val noMatch = "일치하지 않음"
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
    override val searchMessages = "Search messages"
    override val searchMessagesHint = "Search messages in this room"
    override val noMessageSearchResults = "No matching messages"
    override val messageSearchFailed = "Message search failed"
    override val publicRoomsLoadFailed = "Unable to load public rooms"
    override val joinRoomFailed = "Unable to join room"
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
    override val typingOne = "%s is typing…"
    override val typingTwo = "%s and %s are typing…"
    override val typingMany = "Several people are typing…"
    override val readByCount = "Read by %d"
    override val actionEdit = "Edit message"
    override val editingMessage = "Editing message"
    override val messageEdited = "edited"
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
    override val pureBlack = "Pure black (AMOLED)"
    override val pureBlackHint = "Use a true-black background in dark mode to save OLED power"
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
    override val voiceMessage = "Voice message"
    override val recording = "Recording"
    override val voiceUnsupported = "Cannot play this format on this platform"
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
    override val refreshSessions = "Refresh sessions"
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
    override val loginMethodPassword = "Username and password"
    override val loginMethodSso = "Matrix SSO"
    override val loginSso = "Continue with Matrix SSO"
    override val loginSsoHint = "Your home server's official sign-in page opens in your browser"
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
    override val actionCopyRoomLink = "Copy room link"
    override val actionCopySpaceLink = "Copy Space link"
    override val notifications = "Notifications"
    override val backgroundSync = "Background sync"
    override val backgroundSyncHint = "Keeps the connection open so notifications arrive in the background"
    override val actionReply = "Reply"
    override val actionCopyText = "Copy text"
    override val actionCopyLink = "Copy message link"
    override val actionDelete = "Remove message"
    override val actionSelectMessages = "Select messages"
    override val actionCancelSelection = "Cancel selection"
    override val actionCopySelected = "Copy"
    override val actionDeleteSelected = "Delete"
    override val selectedMessages = "%d messages selected"
    override val actionMarkUnread = "Mark as unread"
    override val actionMarkRead = "Mark as read"
    override val actionMute = "Mute notifications"
    override val actionUnmute = "Unmute"
    override val actionPin = "Pin"
    override val actionUnpin = "Unpin"
    override val actionSourceUrl = "Source URL"
    override val actionViewSource = "View source"
    override val actionForward = "Forward"
    override val forwardTo = "Forward to…"
    override val forwardUnsupported = "Forwarding this message type isn't supported yet"
    override val viewSourceFailed = "Failed to load the event"
    override val actionDownload = "Download"
    override val actionHideImage = "Hide image"
    override val hiddenImage = "Image hidden (tap to show)"
    override val downloadFailed = "Download failed"
    override val deleteConfirmTitle = "Delete message"
    override val deleteReasonHint = "Reason (optional)"
    override val deleteCannotUndo = "This cannot be undone; the message will disappear for everyone."
    override val sendShortcut = "Send shortcut"
    override val sendShortcutHint = "The chosen combo sends; other Enter presses insert a newline"
    override val keyEnter = "Enter"
    override val keyCtrlEnter = "Ctrl + Enter"
    override val keyAltEnter = "Alt + Enter"
    override val keyShiftEnter = "Shift + Enter"
    override val actionFavourite = "Favourite"
    override val actionLowPriority = "Low priority"
    override val actionInvite = "Invite"
    override val actionLeave = "Leave"
    override val inviteHint = "Enter the Matrix ID to invite (@user:server)"
    override val copiedToClipboard = "Copied"
    override val copy = "Copy"
    override val replyingTo = "Replying to %s"
    override val verificationIncoming = "Session verification request"
    override val verificationIncomingHint = "Another session asked to verify. After accepting, both sides compare a set of emoji."
    override val cancelVerification = "Cancel verification"
    override val personalization = "Personalization"
    override val appearanceHint = "Theme mode, dynamic color, and palette"
    override val chatListHint = "Space icons, unread indicators, previews, and sticker panel"
    override val homeserver = "Home server"
    override val loggingIn = "Signing in…"
    override val actionIgnoreUser = "Ignore user"
    override val editDisplayName = "Edit display name"
    override val changeAvatar = "Change profile picture"
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
