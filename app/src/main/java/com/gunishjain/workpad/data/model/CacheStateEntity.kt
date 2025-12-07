package com.gunishjain.workpad.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache_state")
data class CacheStateEntity(
    @PrimaryKey @ColumnInfo(name = "page_id") val pageId: String,
    @ColumnInfo(name = "lru_score") val lruScore: Long,
    @ColumnInfo(name = "lfu_score") val lfuScore: Int,
    @ColumnInfo(name = "final_score") val finalScore: Long,
    @ColumnInfo(name = "is_cached") val isCached: Boolean
)
