package com.gunishjain.workpad.domain.model

data class PageMetaData(
    val pageId: String,
    val lastOpenedAt: Long,
    val openCount: Int              // for LFU
)
