package com.gunishjain.workpad.ui.auth

sealed class AuthNavigationEvent {
    data object NavigateToLogin : AuthNavigationEvent()
    data object NavigateToSignUp : AuthNavigationEvent()
    data object NavigateToHome : AuthNavigationEvent()
    data object NavigateBack : AuthNavigationEvent()
}