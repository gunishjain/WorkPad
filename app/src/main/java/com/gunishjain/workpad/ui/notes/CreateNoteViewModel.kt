package com.gunishjain.workpad.ui.notes

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.gunishjain.workpad.domain.model.Page
import com.gunishjain.workpad.domain.repository.PageRepository
import com.gunishjain.workpad.ui.CreateNoteRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(
    private val pageRepository: PageRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route: CreateNoteRoute = savedStateHandle.toRoute()
    val parentId: String? = route.parentId

    private val _uiState = MutableStateFlow(CreateNoteUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = Channel<CreateNoteEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    fun onAction(action: CreateNoteAction) {
        when(action) {
            is CreateNoteAction.OnTitleChange -> updateTitle(action.title)
            is CreateNoteAction.OnContentChange -> updateContent(action.content)
            CreateNoteAction.SaveNote -> saveNote()
            CreateNoteAction.DiscardNote -> TODO()
            CreateNoteAction.NavigateBack -> moveBack()
        }
    }

    private fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    private fun updateContent(content: String) {
        _uiState.update { it.copy(content = content) }
    }

    private fun moveBack() {
        viewModelScope.launch {
            _uiEvents.send(CreateNoteEvent.NavigateBack)
        }
    }

    private fun saveNote() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val page = Page(
                id = UUID.randomUUID().toString(),
                parentId = parentId,
                title = _uiState.value.title,
                content = _uiState.value.content,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                isFavorite = false
            )

            pageRepository.createPage(page)
                .onSuccess {
                    _uiEvents.send(CreateNoteEvent.NavigateBack)
                }
                .onFailure { error ->

                    Log.d("CreateNoteViewModel", "Error saving note: ${error.message}")
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            error = error.message
                        )
                    }
                }
            }

        }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
