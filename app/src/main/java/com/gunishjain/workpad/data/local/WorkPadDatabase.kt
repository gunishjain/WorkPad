package com.gunishjain.workpad.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gunishjain.workpad.data.model.CacheStateEntity
import com.gunishjain.workpad.data.model.PageEntity
import com.gunishjain.workpad.data.model.PageMetaDataEntity
import com.gunishjain.workpad.data.model.SyncQueueEntity

@Database(entities = [PageEntity::class, SyncQueueEntity::class, PageMetaDataEntity::class, CacheStateEntity::class], version = 1, exportSchema = false)
abstract class WorkPadDatabase : RoomDatabase() {
    abstract fun workPadDao(): WorkPadDao
}