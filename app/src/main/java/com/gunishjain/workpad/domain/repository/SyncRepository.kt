package com.gunishjain.workpad.domain.repository

interface SyncRepository {

    suspend fun syncLocalToRemote(): Result<Unit>
    suspend fun syncRemoteToLocal(): Result<Unit>
    suspend fun enqueueSync(pageId: String): Result<Unit>

}