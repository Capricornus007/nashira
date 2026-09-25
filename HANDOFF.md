# Nashira 交接文檔

更新：2026-09-24（上一版是 2026-09-10 導出、master @ `76fb1d4`；之後 40+ 筆提交的狀態這版已逐條對碼核過）。

## ⚠ 中斷點：2026-09-24 深夜用戶關機，反應選擇器做到一半（先讀這段）

**工作區是髒的、未提交、而且此刻大概率編不過**——重啟後第一步不是編譯，是把下面 ③ 做完。

### 已经完成、還在工作區沒進 git 的改動
1. **`EmojiBrowser.kt`（新檔，未 `git add`）**：從 `StickerPicker.kt` 搬出來的表情瀏覽器本體——
   搜尋列＋分類圖示條＋「最近使用／自訂表情／九個 Unicode 分類」混排網格，
   `internal fun EmojiBrowser(strings, client, emoticons, onPickEmoji, onPickEmoticon, modifier)`。
   连同 `EmojiRow` / `EmojiPlan` / `buildEmojiPlan` / `EmojiSectionHeader` / `EmojiCell` 一起搬過來了。
   底部那個 `emojiCellHighlight()` 辅助函式已刪（它是非 composable 卻讀 `MaterialTheme.colorScheme`，
   踩 `staticCompositionLocalOf` 只能在組合區讀的坑）。
2. **`StickerPicker.kt`**：刪掉搬走的 181 行；`if (emojiTab)` 分支改成自己 collect
   `repository.emoticons()` 再呼叫 `EmojiBrowser(...)`。**還沒驗：`StickerThumb` 目前是 `private`，
   但 `EmojiBrowser.kt` 要用它 → 必須改成 `internal`，這是第一個要補的編譯錯誤。**
   另外残留的 import（`StickerItem`、`LazyRow`、`gridItemsIndexed`、`rememberLazyGridState`、
   `EmojiEntry`/`EmojiIndex`/`EmojiTable`、`collectAsState`、`rememberCoroutineScope`、
   `Icons.Filled.Search/Close`、`OutlinedTextField`、`Spacer`、`PaddingValues`、`delay` 等）
   要按編譯器／detekt 的結果清掉，別憑記憶刪。
3. **i18n 五檔齊加 `actionAddReaction`**（`Strings.kt` 宣告＋En/Ja/Ko/ZhTw 實作＋`ZhCnStrings` 覆寫「添加反应」）：
   en "Add Reaction" / ja "リアクション追加" / ko "반응 추가" / zh-TW "新增反應" / zh-CN "添加反应"。
   介面是「全鍵實作」，漏語言會直接編譯失敗，不會靜默顯示英文。

### 還沒動的部分（接下來的活）
① **反應選擇器接上 `EmojiBrowser`（`ChatScreen.kt`）**—— presently `MessageRow` 的 hover 浮條第一個鈕
   是「點一下送 👍」（`QuickReactions.first()`，ChatScreen.kt 約 3268 行），要換成
   `Icons.Filled.Face`（核心圖示集已有，專案其他地方在用）開彈出選擇器：
   - `var pickerOpen`、`var pickerAnchor`；位置用 threeDot 那招
     `.onGloballyPositioned { c -> c.positionInRoot() + Offset(0f, c.size.height.toFloat()) - boxOrigin }`。
   - 容器沿用 `ContextMenuSurface(expanded=..., onDismiss=..., anchor=...)`（內部是 `DropdownMenu`，會自動避邊），
     內容 `Box(Modifier.width(320.dp).height(300.dp)) { EmojiBrowser(strings, client, emptyList(), onPickEmoji = {...}) }`。
   - 點選：`onToggleReaction(entry.glyph, msg.reactions[entry.glyph]?.mine)` 然後關掉彈窗。
   - `QuickReactions`（10 個，約 3033 行）**留著**當 action 選單裡的一列快速反應；
     選單裡再補一列 `ContextMenuItem(strings.actionAddReaction)` 也開同一個選擇器（安卓沒有 hover，這是它唯一入口）。
   - 3022 行那句註解「完整選擇器還沒做，這幾個對齊 Discord 的預設快捷」做完要改掉/刪掉。
   - 反應選擇器**不列自訂表情**（`emoticons = emptyList()`）：本輪已二次核實 Trixnity 5.8.1
     `RelatesTo$Annotation` 只有 `(eventId, key)` 兩個欄位、放不下 MSC2545 反應要的
     `m.relates_to.url`（`ReactionEventContent` 那個 `externalUrl` 是 content 層的字，不是 spec 位置）。
     給按了卻送不出去的表情比不給更糟，這條已寫進 `EmojiBrowser.kt` 檔頭。
