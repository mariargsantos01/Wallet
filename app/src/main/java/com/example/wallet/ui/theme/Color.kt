package com.example.wallet.ui.theme

import androidx.compose.ui.graphics.Color

// =====================================================================
// Design System – Paleta principal (mapeada do CSS fornecido)
// =====================================================================

// Backgrounds / Surfaces
val Background = Color(0xFF0A1628)           // --background
val Foreground = Color(0xFFFFFFFF)           // --foreground
val CardBg = Color(0xFF0F2035)               // --card / --sidebar
val CardForeground = Color(0xFFFFFFFF)       // --card-foreground
val Popover = Color(0xFF132742)              // --popover / --input
val PopoverForeground = Color(0xFFFFFFFF)    // --popover-foreground

// Brand
val Primary = Color(0xFF00BCD4)              // --primary / --accent / --ring
val PrimaryForeground = Color(0xFF000000)    // --primary-foreground
val Accent = Color(0xFF00BCD4)               // --accent
val AccentForeground = Color(0xFF000000)     // --accent-foreground

// Secondary / Muted
val Secondary = Color(0xFF1A3A5C)            // --secondary / --muted
val SecondaryForeground = Color(0xFFFFFFFF)  // --secondary-foreground
val Muted = Color(0xFF1A3A5C)                // --muted
val MutedForeground = Color(0xFF8BA3C0)      // --muted-foreground

// Feedback
val Destructive = Color(0xFFEF5350)          // --destructive
val DestructiveForeground = Color(0xFFFFFFFF)// --destructive-foreground
val Success = Color(0xFF10B981)              // verde (mantido do tema antigo)
val Warning = Color(0xFFF59E0B)              // amarelo/dourado (mantido)

// Bordas / inputs / outlines
val BorderColor = Color(0xFF1E4976)          // --border / --sidebar-border
val InputBg = Color(0xFF132742)              // --input
val Ring = Color(0xFF00BCD4)                 // --ring

// =====================================================================
// Aliases retrocompatíveis (usados em código já existente)
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
