package com.example.wallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.wallet.navigation.AppNavHost
import com.example.wallet.ui.screens.SplashScreen
import com.example.wallet.ui.theme.WalletTheme
import com.example.wallet.utils.ServiceLocator

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // SplashScreen API – exibe o tema "Theme.Wallet.Splash" antes do Compose.
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializa o banco Room + repositories antes de qualquer ViewModel.
        ServiceLocator.init(applicationContext)

        // Mantém a splash nativa visível até o Compose começar a desenhar.
        var keepNativeSplash = true
        splash.setKeepOnScreenCondition { keepNativeSplash }

        setContent {
            WalletTheme {
                var showSplash by remember { mutableStateOf(true) }
                // Libera a splash nativa assim que o Compose começou a renderizar.
                keepNativeSplash = false

                if (showSplash) {
                    SplashScreen(onFinished = { showSplash = false })
                } else {
                    AppNavHost()
                }
            }
        }
    }
}