② **膚色變體**（`EmojiEntry.skins`，長按彈色）——`EmojiCell` 裡那條 TODO 註解就是它。
③ **桌面滑鼠懸停顯示 `:shortcode:` 預覽條** ＋ 64Gram 式釘選/停靠面板。
④ 全部做完 → `--no-daemon` 雙目標編譯（desktop＋android）→ commit → 一批一個 release：
   版本號 0.1.5 → **0.1.6**（`androidApp` versionCode/versionName＋`desktopApp` packageVersion）→ 推 → 盯 CI 到全綠。

### 本地未推的提交（2 筆，HEAD=4d82da1，origin/master=f594cdb）
- `030ae3b` 表情表產生器與 `EmojiTable`（資料層）
- `4d82da1` 表情分頁完成——搜尋＋最近使用＋自訂表情＋Unicode 九個分類
故意先不推：等這一整批（①②③）湊齊再一起出 v0.1.6（用戶已核准「版本一堆那就一堆」＝一批一版）。

### 待驗證清單（綠燈不等於產物，得實機/實際核對）
- v0.1.5 的 HTML 渲染與語音.amplitude 只在 desktop 看過，安卓端還沒照會。
- 頭像上傳兩段式修正（`prepareUploadMedia` → `uploadMedia`）尚未實機驗證真的傳到伺服器和下發。
- 之前測試時**誤貼了一張貼圖到「水晶玉」房間（21:33）**，用戶尚未回答要不要刪——別自己動。

### 下次接手入口命令
```bash
cd ~/Downloads/nashira && git status --short && git log --oneline -3
# 先補 StickerThumb → internal，再編：
./gradlew --no-daemon :shared:compileKotlinDesktop :shared:compileAndroidMain   # 跑完 pkill 殘留 daemon
# 看未推的兩筆：
git log --oneline origin/master..HEAD
# CI（run number ≠ databaseId，列 job 要 --paginate）：
gh run list --repo Capricornus007/nashira --limit 5
```
- 手機（adb）目前**歸隔壁 qoder 國際版 CLI 用**（在修 nb4a）。要碰手機先
  `~/bin/phone-lock acquire nashira`、動完 `touch`/`release`，**禁 `adb kill-server`、動完別鎖屏**。
- 本檔下方的「當前狀態／release」段落是 v0.1.1 時代寫的，版號已過期（實際已發到 v0.1.5），
  只有裡面的「慣例」「Trixnity API 備忘」還有效。

## 當前狀態
- 倉庫：`~/Downloads/nashira`，master 與 origin 同步（本檔撰寫時 HEAD 為版本號 0.1.1 那筆）。
- 上一個已發佈的 release：**v0.1.0**（2026-09-05，之後 40+ 筆提交都沒進版）。
  本次把版號推到 **0.1.1**（`androidApp` versionCode 2 / versionName 0.1.1、`desktopApp` packageVersion 0.1.1），
  push 觸發 `release.yml` 才會真正產出 v0.1.1 的資產：Android APK（CI 秘密金鑰簽名）、Linux `.deb`/`.rpm`、Arch `.pkg.tar.zst`。
  **看 v0.1.1 存不存在要查 `gh release view v0.1.1`，別只信這段字。**
  注意：`release.yml` 是 **push 到 master 就觸發**，tag 取自 `packageVersion`；同一版號再推只會 `--clobber` 蓋掉既有 release 的資產，不會新增版本。
- 另一條桌面分发通道：`~/Downloads/repo` 自動構建倉的 **`nashira-git`**（跟 master HEAD 從源碼出包，與上游 release 的 `nashira` 互斥 conflicts）。
- 手機實測裝置：`9e91021c`（root，adb 已信任）。

## 功能對照 Element 的完成度

