package com.example.wallet.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.material3.ColorScheme

private val WalletDarkColorScheme: ColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = PrimaryForeground,
    primaryContainer = Secondary,
    onPrimaryContainer = SecondaryForeground,

    secondary = Secondary,
    onSecondary = SecondaryForeground,
    secondaryContainer = Secondary,
    onSecondaryContainer = SecondaryForeground,

    tertiary = Accent,
    onTertiary = AccentForeground,

    background = Background,
    onBackground = Foreground,

    surface = CardBg,
    onSurface = CardForeground,
    surfaceVariant = InputBg,
    onSurfaceVariant = MutedForeground,
    surfaceTint = Primary,

    inverseSurface = Foreground,
    inverseOnSurface = Background,
    inversePrimary = Primary,

    error = Destructive,
    onError = DestructiveForeground,
    errorContainer = Destructive,
    onErrorContainer = DestructiveForeground,

    outline = BorderColor,
    outlineVariant = BorderColor,

    scrim = Background
)

@Composable
fun WalletTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalSpacing provides Spacing()
    ) {
        MaterialTheme(
            colorScheme = WalletDarkColorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

/**
 * Accessor para tokens customizados do Design System.
 * Ex: `WalletTheme.spacing.md`, `WalletTheme.colors.primary`.
 */
object WalletTheme {
    val colors: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    val shapes: Shapes
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes

    val spacing: Spacing
        @Composable
        @ReadOnlyComposable
        get() = LocalSpacing.current
}
