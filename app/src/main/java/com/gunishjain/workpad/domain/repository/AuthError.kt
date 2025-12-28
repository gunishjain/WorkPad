package com.gunishjain.workpad.domain.repository

sealed class AuthError : Exception() {
    object InvalidCredentials : AuthError()
    object UserAlreadyExists : AuthError()
    object NetworkError : AuthError()
    object WeakPassword : AuthError()
    object SessionExpired : AuthError()
    data class Unknown(val originalMessage: String?) : AuthError()
    
    override val message: String?
        get() = when (this) {
            is InvalidCredentials -> "Invalid email or password"
            is UserAlreadyExists -> "An account with this email already exists"
            is NetworkError -> "Network error, please check your connection"
            is WeakPassword -> "Password is too weak"
            is SessionExpired -> "Your session has expired. Please login again."
            is Unknown -> originalMessage ?: "An unexpected error occurred"
        }
}
