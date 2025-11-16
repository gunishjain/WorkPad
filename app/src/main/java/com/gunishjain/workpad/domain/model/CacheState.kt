package com.gunishjain.workpad.domain.model

data class CacheState(
    val pageId: String,
    val lruScore: Long,
    val lfuScore: Int,
    val finalScore: Long,
    val isLocallyCached: Boolean
)
