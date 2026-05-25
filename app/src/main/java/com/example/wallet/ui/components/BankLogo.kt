package com.example.wallet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Logo do banco — representação estilizada baseada na identidade visual real.
 * Usa tipografia + cores oficiais para máxima fidelidade sem assets externos.
 */
@Composable
fun BankLogo(
    bankName: String,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp
) {
    when (bankName.lowercase()) {
        "nubank" -> NubankLogo(modifier, size)
        "itaú", "itau" -> ItauLogo(modifier, size)
        "bradesco" -> BradescoLogo(modifier, size)
        "santander" -> SantanderLogo(modifier, size)
        "banco do brasil" -> BancoDoBrasilLogo(modifier, size)
        "caixa" -> CaixaLogo(modifier, size)
        else -> GenericBankLogo(bankName, modifier, size)
    }
}

/**
 * Nubank — Fundo roxo com "Nu" em branco (tipografia do logo real).
 */
@Composable
private fun NubankLogo(modifier: Modifier = Modifier, size: Dp = 32.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFF8A05BE)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Nu",
            color = Color.White,
            fontSize = (size.value * 0.38f).sp,
            fontWeight = FontWeight.ExtraBold,
            fontStyle = FontStyle.Italic,
            letterSpacing = (-0.5).sp
        )
    }
}

/**
 * Itaú — Fundo azul escuro com texto "Itaú" em laranja (simplificado).
 */
@Composable
private fun ItauLogo(modifier: Modifier = Modifier, size: Dp = 32.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.2f))
            .background(Color(0xFF003399)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "iT",
            color = Color(0xFFFF7900),
            fontSize = (size.value * 0.4f).sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-1).sp
        )
    }
}

/**
 * Bradesco — Fundo vermelho com "B" branco bold.
 */
@Composable
private fun BradescoLogo(modifier: Modifier = Modifier, size: Dp = 32.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFFCC092F)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "B",
            color = Color.White,
            fontSize = (size.value * 0.5f).sp,
            fontWeight = FontWeight.Black
        )
    }
}

/**
 * Santander — Fundo vermelho com "S" branco bold.
 */
@Composable
private fun SantanderLogo(modifier: Modifier = Modifier, size: Dp = 32.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFFEC0000)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "S",
            color = Color.White,
            fontSize = (size.value * 0.5f).sp,
            fontWeight = FontWeight.Black
        )
    }
}

/**
 * Banco do Brasil — Fundo amarelo com "BB" em azul.
 */
@Composable
private fun BancoDoBrasilLogo(modifier: Modifier = Modifier, size: Dp = 32.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFFF9D70B)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "BB",
            color = Color(0xFF003882),
            fontSize = (size.value * 0.35f).sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-1).sp
        )
    }
}

/**
 * Caixa — Fundo azul com "CX" em laranja.
 */
@Composable
private fun CaixaLogo(modifier: Modifier = Modifier, size: Dp = 32.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.18f))
            .background(Color(0xFF005CA5)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "CX",
            color = Color(0xFFF37021),
            fontSize = (size.value * 0.35f).sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-0.5).sp
        )
    }
}

/**
 * Logo genérico — inicial em círculo cinza.
 */
@Composable
private fun GenericBankLogo(bankName: String, modifier: Modifier = Modifier, size: Dp = 32.dp) {
    val initial = bankName.firstOrNull()?.uppercase() ?: "?"
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFF455A64)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = Color.White,
            fontSize = (size.value * 0.45f).sp,
            fontWeight = FontWeight.Bold
        )
    }
}

