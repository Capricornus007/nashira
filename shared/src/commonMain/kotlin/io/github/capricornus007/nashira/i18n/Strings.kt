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
    val emoticons: String
    /** 面板開著時那顆鈕的無障礙標籤（圖示是鍵盤，點下去叫回輸入法）。 */
    val keyboard: String
    /**
     * 輸入框右鍵選單裡的格式化項目標籤。清單照 Element Web 的 composer format bar
     * （`Formatting` 列舉：bold／italics／strikethrough／code／quote／insert_link），
     * 它沒有底線與清單按鈕，我們也不自己加。
     */
    val formatBold: String
    val formatItalics: String
    val formatStrikethrough: String
    val formatCodeBlock: String
    val formatQuote: String
    val formatLink: String
    /** 表情分頁：搜尋提示、查無結果、最近使用區標題。 */
    val emojiSearchHint: String
    val emojiNoResults: String
    val emojiRecent: String
    /**
     * Unicode 分類的本地化名稱（面板節標）。四套參考客戶端（64Gram 桌面、MoregramX／
     * Nagram XF 手機、Discord、Element）的分類標題**一律是文字**，只有頂部用來跳轉的
     * 那一條是圖示——之前只給 glyph 是自創的變體。[group] 是 emojibase 的 group id。
     */
    fun emojiCategoryName(group: Int): String = ""
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
    /** 反應選擇器的入口：hover 列的笑臉鈕與選單裡那一列共用。 */
    val actionAddReaction: String
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
    /** 底欄麥克風靜音時按錄音的提示（不錄，但要講清楚為什麼）。 */
    val recordingMutedHint: String
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
    val syncConnecting: String
    val connectionFailed: String
    val connectionFailedHint: String
    val retry: String
    val actionIgnoreUser: String
    /** 帳戶與安全頁的「已屏蔽名單」區（屏蔽要能解開，否則誤屏蔽只能去別的客戶端）。 */
    val ignoredUsersTitle: String
    val ignoredUsersEmpty: String
    val actionUnignoreUser: String
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
    val audioSection: String
    val audioInputDevice: String
    val audioOutputDevice: String
    val audioDeviceDefault: String
    /**
     * 「系統預設: <裝置名>」——Discord 的寫法，一眼看出預設其實落在哪個裝置。
     * 六種語言都是「<標籤>: <名字>」同一個順序，所以給預設實作就好（不像
     * formatDate 每種語法真的不同）；哪天要調語序，某個語言 override 即可。
     */
    fun audioDeviceDefaultNamed(name: String): String = "$audioDeviceDefault: $name"
    val trayOpen: String
    val trayQuit: String
    val jumpToLatest: String
    val inputVolume: String
    val outputVolume: String
    val audioSettingsLink: String
    val editProfile: String
    val copyMatrixId: String
    val presenceOnline: String
    val replyOriginal: String
    val noPreviewAvailable: String
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
