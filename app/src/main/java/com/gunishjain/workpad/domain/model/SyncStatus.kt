package com.gunishjain.workpad.domain.model

data class SyncStatus(
    val pageId: String,
    val status: Status,
    val lastAttempt: Long?
)

enum class Status {
    PENDING,
    SYNCED,
    FAILED
}
