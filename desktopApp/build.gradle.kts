import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":shared"))
    implementation(compose.desktop.currentOs)
    // 讓 Trixnity 的 lognity/SLF4J 日誌真正輸出（否則 desktop 端 NOP 看不到任何錯誤）
    runtimeOnly("org.slf4j:slf4j-simple:2.0.16")
}

compose.desktop {
    application {
        mainClass = "io.github.capricornus007.nashira.desktop.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Deb, TargetFormat.Rpm)
            packageName = "nashira"
            packageVersion = "0.1.0"
            description = "Nashira — Matrix messenger for Android and Linux"
            vendor = "Capricornus007"
            linux {
                iconFile.set(project.file("packaging/nashira-512.png"))
            }
        }
    }
}
