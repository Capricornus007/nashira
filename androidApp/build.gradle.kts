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

    buildTypes {
        release {
            // 對齊 InstallerX 流暢度的關鍵：R8 全優化 + debug 簽名（自用分發）
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    packaging {
        // Trixnity JAR 內帶 kotlin/native linkdata 檔（Android 用不到），多模組撞名
        resources.excludes += listOf("**/linkdata/**", "**/default/manifest", "META-INF/kotlin-project-structure-metadata.json")
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.androidx.activity.compose)
    implementation("androidx.compose.foundation:foundation:1.12.0")
    implementation("androidx.core:core-ktx:1.17.0")
}