### 已完成（代碼可查，非宣稱）
- **P4-1 屏蔽用戶**：`RoomRepository.setIgnored`/`ignoredUsers`，時間線過濾在 `ChatScreen`（`page.messages.filter { sender !in ignoredUsers }`，已 `remember`）。帳戶與安全頁有「已屏蔽的用戶」清單，可逐筆取消屏蔽（2026-09-24 補，之前只能屏蔽、解不開）。
- **P4-2 fully_read**：`markRead()` 一次把 `read` + `fully_read` 兩個 marker 都推到最後一則事件（與 Element「標記為已讀」同義）。獨立的 `markFullyRead()` 已刪除（曾經只被 markRead 前面的多餘呼叫使用）。
- **P4-3 顯示名稱＋頭像**：`SecurityAndAccountScreen` 有頭像選擇器 → `setAvatar`。
  歷史坑（已修）：`prepareUploadMedia` 回的是**媒體暫存 cache URI**，必須再 `uploadMedia(cacheUri)` 才拿到 `mxc://`；少這步不會編譯報錯，只會設出一個顯示不出來的 avatar_url。新增媒體上傳時一律照 `sendImage`/`sendFile`/`setAvatar` 的兩步寫。
- **P4-4 訊息格式化**：`ChatScreen.htmlToAnnotatedString` 真渲染 `formatted_body`。
  支援：`b/strong`、`i/em`、`u`、`del/s`、`code`（inline 與 `pre>code` 區分）、`a`（可點擊，走 `openLink`）、`br`、`img[data-mx-emoticon]`（自訂表情內聯）。巢狀標籤走樣式堆疊（`<b>…<i>…</i>…</b>` 不會中途丟粗體）。實體解碼表在 `HtmlEntities.kt`（WHATWG 全表）＋數字實體。
  **不支援**（目前只吃掉標籤、文字照出）：`blockquote`、`ul/ol/li`、`table/*`、`h1..h6`、`sub/sup`、`span`（含 `data-mx-color`）、`mx-reply`（引用區塊會變成行內文字）、mention pill（`matrix.to` 連結只當普通外链）。
  教訓：`072994a` 曾把正則改壞（`\w`→`\\w`、少了捕獲群）導致**所有 HTML 顯示成原始碼**，卻被標成「UI 未做」無人發覺——這條沒有測試 Coverage，改到渲染請人工對照實際訊息。
- **P5-1 語音訊息**：兩端可錄可播。
  Android：`MediaRecorder`→AAC/m4a、`MediaPlayer` 播放、執行期 `RECORD_AUDIO`。
  桌面：`TargetDataLine`→16k 單聲道 WAV、`javax.sound.Clip` 播放（非 WAV 退 `ffplay`）；额外有裝置選擇、輸入增益、輸出音量、底欄麥克風靜音／耳機拒聽（`PlatformCapabilities.audioDeviceSettingsSupported`＝桌面 true／Android false，那三顆鈕與裝置面板在手機完全不顯示，因為 Android 端實作從不讀那些旗標）。
  **靜音 × 錄音的既定行為**（2026-09-24 用戶拍板，別再改成自動解除）：底欄麥克風靜音時按錄音＝**不錄、也不幫你翻掉靜音**，輸入列那顆麥克風會畫斜線並顯示一行提示；判斷用 `micBlockedByMute = audioDeviceSettingsSupported && uiState.audioMicMuted`，所以手機端恆不觸發（那顆鈕在手機不存在）。
  已知偏差：發出去的是 `m.audio` 不是 MSC3245 的 `m.voice`（Trixnity 5.8.1 `RoomMessageEventContent.Serializer` 把 msgtype 寫死），Element 端顯示成一般音訊氣泡。ffplay 路徑拿不到進度/長度（Clip 路徑有）。
- **P5-2 檔案/圖片**：附件選擇兩端都有、`sendFile`/`sendImage` 完整；圖片有全螢幕檢視器＋另存（`ImageSaving` expect/actual）。
  **影片≠播放**：`m.video` 只解出第一幀當縮圖（`VideoFrame.android/desktop`），點開還是圖，唯一動作是「存檔」。其他 `m.file`/音訊檔只有檔名列，`Attachment` 的註解就直白寫著還沒做內建播放。
