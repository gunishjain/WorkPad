package com.gunishjain.workpad.data.repository

import com.gunishjain.workpad.data.local.WorkPadDao
import com.gunishjain.workpad.data.mapper.toDomain
import com.gunishjain.workpad.data.mapper.toDto
import com.gunishjain.workpad.data.mapper.toEntity
import com.gunishjain.workpad.domain.model.Page
import com.gunishjain.workpad.domain.repository.AuthRepository
import com.gunishjain.workpad.domain.repository.PageRepository
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PageRepositoryImpl @Inject constructor(
    private val remoteDB: Postgrest,
    private val localDB: WorkPadDao,
    private val authRepository: AuthRepository
) : PageRepository {
    override suspend fun createPage(page: Page): Result<Unit> {
        return try {
            // Get current user for RLS (Row Level Security) compliance
            val currentUser = authRepository.getCurrentUser().getOrElse { 
                return Result.failure(it)
            } ?: return Result.failure(Exception("User not authenticated"))

            //Save to Supabase first
            val pageDto = page.toDto(userId = currentUser.id)
            remoteDB.from("pages")
                .insert(pageDto)

            localDB.upsertPage(page.toEntity())
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePage(pageId: String): Result<Unit> {
        return try {

            val currentUser = authRepository.getCurrentUser().getOrElse { 
                return Result.failure(it)
            } ?: return Result.failure(Exception("User not authenticated"))

            // 1. Delete from Supabase first
            remoteDB.from("pages")
                .delete {
                    filter {
                        eq("id", pageId)
                        eq("user_id", currentUser.id) // RLS enforcement
                    }
                }

            // 2. Remove from local cache
            localDB.deletePageById(pageId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePage(page: Page): Result<Unit> {
        return try {

            val currentUser = authRepository.getCurrentUser().getOrElse { 
                return Result.failure(it)
            } ?: return Result.failure(Exception("User not authenticated"))

            // 1. Update in Supabase first
            val pageDto = page.toDto(userId = currentUser.id)
            remoteDB.from("pages")
                .upsert(pageDto)

            // 2. Sync to local cache
            localDB.upsertPage(page.toEntity())
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markFavorite(pageId: String): Result<Unit> {
        return try {

            val currentUser = authRepository.getCurrentUser().getOrElse { 
                return Result.failure(it)
            } ?: return Result.failure(Exception("User not authenticated"))

            // 1. Update in Supabase first
            remoteDB.from("pages")
                .update(
                    {
                        set("is_favorite", true)
                    }
                ) {
                    filter {
                        eq("id", pageId)
                        eq("user_id", currentUser.id) // RLS enforcement
                    }
                }

            // 2. Sync to local cache
            localDB.updateFavoriteStatus(pageId, isFavorite = true)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getPage(pageId: String): Flow<Page?> {
        return localDB.getPage(pageId)
            .map { it?.toDomain() }
    }

    override fun getAllPages(): Flow<List<Page>> {
        return localDB.getAllPages().map {
            it.map { pageEntity ->
                pageEntity.toDomain()
            }
        }
    }

    override fun getChildrenPages(parentPageId: String?): Flow<List<Page>> {
       return localDB.getChildren(parentPageId).map {
           it.map { pageEntity ->
               pageEntity.toDomain()
           }
       }
    }
}