package com.example.wallet.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wallet.model.PurchaseModel
import com.example.wallet.utils.Formatters

@Composable
fun PurchaseItem(
    purchase: PurchaseModel,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(purchase.title, style = MaterialTheme.typography.titleSmall)
                Text(purchase.date, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = Formatters.currency(purchase.amount),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

