package com.gunishjain.workpad.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey @ColumnInfo(name = "page_id") val pageId: String,
    val status: StatusEntity,
    @ColumnInfo(name = "last_attempt") val lastAttempt: Long?,
)

enum class StatusEntity {
    PENDING,
    SYNCED,
    FAILED
}
