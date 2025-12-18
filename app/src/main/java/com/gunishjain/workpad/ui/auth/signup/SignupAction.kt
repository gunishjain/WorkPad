package com.gunishjain.workpad.ui.auth.signup

sealed class SignupAction {
    data class EmailChange(val email: String): SignupAction()
    data class PasswordChange(val password: String): SignupAction()
    data class ConfirmPasswordChange(val confirmPass: String): SignupAction()
    data class UsernameChange(val username: String): SignupAction()
    object Register: SignupAction()

    object NavigateToLogin: SignupAction()

}