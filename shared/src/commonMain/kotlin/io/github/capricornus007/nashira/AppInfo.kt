package io.github.capricornus007.nashira

object AppInfo {
    /** 版號不是寫死在這裡的——由 :shared:generateAppVersion 從 gradle.properties 產生。 */
    const val version = APP_VERSION
    const val engine = APP_ENGINE
    const val crypto = "vodozemac (Rust)"
    const val license = "AGPL-3.0"
    const val repo = "Capricornus007/nashira"

    const val repoUrl = "https://github.com/Capricornus007/nashira"
    const val engineUrl = "https://gitlab.com/connect2x/trixnity/trixnity"
    const val cryptoUrl = "https://github.com/matrix-org/vodozemac"
    const val licenseUrl = "https://www.gnu.org/licenses/agpl-3.0.txt"
}
