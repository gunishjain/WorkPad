package com.gunishjain.workpad.data.mapper

import com.gunishjain.workpad.data.model.PageDto
import com.gunishjain.workpad.domain.model.Page

fun PageDto.toDomain(): Page {
    return Page(
        id = id,
        parentId = parentId,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isFavorite = isFavorite
    )
}

fun Page.toDto(userId: String): PageDto {
    return PageDto(
        id = id,
        userId = userId,
        parentId = parentId,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isFavorite = isFavorite
    )
}
