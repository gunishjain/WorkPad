package com.gunishjain.workpad.domain.repository

import com.gunishjain.workpad.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun signUp(email: String, password: String, name: String? = null): Result<User>
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signOut(): Result<Unit>

    suspend fun getCurrentUser(): Result<User?>
    fun observeAuthState(): Flow<User?>

    suspend fun isAuthenticated(): Boolean
}