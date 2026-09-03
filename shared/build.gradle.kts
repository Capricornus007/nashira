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
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.animation)
            implementation(compose.ui)
            implementation(libs.cmp.material3)
            implementation(libs.material.kolor)
            // kotlin-wrappers:kotlin-browser 只有 js/wasm 變體，Android/JVM 解析會炸（Trixnity POM 傳遞帶入），排除
            implementation("de.connect2x.trixnity:trixnity-client:${libs.versions.trixnity.get()}") {
                exclude(group = "org.jetbrains.kotlin-wrappers")
            }
        }
    }
}
