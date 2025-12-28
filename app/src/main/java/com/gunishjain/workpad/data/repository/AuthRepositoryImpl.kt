package com.gunishjain.workpad.data.repository

import com.gunishjain.workpad.domain.model.User
import com.gunishjain.workpad.domain.repository.AuthError
import com.gunishjain.workpad.domain.repository.AuthRepository
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.providers.builtin.Email
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: Auth
) : AuthRepository {

    private fun mapToAuthError(e: Exception): AuthError {
        return when (e) {
            is HttpRequestTimeoutException, is ConnectException, is UnknownHostException -> AuthError.NetworkError
            is ResponseException -> {
                val message = e.message?.lowercase() ?: ""
                when {
                    message.contains("invalid login credentials") || message.contains("invalid_credentials") -> AuthError.InvalidCredentials
                    message.contains("user already registered") || message.contains("already_exists") -> AuthError.UserAlreadyExists
                    message.contains("weak_password") -> AuthError.WeakPassword
                    else -> AuthError.Unknown(e.message)
                }
            }
            else -> {
                val message = e.message?.lowercase() ?: ""
                when {
                    message.contains("invalid login credentials") || message.contains("invalid_credentials") -> AuthError.InvalidCredentials
                    message.contains("user already registered") || message.contains("already_exists") -> AuthError.UserAlreadyExists
                    else -> AuthError.Unknown(e.message)
                }
            }
        }
    }

    override suspend fun signUp(
        email: String,
        password: String,
        name: String?
    ): Result<User> {
        return try {
            val supabaseUser = auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("username", name)
                }
            } ?: return Result.failure(AuthError.Unknown("User creation failed: No data returned"))

            Result.success(
                User(
                    id = supabaseUser.id,
                    email = supabaseUser.email ?: "",
                    name = name,
                    createdAt = supabaseUser.createdAt,
                    updatedAt = supabaseUser.updatedAt
                )
            )
        } catch (e: Exception) {
            Result.failure(mapToAuthError(e))
        }
    }

    override suspend fun signIn(
        email: String,
        password: String
    ): Result<User> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            
            val supabaseUser = auth.currentUserOrNull()
                ?: return Result.failure(AuthError.SessionExpired)
            
            Result.success(
                User(
                    id = supabaseUser.id,
                    email = supabaseUser.email ?: "",
                    name = supabaseUser.userMetadata?.get("username")?.toString(),
                    createdAt = supabaseUser.createdAt,
                    updatedAt = supabaseUser.updatedAt
                )
            )
        } catch (e: Exception) {
            Result.failure(mapToAuthError(e))
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapToAuthError(e))
        }
    }

    override suspend fun getCurrentUser(): Result<User?> {
        return try {
            val supabaseUser = auth.currentUserOrNull()
            if (supabaseUser == null) {
                Result.success(null)
            } else {
                Result.success(
                    User(
                        id = supabaseUser.id,
                        email = supabaseUser.email ?: "",
                        name = supabaseUser.userMetadata?.get("username")?.toString(),
                        createdAt = supabaseUser.createdAt,
                        updatedAt = supabaseUser.updatedAt
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(mapToAuthError(e))
        }
    }

    override fun observeAuthState(): Flow<User?> {
        return auth.sessionStatus.map { sessionStatus ->
            when (sessionStatus) {
                is SessionStatus.Authenticated -> {
                    val supabaseUser = sessionStatus.session.user ?: return@map null
                    User(
                        id = supabaseUser.id,
                        email = supabaseUser.email ?: "",
                        name = supabaseUser.userMetadata?.get("username")?.toString(),
                        createdAt = supabaseUser.createdAt,
                        updatedAt = supabaseUser.updatedAt
                    )
                }
                else -> null
            }
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        return try {
            auth.currentSessionOrNull() != null
        } catch (e: Exception) {
            false
        }
    }
}