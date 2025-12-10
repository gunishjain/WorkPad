package com.gunishjain.workpad.domain.model

import kotlinx.datetime.Instant

data class User(
    val id: String,              // Maps to Supabase id
    val email: String,
    val name: String?,
    val createdAt: Instant?,
    val updatedAt: Instant?
)
