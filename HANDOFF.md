# Nashira 交接文檔（2026-09-10 導出）

## 當前狀態
- 倉庫：`~/Downloads/nashira`，master @ `76fb1d4`（已推送）
- nb4a：`~/Downloads/NekoBoxForAndroid`，main @ `78c6812c7`（已推送）
- sing-box：`~/Downloads/sing-box`，1.15.x @ `52139a98b`（已推送）
- 手機：`9e91021c`（adb 可用，可能需 kill-server/start-server）
- Gradle daemon 已清（java=0）

## 待辦清單（從「功能性至少大於等於 element 系」原始需求展開）

### P4 快速件（進行中，API 層全綠，UI 部分待完成）
- [x] P4-1 屏蔽用戶：API + 訊息選單項 ✅（但時間線過濾被屏蔽者訊息未做——ignoredUsers() 流已可用，接入 TimelinePane 過濾即可）
- [x] P4-2 fully_read marker：markFullyRead + 房間選單 ✅
- [x] P4-3 顯示名稱編輯：setDisplayName + 帳戶安全頁 UI ✅（頭像上傳 setAvatar API 已有但 UI 未接）
- [~] P4-4 訊息格式化：MessageBody.Text.formattedBody 欄位已加（`76fb1d4`），**UI 渲染端未做**——需要在 MessageRow 的 Text 顯示處解析 HTML（粗體/斜體/代碼/連結），CMP 無現成 HTML 渲染器，可參考 SchildiChat 的 Markwon 或自寫 AnnotatedString 簡易解析器

### P5 中等件（未開始）
- [ ] P5-1 語音訊息（錄音+播放）
- [ ] P5-2 檔案/影片播放驗證（sendFile 已有）
- [ ] P5-3 URL 預覽
- [ ] P5-4 自訂表情（custom emoji，MSC2545 相關）
- [ ] P5-5 等高鍵盤貼圖面板（imePadding 已修通，StickerPicker 高度改 WindowInsets.ime 即可——但 CMP 桌面無此 actual，需 expect/actual 分平台）

### P6 大件（未開始）
- [ ] P6-1 訊息串 threads
- [ ] P6-2 位置分享
- [ ] P6-3 投票 polls
- [ ] P6-4 push 通知（unifiedpush）
- [ ] P6-5 Layan/Kvantum 配色跟隨（深淺已有 XdgColorScheme.desktop.kt，色板未做）
- [ ] P6-6 自動發起裝置驗證（Element TrustedDeviceDetector 等價）
- [ ] P6-7 zh-HK 用語差異覆寫
- [ ] P6-8 語音/視訊通話

## 最近完成的關鍵工作（本 session）

### 輸入法貼合三連修（`be46524`）
根因鏈（843px 縫隙）：
1. Manifest 未設 `windowSoftInputMode` → ROM 預設 adjustPan → 設 `adjustResize`
2. `enableEdgeToEdge()` + `window.setDecorFitsSystemWindows(false)` → IME insets 進 Compose
3. 貼圖面板改 Scaffold content 疊層（不撐 bottomBar）
4. bottomBar 單一 `imePadding()`（去掉 navigationBarsPadding 疊加）
5. `contentWindowInsets = WindowInsets(0,0,0,0)`

### 斷電恢復
- Gradle transforms 快取鎖損壞 → `~/.gradle/caches/9.7.1/transforms` 整清重建
- 電池提醒腳本已裝：`~/.local/bin/battery-alert.sh` + XDG autostart

## 規則提醒
- 規則 60：切換電腦工作區前先發 notify-send 告知用戶
- 規則 55：CI 由用戶監視，代理到 push + ls-remote 驗證為止
- 規則 57：編譯 RAM 門檻，nice -n19 ionice -c3 + --no-daemon -Xmx2G
- 構建後 pkill java daemon 到 0
- 真機驗證用 uiautomator dump（不耗視覺配額——本輪視覺配額已耗盡）

## Trixnity 5.8.1 API 備忘
- `client.di.get<GlobalAccountDataStore>()` 拿全局 account data 流
- `client.api.user.setProfileField(userId, field)` 不回 Result（直接 suspend Unit）
- `client.media` 不存在 → `client.di.get<MediaService>()`
- `ProfileField.DisplayName(name)` / `ProfileField.AvatarUrl(mxcUrl)` 是 inline class
- `profile?.displayName` 需要 `import de.connect2x.trixnity.clientserverapi.model.user.displayName` 擴展

## fork 艦隊狀態（2026-09-10 最新檢查）
- sing-tun 8 條新提交：全部已有或平台不適用（IPv6 ext header 我們是超集版 skipIPv6ExtensionHeaders、NEON=54360a9、auto redirect=bac2a6c、bypass port=8734bd4）
- sing-box 34 條：唯一真缺口 f90999708（bypass pre-match）已修 6dc057f45
- sing-mux v0.3.6：升 pin 失敗回退（idle connection API 重構需配套移植 0b50ce84a 系列）
- nb4a pin 已跟進 sing-box 52139a98b（78c6812c7）

## SchildiChat 參考源碼
- 克隆在 `/tmp/schildi`（可能被清——重跑 `git clone --depth 1 https://github.com/SpiritCroc/schildichat-android.git schildi`）
- FEATURES.md 有完整 Element+SchildiChat 功能對照清單
