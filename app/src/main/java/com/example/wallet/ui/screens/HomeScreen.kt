package com.example.wallet.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font. FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wallet.model.PurchaseModel
import com.example.wallet.ui.components.CardItem
import com.example.wallet.ui.components.ErrorView
import com.example.wallet.ui.components.LoadingView
import com.example.wallet.ui.components.PrimaryButton
import com.example.wallet.ui.components.SecondaryButton
import com.example.wallet.ui.components.SelectBankModal
import com.example.wallet.ui.components.TopBar
import com.example.wallet.viewmodel.MyCardsViewModel
import kotlinx.coroutines.launch

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
    val cards = state.data ?: emptyList()
    val pagerState = rememberPagerState(pageCount = { cards.size })

    var showBankModal by remember { mutableStateOf(false) }

    val mockPurchases = remember {
        listOf(
            PurchaseModel("1", "Alimentação", 45.90, "Hoje, 12:30"),
            PurchaseModel("2", "Cinema", 32.00, "Ontem, 18:00"),
            PurchaseModel("3", "Farmácia", 15.50, "10 Out, 10:15"),
            PurchaseModel("4", "Supermercado", 120.00, "08 Out, 14:20")
        )
    }

    if (showBankModal) {
        SelectBankModal(
            onDismiss = { showBankModal = false },
            onCardCreated = {
                showBankModal = false
                viewModel.load()
            }
        )
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Meus Cartões",
                actions = {
                    if (cards.isNotEmpty()) {
                        Surface(
                            onClick = { /* favoritar */ },
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
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> LoadingView()
                state.error != null -> ErrorView(message = state.error!!, onRetry = viewModel::load)
                cards.isEmpty() -> {
                    // ESTADO VAZIO (Conforme a imagem)
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Spacer(Modifier.height(20.dp))
                        Surface(
                            modifier = Modifier.size(80.dp),
                            color = Color(0xFF10141D),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, Color(0xFF1E2633))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.CreditCard,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp),
                                    tint = Color(0xFF637388)
                                )
                            }
                        }

                        Spacer(Modifier.height(32.dp))
                        Text(
                            text = "Adicione seu primeiro cartão",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Cadastre seus cartões para ter acesso rápido e seguro às informações",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF8E99A8),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(Modifier.height(48.dp))

                        // Botão Central com Borda Pontilhada
                        Box(
                            modifier = Modifier.fillMaxWidth().height(180.dp).clickable { onCreateCard() },
                            contentAlignment = Alignment.Center
                        ) {
                            val stroke = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawRoundRect(
                                    color = Color(0xFF1E2633),
                                    style = stroke,
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx())
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(modifier = Modifier.size(56.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primary) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                    }
                                }
                                Spacer(Modifier.height(16.dp))
                                Text(text = "Adicionar cartão", color = Color(0xFF8E99A8))
                            }
                        }
                    }
                }
                else -> {
                    // LISTAGEM DE CARTÕES E COMPRAS
                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${cards.size} cartão(ões) cadastrados",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.align(Alignment.Start).padding(bottom = 16.dp)
                        )

                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                                enabled = pagerState.currentPage > 0
                            ) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null)
                            }

                            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f), pageSpacing = 16.dp) { page ->
                                CardItem(card = cards[page], onClick = { onCardClick(cards[page].id) })
                            }

                            IconButton(
                                onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                                enabled = pagerState.currentPage < cards.size - 1
                            ) {
                                SecondaryButton(
                                    text = "Gerenciar",
                                    onClick = { /* gerenciar */ },
                                    modifier = Modifier.weight(1f)
                                )
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PrimaryButton(
                                        text = "+ Novo Cartão",
                                        onClick = { showBankModal = true }
                                    )
                                }
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                            }
                        }

                        Text(text = "${pagerState.currentPage + 1} de ${cards.size}", style = MaterialTheme.typography.bodySmall)

                        Spacer(Modifier.height(24.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(onClick = { /* gerenciar */ }, modifier = Modifier.weight(1f)) {
                                Text("GERENCIAR", fontSize = 12.sp)
                            }
                            Button(onClick = onCreateCard, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("NOVO CARTÃO", fontSize = 12.sp)
                            }
                        }

                        Spacer(Modifier.height(32.dp))
                        Text(
                            text = "ÚLTIMAS COMPRAS",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start).padding(bottom = 16.dp)
                        )

                        mockPurchases.forEach { purchase ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text(text = purchase.title, fontWeight = FontWeight.Medium)
                                    Text(text = purchase.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                }
                                Text(text = "-R$ ${"%.2f".format(purchase.amount)}", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                            }
                            HorizontalDivider(thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}
}
