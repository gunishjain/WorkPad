package com.gunishjain.workpad.data.mapper

import com.gunishjain.workpad.data.model.CacheStateEntity
import com.gunishjain.workpad.domain.model.CacheState

fun CacheStateEntity.toDomain(): CacheState {
    return CacheState(
        pageId = pageId,
        lruScore = lruScore,
        lfuScore = lfuScore,
        finalScore = finalScore,
        isLocallyCached = isCached
    )
}

fun CacheState.toEntity(): CacheStateEntity {
    return CacheStateEntity(
        pageId = pageId,
        lruScore = lruScore,
        lfuScore = lfuScore,
        finalScore = finalScore,
        isCached = isLocallyCached
    )
}