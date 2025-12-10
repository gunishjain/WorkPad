package com.gunishjain.workpad.domain.repository

import com.gunishjain.workpad.domain.model.Page
import kotlinx.coroutines.flow.Flow

interface PageRepository {

    suspend fun createPage(page: Page) : Result<Unit>
    suspend fun deletePage(pageId: String): Result<Unit>
    suspend fun updatePage(page: Page): Result<Unit>
    suspend fun markFavorite(pageId: String): Result<Unit>
    suspend fun getPage(pageId: String): Flow<Page?>
    fun getAllPages(): Flow<List<Page>>
    fun getChildrenPages(parentPageId: String): Flow<List<Page>>
}