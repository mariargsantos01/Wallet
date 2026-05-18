package com.example.wallet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wallet.ui.theme.*
import com.example.wallet.viewmodel.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit,
    onNavigateToReset: () -> Unit,
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    val email by viewModel.email.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoPrincipal)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Recuperar Senha",
            style = MaterialTheme.typography.headlineMedium,
            color = Branco,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Enviaremos um link para o seu e-mail",
            style = MaterialTheme.typography.bodyMedium,
            color = CinzaTexto
        )

        Spacer(modifier = Modifier.height(48.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Email",
                color = Branco,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            TextField(
                value = email,
                onValueChange = viewModel::onEmailChange,
                placeholder = { Text("seu@email.com", color = CinzaTexto) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = CinzaTexto)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = CinzaEscuro,
                    unfocusedContainerColor = CinzaEscuro,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Branco,
                    unfocusedTextColor = Branco
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.requestPasswordReset(onNavigateToReset) },
            enabled = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario)
        ) {
            Text(
                text = if (state.isLoading) "Enviando..." else "Enviar Link",
                color = Branco,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        state.error?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Voltar para o Login",
            color = AzulPrimario,
            modifier = Modifier.clickable { onBackToLogin() },
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
