package com.gunishjain.workpad.ui.auth.login

data class LoginUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val error: String? = null,
    val loading: Boolean = false,
    val success: Boolean = false
) {
    val isDataValid : Boolean
        get() = email.isNotBlank() && password.isNotBlank()
}
