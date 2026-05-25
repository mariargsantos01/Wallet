package com.example.wallet.ui.components.cardmanagement

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wallet.ui.components.BankLogo
import com.example.wallet.ui.theme.WalletTheme

// Cores do cartão (compartilhadas com CardItem)
private val ChipGold = Color(0xFFD4A847)
private val ChipGoldDark = Color(0xFFB8860B)
private val MastercardRed = Color(0xFFD50000)
private val MastercardOrange = Color(0xFFFF9800)
private val MastercardOverlap = Color(0xFFFF3D00)

@Composable
fun CardPreview(
    brand: String,
    maskedNumber: String,
    holderName: String,
    expiry: String,
    modifier: Modifier = Modifier,
    bankColor: Long = 0xFF171717,
    bankName: String = ""
) {
    val shape = RoundedCornerShape(16.dp)

    val baseColor = Color(bankColor.toULong())
    val darkVariant = baseColor.copy(alpha = 0.7f)

    val cardGradient = Brush.linearGradient(
        colors = listOf(baseColor, darkVariant, darkVariant, baseColor.copy(alpha = 0.9f)),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.586f)
            .shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.25f),
                spotColor = Color.Black.copy(alpha = 0.35f)
            )
            .clip(shape)
            .background(cardGradient)
    ) {
        // Brilho sutil
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.03f), Color.Transparent),
                        center = Offset(0f, 0f),
                        radius = 400f
                    )
                )
        )

        Box(Modifier.fillMaxSize().padding(20.dp)) {

            // Bandeira texto (topo direita)
            Text(
                text = brand.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.9f),
                letterSpacing = 2.sp,
                modifier = Modifier.align(Alignment.TopEnd)
            )

            // Logo do banco (topo esquerdo)
            if (bankName.isNotBlank()) {
                BankLogo(
                    bankName = bankName,
                    size = 28.dp,
                    modifier = Modifier.align(Alignment.TopStart)
                )
            }

            // Chip
            Canvas(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 36.dp)
                    .size(width = 40.dp, height = 30.dp)
            ) {
                val w = size.width
                val h = size.height
                drawRoundRect(
                    brush = Brush.verticalGradient(listOf(ChipGold, ChipGoldDark, ChipGold)),
                    cornerRadius = CornerRadius(6f, 6f),
                    size = Size(w, h)
                )
                val lc = ChipGoldDark.copy(alpha = 0.6f)
                drawLine(lc, Offset(0f, h / 2), Offset(w, h / 2), 1.5f)
                drawLine(lc, Offset(w * 0.35f, 0f), Offset(w * 0.35f, h), 1.5f)
                drawLine(lc, Offset(w * 0.65f, 0f), Offset(w * 0.65f, h), 1.5f)
            }

            // Contactless
            Canvas(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 62.dp, top = 44.dp)
                    .size(18.dp)
            ) {
                val cx = size.width / 2
                val cy = size.height / 2
                for (i in 1..3) {
                    val r = (size.width * 0.15f) * i
                    drawArc(
                        color = Color(0xFFCCCCCC),
                        startAngle = -45f,
                        sweepAngle = 90f,
                        useCenter = false,
                        topLeft = Offset(cx - r, cy - r),
                        size = Size(r * 2, r * 2),
                        style = Stroke(width = 1.5f)
                    )
                }
            }

            // Logo bandeira (inferior direita)
            when (brand.lowercase()) {
                "mastercard" -> {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 4.dp)
                            .size(width = 44.dp, height = 28.dp)
                    ) {
                        Box(Modifier.size(28.dp).align(Alignment.CenterStart).background(MastercardRed, CircleShape))
                        Box(Modifier.size(28.dp).align(Alignment.CenterEnd).background(MastercardOrange, CircleShape))
                    }
                }
                "visa" -> {
                    Text(
                        text = "VISA",
                        modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 4.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 4.dp)
                            .size(width = 44.dp, height = 28.dp)
                    ) {
                        Box(Modifier.size(28.dp).align(Alignment.CenterStart).background(MastercardRed, CircleShape))
                        Box(Modifier.size(28.dp).align(Alignment.CenterEnd).background(MastercardOrange, CircleShape))
                    }
                }
            }

            // Número
            Text(
                text = maskedNumber,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.5.sp,
                modifier = Modifier.align(Alignment.CenterStart).padding(top = 12.dp)
            )

            // Validade + Nome
            Column(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Text(
                    text = "VALID THRU",
                    fontSize = 7.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Normal
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = expiry.map { it }.joinToString(" "),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = holderName.uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A1628)
@Composable
private fun CardPreviewPreview() {
    WalletTheme {
        CardPreview(
            brand = "Mastercard",
            maskedNumber = "•••• •••• •••• 4523",
            holderName = "João da Silva",
            expiry = "12/28"
        )
    }
}
