package com.gunishjain.workpad.data.mapper

import com.gunishjain.workpad.data.model.PageMetaDataEntity
import com.gunishjain.workpad.domain.model.PageMetaData

fun PageMetaDataEntity.toDomain(): PageMetaData {
    return PageMetaData(
        pageId = pageId,
        lastOpenedAt = lastOpenedAt,
        openCount = openCount,
    )
}

fun PageMetaData.toEntity(): PageMetaDataEntity {
    return PageMetaDataEntity(
        pageId = pageId,
        lastOpenedAt = lastOpenedAt,
        openCount = openCount,
    )
}
