package com.example.wallet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wallet.model.PurchaseCategory
import com.example.wallet.model.PurchaseModel
import com.example.wallet.utils.Formatters

/**
 * Retorna o ícone Material da categoria.
 */
private fun categoryIcon(category: PurchaseCategory): ImageVector = when (category) {
    PurchaseCategory.FOOD -> Icons.Default.Restaurant
    PurchaseCategory.TRANSPORT -> Icons.Default.DirectionsCar
    PurchaseCategory.ENTERTAINMENT -> Icons.Default.Movie
    PurchaseCategory.HEALTH -> Icons.Default.LocalPharmacy
    PurchaseCategory.SHOPPING -> Icons.Default.ShoppingBag
    PurchaseCategory.BILLS -> Icons.Default.Receipt
    PurchaseCategory.SUBSCRIPTION -> Icons.Default.Subscriptions
    PurchaseCategory.OTHER -> Icons.Default.MoreHoriz
}

/**
 * Item de compra — layout horizontal com ícone de categoria + info + valor.
 */
@Composable
fun PurchaseItem(
    purchase: PurchaseModel,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.small
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = categoryIcon(purchase.category),
                        contentDescription = purchase.category.label,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = purchase.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${purchase.category.label} • ${purchase.date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = "-${Formatters.currency(purchase.amount)}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
