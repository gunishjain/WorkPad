package com.gunishjain.workpad.data.mapper

import com.gunishjain.workpad.data.model.PageEntity
import com.gunishjain.workpad.domain.model.Page

fun PageEntity.toDomain(): Page {
    return Page(
        id = id,
        parentId = parentId,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isFavorite = isFavorite,
    )
}

fun Page.toEntity(): PageEntity {
    return PageEntity(
        id = id,
        parentId = parentId,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isFavorite = isFavorite
    )
}


