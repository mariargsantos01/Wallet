package com.example.wallet.model

import androidx.compose.ui.graphics.Color

data class BankAccount(
    val name: String,
    val color: Color,
    val isConnected: Boolean,
    val networks: List<String>
)
