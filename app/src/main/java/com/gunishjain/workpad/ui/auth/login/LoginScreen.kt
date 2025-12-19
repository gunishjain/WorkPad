package com.gunishjain.workpad.ui.auth.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gunishjain.workpad.ui.auth.AuthNavigationEvent
import com.gunishjain.workpad.ui.auth.AuthViewModel

@Composable
fun LoginScreen(
    onNavigate: (AuthNavigationEvent) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {

    val uiState by viewModel.loginState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.error) {
        uiState.error?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            onNavigate(event)
        }
    }

    LoginScreen(
        uiState = uiState,
        onAction = viewModel::onLoginAction
    )

}

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onAction: (LoginAction) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = uiState.email,
            onValueChange = { onAction(LoginAction.EmailChange(it)) },
            label = { Text("Email") },
            isError = uiState.emailError != null,
            supportingText = uiState.emailError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { onAction(LoginAction.PasswordChange(it)) },
            label = { Text("Password") },
            isError = false,
            supportingText = null,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onAction(LoginAction.Login) },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.isDataValid && !uiState.loading
        ) {
            if (uiState.loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { onAction(LoginAction.NavigateToSignup) }
        ) {
            Text("Don't have an account? Sign Up")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(
            uiState = LoginUiState(),
            onAction = {}
        )
    }
}