package com.gunishjain.workpad.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Stores usage stats for caching algorithm (LFU).
@Entity(tableName = "page_metadata")
data class PageMetaDataEntity(
    @PrimaryKey @ColumnInfo(name = "page_id") val pageId: String,
    @ColumnInfo(name = "last_opened_at") val lastOpenedAt: Long,
    @ColumnInfo(name = "open_count") val openCount: Int
)
