package com.gunishjain.workpad.domain.model

data class User(
    val id: String,              // Maps to Supabase user_id (uuid)
    val email: String,
    val name: String?,
    val createdAt: Long,
    val updatedAt: Long
)
