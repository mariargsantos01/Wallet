package com.example.wallet.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wallet.ui.components.AppTextField
import com.example.wallet.ui.components.PrimaryButton
import com.example.wallet.ui.components.TopBar
import com.example.wallet.viewmodel.CreateCardViewModel

@Composable
fun CreateCardScreen(
    onCardCreated: () -> Unit,
    showBackButton: Boolean = false,
    onBack: () -> Unit = {},
    viewModel: CreateCardViewModel = viewModel()
) {
    val form by viewModel.form.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopBar(
                title = "Criar Cartão",
                onBack = if (showBackButton) onBack else null
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Crie seu primeiro cartão Wallet",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )

                AppTextField(
                    value = form.name,
                    onValueChange = viewModel::onNameChange,
                    label = "Nome impresso no cartão",
                    placeholder = "Ex.: João da Silva"
                )

                AppTextField(
                    value = form.cardType,
                    onValueChange = viewModel::onCardTypeChange,
                    label = "Tipo",
                    placeholder = "Black, Gold, Basic"
                )

                AppTextField(
                    value = form.limit,
                    onValueChange = viewModel::onLimitChange,
                    label = "Limite desejado (R$)",
                    placeholder = "0,00",
                    keyboardType = KeyboardType.Number
                )

                Spacer(Modifier.height(4.dp))

                PrimaryButton(
                    text = if (state.isLoading) "Criando..." else "Criar cartão",
                    onClick = { viewModel.submit(onCardCreated) },
                    loading = state.isLoading
                )

                state.error?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

