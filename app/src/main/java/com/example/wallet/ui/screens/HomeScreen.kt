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
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface

@Composable
fun MyCardsScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onCardClick: (String) -> Unit,
    onCreateCard: () -> Unit,
    viewModel: MyCardsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { state.data?.size ?: 0 })

    Scaffold(
        topBar = {
            TopBar(
                title = "Meus Cartões", actions = {
                    Surface(
                        onClick = { /* Sua ação de favoritar */ },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(end = 16.dp).size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Favoritar",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                })
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute, onNavigate = onNavigate
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> LoadingView()
                state.error != null -> ErrorView(
                    message = state.error!!, onRetry = viewModel::load
                )

                state.data != null -> {
                    val cards = state.data!!
                    if (cards.isEmpty()) {
                        EmptyView(message = "Nenhum cartão encontrado.\nCrie um novo cartão!")
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {

                            Text(
                                text = "${cards.size} cartão(ões)",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(bottom = 16.dp)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                        }
                                    }, enabled = pagerState.currentPage > 0
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardArrowLeft,
                                        contentDescription = "Anterior"
                                    )
                                }

                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier.weight(1f),
                                    pageSpacing = 16.dp
                                ) { page ->
                                    CardItem(
                                        card = cards[page],
                                        onClick = { onCardClick(cards[page].id) })
                                }

                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                        }
                                    }, enabled = pagerState.currentPage < cards.size - 1
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardArrowRight,
                                        contentDescription = "Próximo"
                                    )
                                }
                            }

                            Text(
                                text = "${pagerState.currentPage + 1} de ${cards.size}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 16.dp)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { /* Ação de gerenciar */ },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "GERENCIAR", fontSize = 12.sp, maxLines = 1
                                    )
                                }


                                Button(
                                    onClick = onCreateCard, modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "NOVO CARTÃO", fontSize = 12.sp, maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
