package com.example.wallet.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wallet.ui.components.BottomNavigationBar
import com.example.wallet.ui.components.CardItem
import com.example.wallet.ui.components.EmptyView
import com.example.wallet.ui.components.ErrorView
import com.example.wallet.ui.components.LoadingView
import com.example.wallet.ui.components.TopBar
import com.example.wallet.viewmodel.MyCardsViewModel

@Composable
fun MyCardsScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onCardClick: (String) -> Unit,
    onCreateCard: () -> Unit,
    viewModel: MyCardsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopBar(title = "Meus Cartões") },
        bottomBar = { BottomNavigationBar(currentRoute = currentRoute, onNavigate = onNavigate) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateCard) {
                Icon(Icons.Default.Add, contentDescription = "Criar cartão")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> LoadingView()
                state.error != null -> ErrorView(
                    message = state.error!!,
                    onRetry = viewModel::load
                )
                state.data != null -> {
                    val cards = state.data!!
                    if (cards.isEmpty()) {
                        EmptyView(message = "Nenhum cartão encontrado.\nCrie um novo cartão!")
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(
                                    text = "${cards.size} cartão(ões)",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            items(cards, key = { it.id }) { card ->
                                CardItem(card = card, onClick = { onCardClick(card.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}
