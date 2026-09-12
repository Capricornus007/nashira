# Nashira

Kotlin / Compose Multiplatform 的 Matrix 客戶端（Android + Linux 桌面），Discord 式介面。

- 帳號：密碼 / Matrix SSO 登入，登入態持久化（重啟免重登）
- 訊息：文字（HTML formatted_body）、圖片、貼圖（MSC2545 貼圖包）、語音訊息、反應、回覆、編輯、撤回、轉寄、圖釘、全文搜尋
- 端對端加密（vodozemac）、交叉簽署與 SAS 裝置驗證
- 六語介面（zh-TW / zh-HK / zh-CN / en / ja / ko）、Material You 主題

## 下載

[Releases](https://github.com/Capricornus007/nashira/releases) 提供：

| 平台 | 格式 |
|---|---|
| Android | `.apk`（debug 簽章，可直接側載） |
| Debian / Ubuntu 系 | `.deb` |
| Fedora / RHEL / SUSE 系 | `.rpm` |
| Arch 系 | `.pkg.tar.zst` |

## 其他發行版：自行構建

需要 **JDK 21+**（Android 另需 Android SDK；桌面端不需要）。

```bash
git clone https://github.com/Capricornus007/nashira
cd nashira

# 桌面端直接跑
./gradlew desktopApp:run

# 桌面端出可分發目錄（自帶 JRE 映像，解壓即用）
./gradlew desktopApp:createDistributable
# 產物在 desktopApp/build/compose/binaries/main-dist/nashira/

# Android debug APK
./gradlew androidApp:assembleDebug
# 產物在 androidApp/build/outputs/apk/debug/
```

不想用包管理器格式的發行版（Alpine、NixOS、獨立 musl 等）：用上面的
`createDistributable`，解壓 `main-dist/nashira` 後執行 `bin/nashira` 即可。

## 授權

AGPL-3.0（見 [LICENSE](LICENSE)）。矩陣協議實作基於 [Trixnity](https://gitlab.com/connect2x/trixnity)。
