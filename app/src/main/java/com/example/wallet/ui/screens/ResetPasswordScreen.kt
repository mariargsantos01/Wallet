package com.example.wallet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wallet.ui.theme.*
import com.example.wallet.viewmodel.ForgotPasswordViewModel

@Composable
fun ResetPasswordScreen(
    onResetSuccess: () -> Unit,
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    val token by viewModel.token.collectAsStateWithLifecycle()
    val newPassword by viewModel.newPassword.collectAsStateWithLifecycle()
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
            text = "Nova Senha",
            style = MaterialTheme.typography.headlineMedium,
            color = Branco,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Digite o código recebido e sua nova senha",
            style = MaterialTheme.typography.bodyMedium,
            color = CinzaTexto
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Campo Token
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Código (Token)", color = Branco, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
            TextField(
                value = token,
                onValueChange = viewModel::onTokenChange,
                placeholder = { Text("Cole o código aqui", color = CinzaTexto) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null, tint = CinzaTexto) },
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

        Spacer(modifier = Modifier.height(20.dp))

        // Campo Nova Senha
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Nova Senha", color = Branco, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
            TextField(
                value = newPassword,
                onValueChange = viewModel::onNewPasswordChange,
                placeholder = { Text("Mínimo 8 caracteres", color = CinzaTexto) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(8.dp),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CinzaTexto) },
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
            onClick = { viewModel.confirmPasswordReset(onResetSuccess) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario)
        ) {
            Text(if (state.isLoading) "Redefinindo..." else "Redefinir Senha", color = Branco, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        state.error?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
        }
    }
}
