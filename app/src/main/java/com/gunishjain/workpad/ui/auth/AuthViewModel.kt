package com.gunishjain.workpad.ui.auth

import android.util.Log
import android.util.Patterns.EMAIL_ADDRESS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gunishjain.workpad.domain.repository.AuthRepository
import com.gunishjain.workpad.ui.auth.login.LoginAction
import com.gunishjain.workpad.ui.auth.login.LoginUiState
import com.gunishjain.workpad.ui.auth.signup.SignupAction
import com.gunishjain.workpad.ui.auth.signup.SignupUiState
import com.gunishjain.workpad.utils.isValidUsername
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    val repository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(SignupUiState())
    val registerState = _registerState.asStateFlow()

    private val _uiEvents = Channel<AuthNavigationEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private fun sendEvent(event: AuthNavigationEvent) {
        viewModelScope.launch {
            _uiEvents.send(event)
        }
    }

    fun clearError() {
        _loginState.update {
            it.copy(
                error = null
            )
        }
    }

    fun onLoginAction(action: LoginAction) {
        when (action) {
            is LoginAction.EmailChange -> {
                _loginState.update {
                    it.copy(email = action.email,emailError = null)
                }
            }
            is LoginAction.PasswordChange -> {
                _loginState.update {
                    it.copy(password = action.password)
                }
            }
            LoginAction.Login -> login(_loginState.value.email, _loginState.value.password)
            LoginAction.NavigateToSignup -> sendEvent(AuthNavigationEvent.NavigateToSignUp)
        }
    }

    fun onRegisterAction(action: SignupAction) {
        when (action) {
            is SignupAction.ConfirmPasswordChange -> {
                _registerState.update {
                    it.copy(confirmPassword = action.confirmPass)
                }
            }
            is SignupAction.EmailChange -> {
                _registerState.update {
                    it.copy(email = action.email, emailError = null)
                }
            }
            is SignupAction.PasswordChange -> {
                _registerState.update {
                    it.copy(password = action.password, passwordError = null)
                }
            }
            is SignupAction.UsernameChange -> {
                _registerState.update {
                    it.copy(username = action.username, usernameError = null)
                }
            }
            SignupAction.Register -> {
                val email = _registerState.value.email
                val password = _registerState.value.password
                val username = _registerState.value.username
                register(email, password, username)
            }

            SignupAction.NavigateToLogin -> sendEvent(AuthNavigationEvent.NavigateToLogin)
        }
    }

    fun login(email: String, password: String) {

        if(!validate()) return

        viewModelScope.launch {
            _loginState.update {
                it.copy(loading = true)
            }
            
            val result = repository.signIn(email, password)
            
            result.onSuccess { user ->
                Log.d("Login", "Login successful with id: ${user.id}")
                _loginState.update {
                    it.copy(success = true)
                }
                sendEvent(AuthNavigationEvent.NavigateToHome)
            }.onFailure { e ->
                Log.e("Login", "Login failed", e)
                _loginState.update {
                    it.copy(error = e.message ?: "Something went wrong")
                }
            }
            
            _loginState.update {
                it.copy(loading = false)
            }

        }
    }

    fun register(email: String, password: String, name: String) {

        if(!validateRegister()) return

        viewModelScope.launch {
            _registerState.update {
                it.copy(loading = true)
            }
            
            val result = repository.signUp(email, password, name)
            
            result.onSuccess { user ->
                Log.d("Register", "Register successful with id: ${user.id}")
                _registerState.update {
                    it.copy(signupSuccess = true)
                }
                sendEvent(AuthNavigationEvent.NavigateToHome)
            }.onFailure { e ->
                Log.e("Register", "Register failed", e)
                _registerState.update {
                    it.copy(signupError = e.message ?: "Something went wrong")
                }
            }
            
            _registerState.update {
                it.copy(loading = false)
            }
        }
    }

    fun validate(): Boolean {
        val email = _loginState.value.email

        val isEmailValid = EMAIL_ADDRESS.matcher(email).matches()

        _loginState.update {
            it.copy(
                emailError = if (isEmailValid) null else "Invalid email",
            )
        }
        return isEmailValid
    }

    fun validateRegister(): Boolean {
        val email = _registerState.value.email
        val password = _registerState.value.password
        val name = _registerState.value.username

        val isEmailValid = EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.length >= 6
        val isNameValid = isValidUsername(name)

        _registerState.update {
            it.copy(
                emailError = if (isEmailValid) null else "Invalid email",
                passwordError = if (isPasswordValid) null else "Password must be at least 6 characters",
                usernameError = if (isNameValid) null else "Name Validation Error"
            )
        }
        return isEmailValid && isPasswordValid && isNameValid
    }



}