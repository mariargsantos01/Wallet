package com.example.wallet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wallet.ui.theme.*
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
            .background(FundoPrincipal)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Títulos
        Text(
            text = "Criar Conta",
            style = MaterialTheme.typography.headlineMedium,
            color = Branco,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Preencha os dados abaixo",
            style = MaterialTheme.typography.bodyMedium,
            color = CinzaTexto
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Nome Completo
        SignUpTextField(
            label = "Nome Completo", 
            value = fullName, 
            onValueChange = viewModel::onFullNameChange, 
            placeholder = "Seu nome",
            leadingIcon = Icons.Default.Person
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Username
        SignUpTextField(
            label = "Usuário", 
            value = username, 
            onValueChange = viewModel::onUsernameChange, 
            placeholder = "seu.usuario",
            leadingIcon = Icons.Default.AccountCircle
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Email
        SignUpTextField(
            label = "Email", 
            value = email, 
            onValueChange = viewModel::onEmailChange, 
            placeholder = "seu@email.com",
            leadingIcon = Icons.Default.Email
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Senha
        SignUpTextField(
            label = "Senha",
            value = password,
            onValueChange = viewModel::onPasswordChange,
            placeholder = "********",
            isPassword = true,
            leadingIcon = Icons.Default.Lock
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botão Cadastrar
        Button(
            onClick = { viewModel.signUp(onSignUpSuccess) },
            enabled = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulPrimario,
                disabledContainerColor = AzulPrimario.copy(alpha = 0.5f)
            )
        ) {
            Text(
                text = if (state.isLoading) "Cadastrando..." else "Cadastrar",
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
            text = "Já tem uma conta? Entre aqui",
            color = AzulPrimario,
            modifier = Modifier.clickable { onBackToLogin() },
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun SignUpTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = Branco, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = CinzaTexto) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            leadingIcon = {
                Icon(leadingIcon, contentDescription = null, tint = CinzaTexto)
            },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CinzaEscuro,
                unfocusedContainerColor = CinzaEscuro,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Branco,
                unfocusedTextColor = Branco,
                cursorColor = AzulPrimario
            )
        )
    }
}
