package com.example.wallet.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Tokens de espaçamento padronizados (Design System).
 * Layout "respirado" – espaçamentos generosos entre componentes.
 * Use `WalletTheme.spacing.md` em vez de valores arbitrários.
 */
data class Spacing(
    val none: Dp = 0.dp,
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val smd: Dp = 12.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp,
    val xxxl: Dp = 64.dp
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }
