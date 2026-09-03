package io.github.capricornus007.nashira.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import io.github.capricornus007.nashira.App
import io.github.capricornus007.nashira.appContext
import io.github.capricornus007.nashira.matrix.TokenStorage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContext = applicationContext
        TokenStorage.context = applicationContext
        setContent {
            App() // 主題模式改由 App 內部管理（追隨系統/深/淺），不傳 defaultDark
        }
    }
}
