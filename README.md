# Nashira

Kotlin / Compose Multiplatform 的 Matrix 客戶端（Android + Linux 桌面），
介面採用 **Material 3 Expressive**（MD3E），Discord 式版面。

- 訊息：文字（HTML formatted_body）、圖片、貼圖（MSC2545 貼圖包）、
  custom emoji、語音訊息、反應、回覆、編輯、撤回、轉寄、圖釘、全文搜尋
- 連結預覽卡（og meta）
- 端對端加密（vodozemac）、交叉簽署與 SAS 裝置驗證
- 六語介面（zh-TW / zh-HK / zh-CN / en / ja / ko）
- MD3E 主題系統：動態取色、Arcaea 品牌色板、深色／純黑

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
# 產物在 desktopApp/build/compose/binaries/main/app/nashira/

# Android debug APK
./gradlew androidApp:assembleDebug
# 產物在 androidApp/build/outputs/apk/debug/
```

不想用包管理器格式的發行版（Alpine、NixOS、獨立 musl 等）：用上面的
`createDistributable`，解壓 `main/app/nashira` 後執行 `bin/nashira` 即可。

## 授權

AGPL-3.0（見 [LICENSE](LICENSE)）。矩陣協議實作基於 [Trixnity](https://gitlab.com/connect2x/trixnity)。
