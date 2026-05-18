package com.example.wallet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
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
import com.example.wallet.ui.components.PasswordTextField
import com.example.wallet.ui.components.PrimaryButton
import com.example.wallet.viewmodel.SignUpViewModel

@Composable
fun SignUpScreen(
    onBackToLogin: () -> Unit,
    onSignUpSuccess: () -> Unit,
    viewModel: SignUpViewModel = viewModel()
) {
    val fullName by viewModel.fullName.collectAsStateWithLifecycle()
    val username by viewModel.username.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(60.dp))

        Text(
            text = "Criar Conta",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Preencha os dados abaixo",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(32.dp))

        AppTextField(
            value = fullName,
            onValueChange = viewModel::onFullNameChange,
            label = "Nome Completo",
            placeholder = "Seu nome",
            leadingIcon = Icons.Default.Person
        )
        Spacer(Modifier.height(16.dp))

        AppTextField(
            value = username,
            onValueChange = viewModel::onUsernameChange,
            label = "Usuário",
            placeholder = "seu.usuario",
            leadingIcon = Icons.Default.AccountCircle
        )
        Spacer(Modifier.height(16.dp))

        AppTextField(
            value = email,
            onValueChange = viewModel::onEmailChange,
            label = "Email",
            placeholder = "seu@email.com",
            leadingIcon = Icons.Default.Email,
            keyboardType = KeyboardType.Email
        )
        Spacer(Modifier.height(16.dp))

        PasswordTextField(
            value = password,
            onValueChange = viewModel::onPasswordChange,
            label = "Senha",
            placeholder = "********",
            leadingIcon = Icons.Default.Lock
        )

        Spacer(Modifier.height(28.dp))

        PrimaryButton(
            text = if (state.isLoading) "Cadastrando..." else "Cadastrar",
            onClick = { viewModel.signUp(onSignUpSuccess) },
            loading = state.isLoading
        )

        state.error?.let {
            Spacer(Modifier.height(12.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Já tem uma conta? Entre aqui",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onBackToLogin() }
        )

        Spacer(Modifier.height(40.dp))
    }
}

