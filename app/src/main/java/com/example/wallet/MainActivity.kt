package com.example.wallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.wallet.navigation.AppNavHost
import com.example.wallet.navigation.Routes
import com.example.wallet.ui.screens.SplashScreen
import com.example.wallet.ui.theme.WalletTheme
import com.example.wallet.utils.ServiceLocator
import com.example.wallet.utils.ThemeMode
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializa o banco Room + repositories antes de qualquer ViewModel.
        ServiceLocator.init(applicationContext)

        // Valida a sessão: se o userId salvo não existe mais no DB (destructive migration),
        // limpa a sessão para forçar login novamente.
        validateSession()

        var keepNativeSplash = true
        splash.setKeepOnScreenCondition { keepNativeSplash }

        setContent {
            val themeMode by ServiceLocator.themePreferences.themeMode.collectAsState()
            val isDark = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            WalletTheme(darkTheme = isDark) {
                var showSplash by remember { mutableStateOf(true) }
                keepNativeSplash = false

                if (showSplash) {
                    SplashScreen(onFinished = { showSplash = false })
                } else {
                    // Decide a rota inicial baseado na sessão válida
                    val hasValidSession = ServiceLocator.sessionManager.getCurrentUserId() != null
                    val startRoute = if (hasValidSession) {
                        ServiceLocator.startPurchaseSimulator()
                        Routes.MyCards.route
                    } else {
                        Routes.Login.route
                    }
                    AppNavHost(startDestination = startRoute)
                }
            }
        }
    }

    /**
     * Verifica se o userId persistido ainda existe no banco.
     * Se o DB passou por destructive migration, o userId antigo não existe mais.
     */
    private fun validateSession() {
        val userId = ServiceLocator.sessionManager.getCurrentUserId() ?: return
        // Verifica de forma síncrona (rápida, dado local)
        val exists = runBlocking {
            try {
                ServiceLocator.userRepository.getCurrentUser() != null
            } catch (_: Exception) {
                false
            }
        }
        if (!exists) {
            ServiceLocator.sessionManager.clear()
        }
    }
}
