package com.gunishjain.workpad.ui.auth.login

sealed class LoginAction {

    data class EmailChange(val email: String): LoginAction()
    data class PasswordChange(val password: String): LoginAction()
    object Login: LoginAction()
    object NavigateToSignup: LoginAction()

}