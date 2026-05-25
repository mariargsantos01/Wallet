package com.example.wallet.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

private val WalletDarkColorScheme: ColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = PrimaryForeground,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,

    secondary = Secondary,
    onSecondary = SecondaryForeground,
    secondaryContainer = Color(0xFF3D2560),
    onSecondaryContainer = SecondaryLight,

    tertiary = Tertiary,
    onTertiary = TertiaryForeground,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,

    background = Background,
    onBackground = Foreground,

    surface = CardBg,
    onSurface = CardForeground,
    surfaceVariant = InputBg,
    onSurfaceVariant = MutedForeground,
    surfaceTint = Primary,
    surfaceDim = SurfaceDim,
    surfaceBright = SurfaceBright,
    surfaceContainerLowest = Background,
    surfaceContainerLow = BackgroundElevated,
    surfaceContainer = CardBg,
    surfaceContainerHigh = CardBgElevated,
    surfaceContainerHighest = Popover,

    inverseSurface = Foreground,
    inverseOnSurface = Background,
    inversePrimary = PrimaryDark,

    error = Destructive,
    onError = DestructiveForeground,
    errorContainer = DestructiveContainer,
    onErrorContainer = Destructive,

    outline = BorderColor,
    outlineVariant = BorderSubtle,

    scrim = Background
)

private val WalletLightColorScheme: ColorScheme = lightColorScheme(
    primary = PrimaryDark,
    onPrimary = LightBackground,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,

    secondary = LightSecondary,
    onSecondary = LightSecondaryForeground,
    secondaryContainer = Color(0xFFEDE7F6),
    onSecondaryContainer = Color(0xFF4A148C),

    tertiary = TertiaryDark,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,

    background = LightBackground,
    onBackground = LightForeground,

    surface = LightCardBg,
    onSurface = LightCardForeground,
    surfaceVariant = LightInputBg,
    onSurfaceVariant = LightMutedForeground,
    surfaceTint = PrimaryDark,
    surfaceDim = LightSurfaceDim,
    surfaceBright = LightSurfaceBright,
    surfaceContainerLowest = LightBackground,
    surfaceContainerLow = LightBackgroundElevated,
    surfaceContainer = LightCardBg,
    surfaceContainerHigh = LightCardBgElevated,
    surfaceContainerHighest = LightPopover,

    inverseSurface = LightForeground,
    inverseOnSurface = LightBackground,
    inversePrimary = PrimaryLight,

    error = Destructive,
    onError = DestructiveForeground,
    errorContainer = LightDestructiveContainer,
    onErrorContainer = Destructive,

    outline = LightBorderColor,
    outlineVariant = LightBorderSubtle,

    scrim = LightForeground
)

@Composable
fun WalletTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) WalletDarkColorScheme else WalletLightColorScheme

    CompositionLocalProvider(
        LocalSpacing provides Spacing()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
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
