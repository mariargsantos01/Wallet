package com.example.wallet.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wallet.model.CardModel

// Cores do cartão
private val ChipGold = Color(0xFFD4A847)
private val ChipGoldDark = Color(0xFFB8860B)
private val MastercardRed = Color(0xFFD50000)
private val MastercardOrange = Color(0xFFFF9800)
private val MastercardOverlap = Color(0xFFFF3D00)
private val ContactlessWhite = Color(0xFFCCCCCC)

/**
 * Card visual realista estilo Mastercard — chip dourado, logo,
 * contactless, número, validade e nome do titular.
 * Sombra profunda e degradê sutil.
 */
@Composable
fun CardItem(
    card: CardModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)

    // Cor base do banco selecionado
    val baseColor = Color(card.bankColor.toULong())
    val darkVariant = baseColor.copy(alpha = 0.7f)

    // Degradê sutil no fundo do cartão baseado na cor do banco
    val cardGradient = Brush.linearGradient(
        colors = listOf(
            baseColor,
            darkVariant,
            darkVariant,
            baseColor.copy(alpha = 0.9f)
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.586f) // Proporção de cartão real (85.6mm x 53.98mm)
            .shadow(
                elevation = 16.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.4f)
            )
            .clip(shape)
            .background(cardGradient)
            .clickable(onClick = onClick)
    ) {
        // Brilho sutil no canto superior esquerdo
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.03f),
                            Color.Transparent
                        ),
                        center = Offset(0f, 0f),
                        radius = 400f
                    )
                )
        )

        // ── Conteúdo do cartão ──
        Box(Modifier.fillMaxSize().padding(20.dp)) {

            // ─── Topo: Bandeira (canto superior direito) ───
            Text(
                text = card.brand.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.9f),
                letterSpacing = 2.sp,
                modifier = Modifier.align(Alignment.TopEnd)
            )

            // ─── Logo do banco (canto superior esquerdo, acima do chip) ───
            if (card.bankName.isNotBlank()) {
                BankLogo(
                    bankName = card.bankName,
                    size = 28.dp,
                    modifier = Modifier.align(Alignment.TopStart)
                )
            }

            // ─── Chip dourado (esquerda, topo) ───
            ChipElement(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 36.dp)
            )

            // ─── Contactless (direita do chip) ───
            ContactlessIcon(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 62.dp, top = 44.dp)
            )

            // ─── Logo da bandeira (canto inferior direito) ───
            BrandLogo(
                brand = card.brand,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 4.dp)
            )

            // ─── Número do cartão ───
            Text(
                text = formatCardNumber(card.lastDigits),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.5.sp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(top = 12.dp)
            )

            // ─── Validade ───
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 0.dp)
            ) {
                Text(
                    text = "VALID THRU",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 7.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Normal
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = formatExpiry(card.expiry),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(8.dp))
                // ─── Nome do titular ───
                Text(
                    text = card.name.uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

/**
 * Chip dourado do cartão — retângulo arredondado com linhas internas.
 */
@Composable
private fun ChipElement(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier.size(width = 40.dp, height = 30.dp)
    ) {
        val w = size.width
        val h = size.height
        val cornerRadius = CornerRadius(6f, 6f)

        // Corpo do chip
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(ChipGold, ChipGoldDark, ChipGold)
            ),
            cornerRadius = cornerRadius,
            size = Size(w, h)
        )

        // Linhas internas do chip
        val lineColor = ChipGoldDark.copy(alpha = 0.6f)
        val strokeWidth = 1.5f

        // Linha horizontal central
        drawLine(lineColor, Offset(0f, h / 2), Offset(w, h / 2), strokeWidth)
        // Linhas horizontais superior e inferior
        drawLine(lineColor, Offset(w * 0.2f, h * 0.3f), Offset(w * 0.8f, h * 0.3f), strokeWidth)
        drawLine(lineColor, Offset(w * 0.2f, h * 0.7f), Offset(w * 0.8f, h * 0.7f), strokeWidth)
        // Linhas verticais
        drawLine(lineColor, Offset(w * 0.35f, 0f), Offset(w * 0.35f, h), strokeWidth)
        drawLine(lineColor, Offset(w * 0.65f, 0f), Offset(w * 0.65f, h), strokeWidth)
    }
}

/**
 * Ícone do contactless — arcos concêntricos.
 */
@Composable
private fun ContactlessIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(18.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val color = ContactlessWhite

        // Arcos concêntricos (de menor para maior)
        for (i in 1..3) {
            val radius = (size.width * 0.15f) * i
            drawArc(
                color = color,
                startAngle = -45f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 1.5f)
            )
        }
    }
}

/**
 * Logo da bandeira do cartão.
 */
@Composable
private fun BrandLogo(brand: String, modifier: Modifier = Modifier) {
    when (brand.lowercase()) {
        "mastercard" -> MastercardLogo(modifier)
        "visa" -> VisaLogo(modifier)
        "elo" -> EloLogo(modifier)
        else -> MastercardLogo(modifier) // fallback
    }
}

@Composable
private fun MastercardLogo(modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(width = 44.dp, height = 28.dp)) {
        // Círculo vermelho (esquerda)
        Box(
            modifier = Modifier
                .size(28.dp)
                .align(Alignment.CenterStart)
                .background(MastercardRed, CircleShape)
        )
        // Círculo laranja (direita, sobreposto)
        Box(
            modifier = Modifier
                .size(28.dp)
                .align(Alignment.CenterEnd)
                .background(MastercardOrange, CircleShape)
        )
        // Sobreposição (cor de mistura)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val circleRadius = size.height / 2
            val leftCenter = Offset(circleRadius, size.height / 2)
            val rightCenter = Offset(size.width - circleRadius, size.height / 2)

            // Desenha a interseção
            val path = Path().apply {
                // Apenas a área de sobreposição como um pequeno retângulo arredondado no meio
                val overlapLeft = rightCenter.x - circleRadius
                val overlapRight = leftCenter.x + circleRadius
                val midX = (overlapLeft + overlapRight) / 2
                val halfW = (overlapRight - overlapLeft) / 2

                moveTo(midX, size.height / 2 - circleRadius * 0.86f)
                cubicTo(
                    midX + halfW, size.height / 2 - circleRadius * 0.5f,
                    midX + halfW, size.height / 2 + circleRadius * 0.5f,
                    midX, size.height / 2 + circleRadius * 0.86f
                )
                cubicTo(
                    midX - halfW, size.height / 2 + circleRadius * 0.5f,
                    midX - halfW, size.height / 2 - circleRadius * 0.5f,
                    midX, size.height / 2 - circleRadius * 0.86f
                )
                close()
            }
            drawPath(path, MastercardOverlap)
        }
    }
}

@Composable
private fun VisaLogo(modifier: Modifier = Modifier) {
    Text(
        text = "VISA",
        modifier = modifier,
        fontSize = 18.sp,
        fontWeight = FontWeight.Black,
        color = Color.White,
        letterSpacing = 1.sp
    )
}

@Composable
private fun EloLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFFF9D70B), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "elo",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

// ── Funções utilitárias ──

private fun formatCardNumber(lastDigits: String): String {
    return "•••• •••• •••• $lastDigits"
}

private fun formatExpiry(expiry: String): String {
    // Formata "12/28" para "1 2 / 2 8" com espaçamento
    return expiry.map { it }.joinToString(" ")
}
