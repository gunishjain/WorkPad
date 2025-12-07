package com.gunishjain.workpad.data.mapper

import com.gunishjain.workpad.data.model.StatusEntity
import com.gunishjain.workpad.data.model.SyncQueueEntity
import com.gunishjain.workpad.domain.model.Status
import com.gunishjain.workpad.domain.model.SyncStatus

fun SyncQueueEntity.toDomain(): SyncStatus {
    return SyncStatus(
        pageId = pageId,
        lastAttempt = lastAttempt,
        status = status.toDomain()
    )
}

fun SyncStatus.toEntity(): SyncQueueEntity {
    return SyncQueueEntity(
        pageId = pageId,
        lastAttempt = lastAttempt,
        status = status.toEntity()
    )
}


fun StatusEntity.toDomain() = when (this) {
    StatusEntity.PENDING -> Status.PENDING
    StatusEntity.SYNCED -> Status.SYNCED
    StatusEntity.FAILED -> Status.FAILED
}

fun Status.toEntity() = when (this) {
    Status.PENDING -> StatusEntity.PENDING
    Status.SYNCED -> StatusEntity.SYNCED
    Status.FAILED -> StatusEntity.FAILED
}