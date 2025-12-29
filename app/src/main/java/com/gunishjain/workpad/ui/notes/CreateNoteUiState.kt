package com.gunishjain.workpad.ui.notes

data class CreateNoteUiState(
    val title: String = "",
    val content: String = "",
    val isSaving: Boolean = false,
    val error: String? = null
)