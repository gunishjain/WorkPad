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
                    it.copy(email = action.email)
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
                    it.copy(email = action.email)
                }
            }
            is SignupAction.PasswordChange -> {
                _registerState.update {
                    it.copy(password = action.password)
                }
            }
            is SignupAction.UsernameChange -> {
                _registerState.update {
                    it.copy(username = action.username)
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
            try {
                val user = repository.signIn(email,password)
                Log.d("Login", "Login successful with id: ${user.isSuccess}")
                _loginState.update {
                    it.copy(success = true)
                }

            } catch (e: Exception) {
                _loginState.update {
                    it.copy(error = e.message ?: "Something went wrong")
                }
            }
            _loginState.update {
                it.copy(loading = false)
            }

        }
    }

    fun register(email: String, password: String,name: String) {

        if(!validateRegister()) return

        viewModelScope.launch {
            _registerState.update {
                it.copy(loading = true)
            }
            try {
                val user = repository.signUp(email, password, name)
                Log.d("Register", "Register successful with id: ${user.isSuccess}")
                _registerState.update {
                    it.copy(signupSuccess = true)
                }
            } catch (e: Exception) {
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
        val password = _loginState.value.password

        val isEmailValid = EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.length >= 6

        _loginState.update {
            it.copy(
                emailError = if (isEmailValid) null else "Invalid email",
                passwordError = if (isPasswordValid) null else "Password must be at least 6 characters"
            )
        }
        return isEmailValid && isPasswordValid
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