- **P5-3 URL 預覽**：`UrlPreview` + `UrlPreviewInline` 客戶端自己抓 OG/twitter meta（不走伺服器 `preview_url`）。限制：只預覽每則訊息的**第一個** URL、**不含縮圖**、只讀前 96KB、快取在記憶體且會一直長大。
- **P5-4 自訂表情（MSC2545）**：讀 `image_pack`（個人 account data ＋房間 state ＋伺服器 fallback）、貼圖面板 Emoticon 分頁、以 `<img data-mx-emoticon>` 的 `formatted_body` 送出並內聯渲染。
  **缺口**：不能拿自訂表情當 reaction（`sendReaction` 只發 unicode），反應面板仍是 `ChatScreen.QuickReactions` 那幾個硬編選項，「完整表情選擇器還沒做」。
- **P5-5 貼圖面板等高鍵盤**：Android 用 `WindowInsets.ime` 高度（面板與鍵盤互斥時先以 `SideEffect` 記底高）；桌面 `ime` 恆 0，靠 `coerceAtLeast(300.dp)` 的下限。當初擔心的「CMP 桌面沒有 actual、要 expect/actual 分平台」最後不需要。
- 托盤（桌面）：AWT `TrayIcon` ＋ Arcaea 金星圖示、菜單用獨立 `Window`（`ba207eb`：Compose `Popup` 在 `application{}` 裡沒有 `LocalHostDefaultProvider`，右鍵托盤圖標會直接崩）、LCD 子像素抗鋸齒、XGrabKey 假失敗已處理。
- 其它：全域快捷鍵、跳到最新浮動鈕（新訊息不再硬拽走正在翻歷史的人）、日期分隔、回覆上下文列、SSO 站內回呼、斷線重試、登入還原改用資料庫 token（`authProviderData=null`）。

### 未開始（零痕跡，別再去找）
P6-1 threads、P6-2 位置分享、P6-3 polls、P6-8 語音/視訊通話（無 WebRTC/MatrixRTC/jitsi 依賴）。
- **P6-4 push 通知**：沒有 UnifiedPush。已有的是「執行期通知」：push-rule 驅動的 `Notifications.watchNotifications`（用 `client.notification.getNotifications()`，不用未讀旗標，原因見檔內註解）＋ Android `SyncService` 前台服務／`POST_NOTIFICATIONS`／桌面 `notify-send`。程式被殺就不會收到。
- **P6-5 Layan/Kvantum 配色跟隨**：`XdgColorScheme.desktop.kt` 只偵測「深/淺」（KDE colorScheme、GTK prefer-dark、主題名含 `-dark`）＋檔案監聽；**色板/emphasis 色尚未抽取**。Android 走 Material You（SDK≥31）。
- **P6-6 自動發起裝置驗證**：SAS 狀態機、來電式驗證請求、根層 `DeviceVerificationHost`、手動「要求驗證此裝置」都有；缺 TrustedDeviceDetector 那種「掃描可交叉簽章的未驗證裝置並主動發起」。
- **P6-7 zh-HK**：`ZhHkStrings` 已註冊可選，但**零覆寫**（目前與 zh-TW 全同）。語言檔在 `i18n/Strings{En,Ja,Ko,ZhTw}.kt`（`Strings.kt` 只是介面），zh-CN 有覆寫少數鍵。

## 建置與驗證
- 本地：`./gradlew desktopApp:run`、`:desktopApp:createDistributable`、`:androidApp:assembleDebug`。
- 共享模組型別檢查任務名是 **`:shared:compileKotlinDesktop`** 與 **`:shared:compileAndroidMain`**（不是 `compileDebugKotlinAndroid`：android 區塊用 `com.android.kotlin.multiplatform.library`）。
- CI（`build.yml`，每次 push）：`./gradlew :androidApp:assembleDebug :desktopApp:packageDistributionForCurrentOS --no-daemon`。`release.yml` 另跑 `assembleRelease` ＋ Arch 容器 job（`packaging/PKGBUILD`，吃 `NASHIRA_VERSION`/`NASHIRA_DIST`）。
- 無 formatter/linter（沒有 spotless/ktlint/detekt）、無測試來源。`gradle.properties` 已限制 `org.gradle.jvmargs=-Xmx2048m`。
- 編譯依常規：`nice -n19 ionice -c3`、`--no-daemon`，跑完確認沒有殘留 java/Kotlin daemon、`git status` 乾淨（`build/` 已 ignore）。

