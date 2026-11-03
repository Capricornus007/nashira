import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
}

kotlin {
    jvmToolchain(27)
}

dependencies {
    implementation(project(":shared"))
    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    testImplementation(kotlin("test"))
    // 讓 Trixnity 的 lognity/SLF4J 日誌真正輸出（否則 desktop 端 NOP 看不到任何錯誤）
    runtimeOnly("org.slf4j:slf4j-simple:2.0.16")
    // 全域快捷鍵（XGrabKey）：JNA 直連 Xlib，不依賴 WM/sxhkd 配置——
    // app 自己註冊（Firefox ctrl+q 同款哲學，2026-09-14 用戶定調）。
    implementation("net.java.dev.jna:jna:5.16.0")
    implementation("net.java.dev.jna:jna-platform:5.16.0")
}

configurations.configureEach {
    // The desktop artifacts declare this optional fallback API, but Nashira uses only stock AWT.
    exclude(group = "org.jetbrains.runtime", module = "jbr-api")
}

compose.desktop {
    application {
        mainClass = "io.github.capricornus007.nashira.desktop.MainKt"
        // X11Platform and the stock-OpenJDK XIM bridge access OpenJDK's stable X11/AWT internals.
        // Gradle run and packaged launchers need the same opens so behavior cannot diverge.
        jvmArgs += listOf(
            "--add-opens=java.desktop/sun.awt.X11=ALL-UNNAMED",
            "--add-opens=java.desktop/sun.awt=ALL-UNNAMED",
            "--add-opens=java.desktop/sun.awt.im=ALL-UNNAMED",
            // ── 執行期記憶體 ────────────────────────────────────────────────
            // jpackage 出廠**一個記憶體參數都沒有**，於是最大堆＝實體記憶體的 1/4
            //（13GB 機器＝3.3GB），GC 又是 G1，會先把地盤圈起來。
            // 實測 v0.1.62 開著兩個房間閒置 RSS = 606MB（用戶 2026-10-07 點名「運行佔用也要小」）。
            // 512MB 軟上限對一個聊天客戶是夠的（媒體快取本身另有 24MB 上限），
            // 超過 SoftMaxHeapSize 時 GC 先努力回收，真需要才長到上面那個硬頂。
            // 256m 軟頂／384m 硬頂是**量出來的**：同一組 G1，上限從 512/768 收到 256/384，
            // 實測 RSS 593MB → 452MB（NMT committed 387MB → 257MB），流暢度沒變。
            // 堆裡放的是資料模型，圖片的像素在 skia 那側（不吃 Java 堆），所以 384m 綽有餘。
            "-XX:SoftMaxHeapSize=256m",
            "-Xmx384m",
            "-XX:MaxMetaspaceSize=256m",
            "-XX:ReservedCodeCacheSize=96m",
            "-Xss512k",
            // GC 三種都量過（同一臺、同樣開兩個房間、啟動後 30～55 秒取樣 RSS）：
            //   Serial   → 296MB，但它的省法就是「一次停下掃完」，正是滾動最怕的那種暫停
            //              （用戶 2026-10-07 直接問「會不會卡死成 ppt 然後動畫全無」——會）。
            //   分代 ZGC → 826／866／873MB 還在爬，比出廠預設還差，淘汰。
            //   G1＋限量 → 600MB 級，翻頁實測「順暢了」→ 留這個。
            "-XX:+UseG1GC",
            // 閒置時主動回收並把記憶體「還給作業系統」。G1 預設回收完仍占著不放手，
            // 掛著當常駐客戶端時這筆很虛：加了這個，閒置一輪之後 RSS 會自己落下來。
            "-XX:G1PeriodicGCInterval=60000",
        )
        nativeDistributions {
            targetFormats(TargetFormat.Deb, TargetFormat.Rpm)
            // 捆進安裝包的 Java 執行期要哪些模組。這份清單是**抄現有公開發布包的實測結果**
            // （v0.1.36 的 .deb 內 lib/runtime/release 的 MODULES 欄就是這 7 個，而 app 跑得起來），
            // 不是我挑的——多寫會變大、少寫會啟動失敗，照抄已被證明夠用的那一份。
            //
            // 為什麼要明寫而不是讓 plugin 自己算：`modules()` 控制的是共用的那個 jlink task
            // （`getCreateRuntimeImage`），所以 .deb／.rpm 與 createDistributable（Arch 包吃這個）
            // 三條路才會用同一份執行期。
            // ⚠️ 不要改用 `runtimeImage` 去指向外部 jlink 產物：jpackage 的 runtimeImage 是
            //    plugin 內部接給那個 jlink task 的輸出，手動覆蓋會繞過它的 task 圖、
            //    而且管不到 distributable 那條路（1.12.0 反編譯 ConfigureJvmApplicationKt 查實）。
            //
            // The image is linked from the stock JDK selected by the Gradle toolchain. Candidate
            // positioning is supplied by XimCandidatePosition through standard Xlib XIM calls,
            // not by a vendor runtime patch, so this module list is runtime-vendor independent.
            // CI and the Arch recipe both assert that the bundled image is OpenJDK 27.
            modules(
                "java.base",
                "java.datatransfer",
                "java.xml",
                "java.prefs",
                "java.desktop",
                "java.logging",
                "jdk.crypto.ec",
                // 內嵌影片播放的本機串流代理（MediaStream.desktop.kt）用的是 com.sun.net.httpserver，
                // 不在上面那 7 個裡——少了它，裝出來的包一開影片就 NoClassDefFoundError，
                // 而且 CI 照綠（編譯期不看 runtime 模組清單）。
                "jdk.httpserver",
            )
            packageName = "nashira"
            // 版號單一來源＝gradle.properties 的 nashiraVersion（release.yml 打 tag 讀同一行）
            packageVersion = providers.gradleProperty("nashiraVersion").get()
            description = "Nashira — Matrix messenger for Android and Linux"
            vendor = "Capricornus007"
            linux {
                iconFile.set(project.file("packaging/nashira-512.png"))
            }
        }
    }
}
