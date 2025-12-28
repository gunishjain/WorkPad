package com.gunishjain.workpad.ui

import kotlinx.serialization.Serializable


@Serializable
data object LoginRoute

@Serializable
data object HomeRoute

@Serializable
data object SignUpRoute

@Serializable
data class CreateNoteRoute(
    val parentId: String? = null
)

@Serializable
data class EditNoteRoute(
    val pageId: String
)