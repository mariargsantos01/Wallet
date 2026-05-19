package com.example.wallet.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Baseado em --radius: 0.75rem = 12dp
// sm = radius - 4 = 8dp, md = radius - 2 = 10dp, lg = 12dp, xl = radius + 4 = 16dp
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small      = RoundedCornerShape(8.dp),   // --radius-sm
    medium     = RoundedCornerShape(10.dp),  // --radius-md
    large      = RoundedCornerShape(12.dp),  // --radius-lg (default)
    extraLarge = RoundedCornerShape(16.dp)   // --radius-xl
)

