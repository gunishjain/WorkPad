package com.gunishjain.workpad.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("parent_id") val parentId: String? = null,
    val title: String,
    val content: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("is_favorite") val isFavorite: Boolean
)
