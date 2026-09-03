import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.compose)
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
                implementation(libs.ktor.client.cio)
            }
        }
    }

    sourceSets {
        val androidMain by getting
        androidMain.dependencies {
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
