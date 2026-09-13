plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "io.github.capricornus007.nashira"
    compileSdk = 37

    defaultConfig {
        applicationId = "io.github.capricornus007.nashira"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
    }

    buildFeatures {
        compose = true
    }

    // 正式簽名：CI 經 RELEASE_KEYSTORE/STORE_PASSWORD/KEY_ALIAS/KEY_PASSWORD 機密注入
    // （KEYSTORE_FILE 指向解出的 jks，相對本模組目錄）。缺任一項時退回 debug 簽名
    // ——本地開發與真機覆蓋安裝不受影響。
    val releaseKeystore = System.getenv("KEYSTORE_FILE")?.let { file(it) } ?: file("release.jks")
    val hasReleaseSigning =
        releaseKeystore.exists() && !System.getenv("STORE_PASSWORD").isNullOrBlank()

    if (hasReleaseSigning) {
        signingConfigs {
            create("release") {
                storeFile = releaseKeystore
                storePassword = System.getenv("STORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
                enableV1Signing = true
                enableV2Signing = true
                enableV3Signing = true
            }
        }
    }

    buildTypes {
        release {
            // 對齊 InstallerX 流暢度的關鍵：R8 全優化（簽名見上：有金鑰用正式，否則 debug）
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = if (hasReleaseSigning) signingConfigs.getByName("release")
            else signingConfigs.getByName("debug")
        }
    }

    packaging {
        // Trixnity JAR 內帶 kotlin/native linkdata 檔（Android 用不到），多模組撞名
        resources.excludes += listOf("**/linkdata/**", "**/default/manifest", "META-INF/kotlin-project-structure-metadata.json")
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation(libs.androidx.activity.compose)
    implementation("androidx.compose.foundation:foundation:1.12.0")
    implementation("androidx.core:core-ktx:1.17.0")
}
