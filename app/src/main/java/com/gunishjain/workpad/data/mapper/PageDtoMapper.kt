package com.gunishjain.workpad.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.gunishjain.workpad.data.model.PageDto
import com.gunishjain.workpad.domain.model.Page
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
fun PageDto.toDomain(): Page {
     return Page(
        id = id,
        parentId = parentId,
        title = title,
        content = content,
        createdAt = try { Instant.parse(createdAt).toEpochMilli() } catch (e: Exception) { 0L },
        updatedAt = try { Instant.parse(updatedAt).toEpochMilli() } catch (e: Exception) { 0L },
        isFavorite = isFavorite
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun Page.toDto(userId: String): PageDto {
    return PageDto(
        id = id,
        userId = userId,
        parentId = parentId,
        title = title,
        content = content,
        createdAt = Instant.ofEpochMilli(createdAt).toString(),
        updatedAt = Instant.ofEpochMilli(updatedAt).toString(),
        isFavorite = isFavorite
    )
}
