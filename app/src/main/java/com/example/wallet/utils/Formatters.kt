package com.example.wallet.utils

import java.text.NumberFormat
import java.util.Locale

object Formatters {

    private val currencyFormat: NumberFormat =
        NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    fun currency(value: Double): String = currencyFormat.format(value)
}

