package com.gunishjain.workpad.data.repository

import com.gunishjain.workpad.data.local.WorkPadDao
import com.gunishjain.workpad.data.mapper.toDomain
import com.gunishjain.workpad.data.mapper.toEntity
import com.gunishjain.workpad.domain.model.Page
import com.gunishjain.workpad.domain.repository.AuthRepository
import com.gunishjain.workpad.domain.repository.SyncRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val remoteDB: Postgrest,
    private val localDB: WorkPadDao,
    private val authRepository: AuthRepository
) : SyncRepository {

    override suspend fun syncLocalToRemote(): Result<Unit> {
        return try {
            // Get current user ID
            val currentUser = authRepository.getCurrentUser()
                ?: return Result.failure(Exception("User not authenticated"))

            // Get all local pages (as a snapshot, not Flow)
            val localPages = localDB.getAllPages().first()

            // Convert to domain models
            val pages = localPages.map { it.toDomain() }

            // Upload each page to Supabase
            pages.forEach { page ->
                val remotePageDto = PageDto(
                    id = page.id,
                    userId = currentUser.id,
                    parentId = page.parentId,
                    title = page.title,
                    content = page.content,
                    createdAt = page.createdAt,
                    updatedAt = page.updatedAt,
                    isFavorite = page.isFavorite
                )

                // Upsert to Supabase (insert or update if exists)
                remoteDB.from("pages")
                    .upsert(remotePageDto)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncRemoteToLocal(): Result<Unit> {
        return try {
            // Get current user ID
            val currentUser = authRepository.getCurrentUser()
                ?: return Result.failure(Exception("User not authenticated"))

            // Fetch all pages from Supabase for current user
            val remotePages = remoteDB.from("pages")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("user_id", currentUser.id)
                    }
                }
                .decodeList<PageDto>()

            // Get all local pages for conflict resolution
            val localPages = localDB.getAllPages().first()
            val localPageMap = localPages.associateBy { it.id }

            // Sync each remote page
            remotePages.forEach { remoteDto ->
                val localPage = localPageMap[remoteDto.id]

                // Conflict resolution: if local exists and is newer, skip
                val shouldUpdate = if (localPage != null) {
                    remoteDto.updatedAt > localPage.updatedAt
                } else {
                    true // No local version, always insert
                }

                if (shouldUpdate) {
                    val pageEntity = Page(
                        id = remoteDto.id,
                        parentId = remoteDto.parentId,
                        title = remoteDto.title,
                        content = remoteDto.content,
                        createdAt = remoteDto.createdAt,
                        updatedAt = remoteDto.updatedAt,
                        isFavorite = remoteDto.isFavorite
                    ).toEntity()
                    localDB.upsertPage(pageEntity)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun enqueueSync(pageId: String): Result<Unit> {
        return try {
            // For Phase 1: Just trigger immediate sync
            // In Phase 2, add to sync_queue table
            // and let WorkManager handle it
            
            // For now, this is a no-op since PageRepository
            // can trigger sync directly when needed
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@Serializable
private data class PageDto(
    val id: String,
    val userId: String,
    val parentId: String? = null,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isFavorite: Boolean
)