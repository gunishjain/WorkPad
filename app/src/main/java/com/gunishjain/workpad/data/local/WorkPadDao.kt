package com.gunishjain.workpad.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gunishjain.workpad.data.model.PageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkPadDao {

    // Helper: Replacing a page works for both Create and Update
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPage(page: PageEntity)

    @Delete
    suspend fun deletePage(page: PageEntity)

    // For recursive deletion or just simpler access
    @Query("DELETE FROM pages WHERE id = :pageId")
    suspend fun deletePageById(pageId: String)

    @Query("SELECT * FROM pages WHERE id = :pageId")
    fun getPage(pageId: String): Flow<PageEntity?>

    // Returns all pages. Useful for searching or initial sync logic
    @Query("SELECT * FROM pages")
    fun getAllPages(): Flow<List<PageEntity>>

    // Key function for your hierarchical UI
    // Handles root pages (parentId IS NULL) and children
    @Query("SELECT * FROM pages WHERE parent_id = :parentId OR (:parentId IS NULL AND parent_id IS NULL)")
    fun getChildren(parentId: String?): Flow<List<PageEntity>>

    // Efficiently update favorite status without replacing entire object
    @Query("UPDATE pages SET is_favorite = :isFavorite WHERE id = :pageId")
    suspend fun updateFavoriteStatus(pageId: String, isFavorite: Boolean)
}
