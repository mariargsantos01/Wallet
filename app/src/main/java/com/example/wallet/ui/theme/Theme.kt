package com.example.wallet.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AzulPrimario,
    onPrimary = Branco,
    background = FundoPrincipal,
    onBackground = Branco,
    surface = FundoSecundario,
    onSurface = Branco,
    surfaceVariant = CinzaEscuro,
    onSurfaceVariant = CinzaTexto,
    outline = CinzaTexto
)

@Composable
fun WalletTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
