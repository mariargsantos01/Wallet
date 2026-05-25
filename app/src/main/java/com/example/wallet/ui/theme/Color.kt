package com.example.wallet.ui.theme

import androidx.compose.ui.graphics.Color

// =====================================================================
// Design System – Paleta principal (enriquecida com acentos sutis)
// =====================================================================

// ─── DARK THEME ──────────────────────────────────────────────────────

// Backgrounds / Surfaces — Hierarquia de profundidade
val Background = Color(0xFF0A1628)           // Fundo principal mais profundo
val BackgroundElevated = Color(0xFF0D1B30)   // Fundo com leve elevação
val Foreground = Color(0xFFFFFFFF)           // Texto principal sobre fundo escuro
val CardBg = Color(0xFF0F2035)              // Card base
val CardBgElevated = Color(0xFF122A45)      // Card com maior destaque
val CardForeground = Color(0xFFFFFFFF)       // Texto sobre card
val Popover = Color(0xFF132742)              // Popover / modal
val PopoverForeground = Color(0xFFFFFFFF)    // Texto sobre popover

// Brand — Ciano principal
val Primary = Color(0xFF00BCD4)              // Ciano principal
val PrimaryDark = Color(0xFF0097A7)          // Variante mais escura do primary
val PrimaryLight = Color(0xFF4DD0E1)         // Variante mais clara do primary
val PrimaryForeground = Color(0xFF000000)    // Texto sobre primary
val PrimaryContainer = Color(0xFF0D3B47)     // Container com tom da primary
val OnPrimaryContainer = Color(0xFF80DEEA)   // Texto sobre primaryContainer

// Secondary — Violeta/Púrpura sutil (complemento do ciano)
val Secondary = Color(0xFF2D1B4E)            // Púrpura escuro de fundo
val SecondaryLight = Color(0xFFBB86FC)       // Púrpura claro para destaques
val SecondaryForeground = Color(0xFFEDE7F6)  // Texto sobre secondary

// Tertiary — Dourado/Amber quente (contraste com os tons frios)
val Tertiary = Color(0xFFFFB74D)             // Amber/Dourado
val TertiaryDark = Color(0xFFF57C00)         // Amber escuro
val TertiaryContainer = Color(0xFF3D2E0A)    // Container amber
val OnTertiaryContainer = Color(0xFFFFE0B2)  // Texto sobre tertiaryContainer
val TertiaryForeground = Color(0xFF1A1000)   // Texto escuro sobre tertiary

// Accent (mantido como ciano para retrocompatibilidade)
val Accent = Color(0xFF00BCD4)
val AccentForeground = Color(0xFF000000)

// Muted — Tons neutros azulados
val Muted = Color(0xFF1A3A5C)
val MutedForeground = Color(0xFF8BA3C0)

// Superfícies com variações sutis de profundidade
val SurfaceDim = Color(0xFF081220)           // Superfície mais escura
val SurfaceBright = Color(0xFF162D4A)        // Superfície mais clara

// Feedback
val Destructive = Color(0xFFEF5350)
val DestructiveForeground = Color(0xFFFFFFFF)
val DestructiveContainer = Color(0xFF3D1212)
val Success = Color(0xFF10B981)
val SuccessContainer = Color(0xFF0D3326)
val Warning = Color(0xFFF59E0B)
val WarningContainer = Color(0xFF3D2E0A)

// Bordas / inputs
val BorderColor = Color(0xFF1E4976)
val BorderSubtle = Color(0xFF15344F)         // Borda mais sutil
val InputBg = Color(0xFF132742)
val Ring = Color(0xFF00BCD4)

// ─── LIGHT THEME ─────────────────────────────────────────────────────

val LightBackground = Color(0xFFF8FAFB)
val LightBackgroundElevated = Color(0xFFFFFFFF)
val LightForeground = Color(0xFF0A1628)
val LightCardBg = Color(0xFFFFFFFF)
val LightCardBgElevated = Color(0xFFF1F5F9)
val LightCardForeground = Color(0xFF0A1628)
val LightPopover = Color(0xFFFFFFFF)
val LightPopoverForeground = Color(0xFF0A1628)

val LightPrimaryContainer = Color(0xFFE0F7FA)
val LightOnPrimaryContainer = Color(0xFF006064)

val LightSecondary = Color(0xFFF3E5F5)       // Lilás suave
val LightSecondaryForeground = Color(0xFF4A148C)
val LightMutedForeground = Color(0xFF5A7A9A)

val LightTertiaryContainer = Color(0xFFFFF3E0) // Amber claro
val LightOnTertiaryContainer = Color(0xFFE65100)

val LightSurfaceDim = Color(0xFFE8EDF2)
val LightSurfaceBright = Color(0xFFFFFFFF)

val LightDestructiveContainer = Color(0xFFFFEBEE)
val LightSuccessContainer = Color(0xFFE8F5E9)
val LightWarningContainer = Color(0xFFFFF8E1)

val LightBorderColor = Color(0xFFD0DCE8)
val LightBorderSubtle = Color(0xFFE2EAF2)
val LightInputBg = Color(0xFFF1F5F9)

// =====================================================================
// Aliases retrocompatíveis
// =====================================================================
val AzulPrimario = Primary
val FundoPrincipal = Background
val FundoSecundario = CardBg
val CinzaEscuro = InputBg
val Branco = Foreground
val CinzaTexto = MutedForeground
val Verde = Success
val Vermelho = Destructive
val AmareloDourado = Warning
