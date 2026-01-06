package com.gunishjain.workpad.ui.notes

data class CreateNoteUiState(
    val title: String = "",
    val content: String = "",
    val parentTitle: String? = null,
    val isEditMode: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val showBottomSheet: Boolean = false,
    val isFavorite: Boolean = false
)