package com.gunishjain.workpad.ui.auth.signup

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
fun SignupScreen(
    onNavigate: (AuthNavigationEvent) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {

    val uiState by viewModel.registerState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.signupError) {
        uiState.signupError?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            onNavigate(event)
        }
    }

    SignupScreen(
        uiState = uiState,
        onAction = viewModel::onRegisterAction
    )

}

@Composable
fun SignupScreen(
    uiState: SignupUiState,
    onAction: (SignupAction) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sign Up",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = uiState.username,
            onValueChange = { onAction(SignupAction.UsernameChange(it)) },
            label = { Text("Username") },
            isError = uiState.usernameError != null,
            supportingText = uiState.usernameError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = { onAction(SignupAction.EmailChange(it)) },
            label = { Text("Email") },
            isError = uiState.emailError != null,
            supportingText = uiState.emailError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { onAction(SignupAction.PasswordChange(it)) },
            label = { Text("Password") },
            isError = uiState.passwordError != null,
            supportingText = uiState.passwordError?.let { { Text(it) } },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.confirmPassword,
            onValueChange = { onAction(SignupAction.ConfirmPasswordChange(it)) },
            label = { Text("Confirm Password") },
            isError = uiState.confirmPasswordError != null,
            supportingText = uiState.confirmPasswordError?.let { { Text(it) } },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onAction(SignupAction.Register) },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.isDataValid && !uiState.loading
        ) {
            if (uiState.loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Sign Up")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { onAction(SignupAction.NavigateToLogin) }
        ) {
            Text("Already have an account? Login")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun SignupScreenPreview() {
    MaterialTheme {
        SignupScreen(
            uiState = SignupUiState(),
            onAction = {}
        )
    }
}