## 慣例（改碼前先看）
- 平台差異一律用 `theme/PlatformCapabilities.kt` 的 `expect val xxxSupported: Boolean` ＋ `androidMain/desktopMain` 兩個 actual（例：`audioDeviceSettingsSupported`、`keyboardLayoutSettingsSupported`、`dynamicColorSupported`）。這是既成模式，別再散 `isAndroid()` 判斷。
- `LocalUiState` 是 `staticCompositionLocalOf`：**只能在 composable 作用域讀 `.current`**，放進 `onClick` lambda 會撞 `@Composable invocations can only happen from the context ...`；要在事件裡改 UiState，就在 composable 頂部抓一個區域變數（例：`ChatScreen` 的 `uiState`）。
- `UiState` 是 `App.kt` 裡那個 `var` 欄位全在記憶體＋本地持久化的類別，沒有 ViewModel；平台旗標要同步給 `AudioSelection` 之类的全域 sink 才會有即時效應。
- 媒體上傳一律「`prepareUploadMedia`/`prepareUploadEncryptedMedia` → `uploadMedia(cacheUri)`」兩步（見 P4-3 的坑）。
- 音訊裝置分兩層，別混用：`AudioDevice.id` 是存進設定、給平台開線用的識別名（桌面＝javax.sound 的 Mixer 名，形如 `Generic_1 [plughw:1,0]`），`label` 才是給人看的。顯示一律走 `audioDevicesFor()`／`audioDeviceLabel()`（底欄面板與設置頁共用），桌面 actual 再用 `pactl` 把 `alsa.card`/`alsa.device` 對回 PipeWire 節點描述（「Ryzen HD Audio Controller Speaker」），對不上退回 `Mixer.Info` 描述，再沒有才顯示 id——**不要把 id 直接印到 UI**。
- 沒有 logger：失敗路徑可以留 `println`，成功路徑與高頻事件（hover、每則訊息、每幀）不要印。

## 環境事實（2026-09-24 核對）
- SchildiChat 參考源碼 `/tmp/schildi` **已不在**（/tmp 清空）；需要時重 clone。
- Gradle 快取 `~/.gradle/caches/9.7.1/transforms` 確實存在（另有 `9.5.0-milestone-7`）。
- 電池提醒仍在：`~/.local/bin/battery-alert.sh` ＋ `~/.config/autostart/battery-alert.desktop`。

## 規則提醒（與全域 AI_RULES.md 同步）
- 規則 60：遠端操作用戶桌面前先 `notify-send`。
- 規則 55/63（2026-09-19 反轉）：**CI 由代理盯到全綠**，失敗自己抓日誌、修復、重推；涉及編譯測試的倉庫要兩種途徑都過（本地照 CI 定義實跑＋Actions 綠燈）。
- 規則 66：只有「不可逆」與「會花錢」兩類要先問；改檔、本地 commit、推自己倉庫主分支、開/回 issue 與 PR、清構建殘留都是默認核准。嚴禁把修改推到別人的倉庫。
- 規則 68/69：一輪任務做到「只剩盯 CI」就收尾（寫明未推的东西＋待驗證清單＋下次接手入口），深夜收尾時順帶 `sudo poweroff`。
- 規則 57：編譯期間 available RAM 保持 ≥ 1/4～1/3；`nice -n19 ionice -c3`、gradle `--no-daemon`。

## Trixnity 5.8.1 API 備忘
- `client.di.get<GlobalAccountDataStore>()` 拿全局 account data 流；`client.di.get<MediaService>()`（`client.media` 不存在）。
- `client.api.user.setProfileField(userId, field)` 不回 Result（suspend Unit）；`ProfileField.DisplayName(name)` / `AvatarUrl(mxcUrl)` 是 inline class。
- `profile?.displayName` 需 `import de.connect2x.trixnity.clientserverapi.model.user.displayName`。
- 訊息內容的 msgtype 分發寫死在 `RoomMessageEventContent.Serializer` → 帶不上 `m.voice` 這類自訂 msgtype。
- 已讀標記：`setReadMarkers(roomId, fullyRead, read)`，兩個都給才是 Element 的「標記為已讀」。
