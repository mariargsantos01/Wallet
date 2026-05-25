package com.example.wallet.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.wallet.model.PurchaseCategory
import com.example.wallet.model.PurchaseModel
import com.example.wallet.utils.Formatters

/**
 * Cores fixas para cada categoria do gráfico.
 */
private val categoryColors = mapOf(
    PurchaseCategory.FOOD to Color(0xFFFF6B6B),
    PurchaseCategory.TRANSPORT to Color(0xFF4ECDC4),
    PurchaseCategory.ENTERTAINMENT to Color(0xFFFFE66D),
    PurchaseCategory.HEALTH to Color(0xFF95E1D3),
    PurchaseCategory.SHOPPING to Color(0xFFA8E6CF),
    PurchaseCategory.BILLS to Color(0xFFFF8A5C),
    PurchaseCategory.SUBSCRIPTION to Color(0xFF6C5CE7),
    PurchaseCategory.OTHER to Color(0xFFB8B8B8)
)

/**
 * Dados agrupados por categoria para gráficos.
 */
data class CategorySpending(
    val category: PurchaseCategory,
    val total: Double,
    val percentage: Float,
    val color: Color
)

/**
 * Gráfico de rosca (donut) com gastos por categoria.
 */
@Composable
fun SpendingDonutChart(
    purchases: List<PurchaseModel>,
    modifier: Modifier = Modifier
) {
    val totalSpent = purchases.sumOf { it.amount }
    if (totalSpent == 0.0 || purchases.isEmpty()) return

    val categoryData = remember(purchases) {
        purchases
            .groupBy { it.category }
            .map { (cat, items) ->
                val catTotal = items.sumOf { it.amount }
                CategorySpending(
                    category = cat,
                    total = catTotal,
                    percentage = (catTotal / totalSpent).toFloat(),
                    color = categoryColors[cat] ?: Color.Gray
                )
            }
            .sortedByDescending { it.total }
    }

    var animationPlayed by remember { mutableStateOf(false) }
    val animationProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "donut"
    )
    LaunchedEffect(Unit) { animationPlayed = true }

    SectionCard(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Gastos por Categoria",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gráfico de rosca
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(120.dp)) {
                        val strokeWidth = 24.dp.toPx()
                        val radius = (size.minDimension - strokeWidth) / 2
                        val topLeft = Offset(
                            (size.width - radius * 2) / 2,
                            (size.height - radius * 2) / 2
                        )
                        val arcSize = Size(radius * 2, radius * 2)
                        var startAngle = -90f

                        categoryData.forEach { data ->
                            val sweep = data.percentage * 360f * animationProgress
                            drawArc(
                                color = data.color,
                                startAngle = startAngle,
                                sweepAngle = sweep,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            startAngle += sweep
                        }
                    }
                    // Total no centro
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = Formatters.currency(totalSpent),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Legenda
                Column(
                    modifier = Modifier.padding(start = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categoryData.take(5).forEach { data ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(data.color)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = data.category.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.width(80.dp)
                            )
                            Text(
                                text = "${(data.percentage * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Gráfico de barras horizontais mostrando gastos diários.
 */
@Composable
fun DailySpendingChart(
    purchases: List<PurchaseModel>,
    modifier: Modifier = Modifier
) {
    if (purchases.isEmpty()) return

    // Agrupa por data e pega os últimos 7 dias
    val dailyData = remember(purchases) {
        purchases
            .groupBy { it.date }
            .map { (date, items) -> date to items.sumOf { it.amount } }
            .sortedByDescending { it.second }
            .take(7)
            .sortedBy { it.first }
    }

    if (dailyData.isEmpty()) return

    val maxAmount = dailyData.maxOf { it.second }

    var animationPlayed by remember { mutableStateOf(false) }
    val animationProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "bars"
    )
    LaunchedEffect(Unit) { animationPlayed = true }

    SectionCard(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Gastos por Dia",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(16.dp))

            dailyData.forEach { (date, amount) ->
                val fraction = if (maxAmount > 0) (amount / maxAmount).toFloat() else 0f
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Data (só dia/mês)
                    Text(
                        text = date.take(5),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(44.dp)
                    )
                    // Barra
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction * animationProgress)
                                .height(20.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    // Valor
                    Text(
                        text = Formatters.currency(amount),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.width(72.dp)
                    )
                }
            }
        }
    }
}

/**
 * Gráfico de barras horizontais mostrando gastos mensais por categoria.
 */
@Composable
fun MonthlyCategoryChart(
    purchases: List<PurchaseModel>,
    modifier: Modifier = Modifier
) {
    if (purchases.isEmpty()) return

    val categoryData = remember(purchases) {
        purchases
            .groupBy { it.category }
            .map { (cat, items) -> cat to items.sumOf { it.amount } }
            .sortedByDescending { it.second }
    }

    if (categoryData.isEmpty()) return

    val maxAmount = categoryData.maxOf { it.second }

    var animationPlayed by remember { mutableStateOf(false) }
    val animationProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "catBars"
    )
    LaunchedEffect(Unit) { animationPlayed = true }

    SectionCard(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Ranking por Categoria",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(16.dp))

            categoryData.forEach { (category, amount) ->
                val fraction = if (maxAmount > 0) (amount / maxAmount).toFloat() else 0f
                val color = categoryColors[category] ?: Color.Gray

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = category.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.width(80.dp),
                        maxLines = 1
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction * animationProgress)
                                .height(16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(color)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = Formatters.currency(amount),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.width(72.dp)
                    )
                }
            }
        }
    }
}

