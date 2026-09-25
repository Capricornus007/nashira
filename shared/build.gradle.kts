import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// ── 版號單一來源 ────────────────────────────────────────────────────────────
// 「設定→關於」以前讀的是 AppInfo 裡寫死的 "0.1.0"，誰都沒記得改，結果 v0.1.10
// 的套件還顯示 0.1.0（用戶 2026-09-25 實測點名）。改成建置時從 gradle.properties
// 產生常數：版號只存在那一行，這裡（和兩個 app 模組、release.yml）全部跟著走。
val appVersionValue = providers.gradleProperty("nashiraVersion").get()
val engineVersionValue = libs.versions.trixnity.get()
val generatedVersionDir = layout.buildDirectory.dir("generated/nashiraVersion")

val generateAppVersion = tasks.register("generateAppVersion") {
    group = "nashira"
    description = "產生 AppInfo 用的版號／引擎常數（別手改生成物）"
    inputs.property("nashiraVersion", appVersionValue)
    inputs.property("trixnity", engineVersionValue)
    outputs.dir(generatedVersionDir)
    doLast {
        val out = generatedVersionDir.get()
            .file("io/github/capricornus007/nashira/AppVersion.kt").asFile
        out.parentFile.mkdirs()
        out.writeText(
            "// 建置時由 :shared:generateAppVersion 產生，別手改。\n" +
                "package io.github.capricornus007.nashira\n" +
                "\n" +
                "const val APP_VERSION: String = \"$appVersionValue\"\n" +
                "const val APP_ENGINE: String = \"Trixnity $engineVersionValue\"\n",
        )
    }
}

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose)
}

kotlin {
    android {
        namespace = "io.github.capricornus007.nashira.shared"
        compileSdk = 37
        minSdk = 26
    }
    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
        compilations.all {
            dependencies {
                implementation(libs.ktor.client.okhttp)
                // Room KMP 在 JVM 必須顯式給 SQLite 驅動（Android 自帶 framework 版）
                implementation("androidx.sqlite:sqlite-bundled-jvm:2.6.2")
            }
        }
    }

    sourceSets {
        val androidMain by getting
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            // Android 直依 vodozemac android 構件（root 的 available-at 變體協商
            // 在嵌套傳遞時丟失 jvm-environment 屬性——Gradle 已知問題）
            implementation("de.connect2x.trixnity:trixnity-vodozemac-android:${libs.versions.trixnity.get()}")
            implementation("de.connect2x.trixnity:trixnity-vodozemac-binaries-android:${libs.versions.trixnity.get()}")
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.animation)
            implementation(compose.ui)
            implementation(libs.cmp.material3)
            implementation(libs.material.kolor)
            implementation(libs.kotlinx.datetime)
            // MSC2545 自訂 content（m.sticker / im.ponies.*）需要 serialization 插件與 runtime；
            // runtime 用 Trixnity 傳遞帶入的版本，避免雙版本
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.11.0")
            implementation("org.jetbrains.compose.material:material-icons-core:1.7.3")
            // kotlin-wrappers:kotlin-browser 只有 js/wasm 變體，Android/JVM 解析會炸（Trixnity POM 傳遞帶入），排除
            implementation("de.connect2x.trixnity:trixnity-client:${libs.versions.trixnity.get()}") {
                exclude(group = "org.jetbrains.kotlin-wrappers")
            }
            // 三個存儲模組也帶入 trixnity-client（同樣需要排除 kotlin-wrappers）
            implementation("de.connect2x.trixnity:trixnity-client-repository-room:${libs.versions.trixnity.get()}") {
                exclude(group = "org.jetbrains.kotlin-wrappers")
            }
            implementation("de.connect2x.trixnity:trixnity-client-media-okio:${libs.versions.trixnity.get()}") {
                exclude(group = "org.jetbrains.kotlin-wrappers")
            }
            implementation("de.connect2x.trixnity:trixnity-client-cryptodriver-vodozemac:${libs.versions.trixnity.get()}") {
                exclude(group = "org.jetbrains.kotlin-wrappers")
            }
        }
    }
}

// 產生式常數進 commonMain，Android 與 desktop 兩個目標才都看得到
kotlin.sourceSets.getByName("commonMain").kotlin.srcDir(generateAppVersion)
