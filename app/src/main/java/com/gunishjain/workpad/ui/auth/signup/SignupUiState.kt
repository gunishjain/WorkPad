package com.gunishjain.workpad.ui.auth.signup

data class SignupUiState(
    val email: String = "",
    val password: String = "",
    val username: String = "",
    val usernameError: String? = null,
    val confirmPassword: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val loading: Boolean = false,
    val signupSuccess: Boolean = false,
    val signupError: String? = null
) {
    val isDataValid: Boolean
        get() = emailError == null && passwordError == null && confirmPasswordError == null
}
