package com.gunishjain.workpad.data.repository

import com.gunishjain.workpad.data.local.WorkPadDao
import com.gunishjain.workpad.data.mapper.toDomain
import com.gunishjain.workpad.data.mapper.toEntity
import com.gunishjain.workpad.domain.model.Page
import com.gunishjain.workpad.domain.repository.PageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PageRepositoryImpl @Inject constructor(
    private val pageDao: WorkPadDao
) : PageRepository {
    override suspend fun createPage(page: Page): Result<Unit> {
        return try {
            pageDao.upsertPage(page.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePage(pageId: String): Result<Unit> {
        return try {
            pageDao.deletePageById(pageId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePage(page: Page): Result<Unit> {
        return try {
             pageDao.upsertPage(page.toEntity())
             Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markFavorite(pageId: String): Result<Unit> {
        return try {
            pageDao.updateFavoriteStatus(
                pageId,
                isFavorite = true
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getPage(pageId: String): Flow<Page?> {
        return pageDao.getPage(pageId)
            .map { it?.toDomain() }
    }

    override fun getAllPages(): Flow<List<Page>> {
        return pageDao.getAllPages().map {
            it.map { pageEntity ->
                pageEntity.toDomain()
            }
        }
    }

    override fun getChildrenPages(parentPageId: String?): Flow<List<Page>> {
       return pageDao.getChildren(parentPageId).map {
           it.map { pageEntity ->
               pageEntity.toDomain()
           }
       }
    }
}