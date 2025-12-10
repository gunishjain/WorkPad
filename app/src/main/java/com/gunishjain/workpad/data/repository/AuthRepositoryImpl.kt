package com.gunishjain.workpad.data.repository

import com.gunishjain.workpad.domain.model.User
import com.gunishjain.workpad.domain.repository.AuthRepository
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: Auth
) : AuthRepository {

    override suspend fun signUp(
        email: String,
        password: String,
        name: String?
    ): Result<User> {
        return try {

            val result = auth.signUpWith(Email) {
                apply {
                    this.email = email
                    this.password = password
                    data = buildJsonObject {
                        put("username", name)
                    }
                }
            }
            val supabaseUser = result
                ?: return Result.failure(IllegalStateException("User object is null"))

            val mappedUser = User(
                id = supabaseUser.id,
                email = supabaseUser.email ?: "",
                name = name,
                createdAt = supabaseUser.createdAt,
                updatedAt = supabaseUser.updatedAt
            )

            Result.success(mappedUser)

        } catch (e: Exception) {
            Result.failure(e)
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
            
            // After successful sign in, get the current user
            val supabaseUser = auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("User object is null after sign in"))
            
            val mappedUser = User(
                id = supabaseUser.id,
                email = supabaseUser.email ?: "",
                name = supabaseUser.userMetadata?.get("username")?.toString(),
                createdAt = supabaseUser.createdAt,
                updatedAt = supabaseUser.updatedAt
            )
            
            Result.success(mappedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): User? {
        return try {
            val supabaseUser = auth.currentUserOrNull() ?: return null
            
            User(
                id = supabaseUser.id,
                email = supabaseUser.email ?: "",
                name = supabaseUser.userMetadata?.get("username")?.toString(),
                createdAt = supabaseUser.createdAt,
                updatedAt = supabaseUser.updatedAt
            )
        } catch (e: Exception) {
            null
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
        return auth.currentSessionOrNull() != null
    }
}