package com.gunishjain.workpad.domain.model

data class Page(
     val id: String,
     val parentId: String?,
     val title: String,
     val content: String,
     val createdAt: Long,
     val updatedAt: Long,
     val isFavorite: Boolean
)
