package com.gunishjain.workpad.ui.notes

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.gunishjain.workpad.domain.model.Page
import com.gunishjain.workpad.domain.repository.PageRepository
import com.gunishjain.workpad.ui.EditNoteRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    private val pageRepository: PageRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route: EditNoteRoute = savedStateHandle.toRoute()
    val pageId: String = route.pageId

    private val _uiState = MutableStateFlow(CreateNoteUiState(isEditMode = true))
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = Channel<CreateNoteEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private var originalPage: Page? = null

    init {
        loadNote()
        setupAutoSave()
    }

    fun onAction(action: CreateNoteAction) {
        when(action) {
            is CreateNoteAction.OnTitleChange -> _uiState.update { it.copy(title = action.title) }
            is CreateNoteAction.OnContentChange -> _uiState.update { it.copy(content = action.content) }
            CreateNoteAction.DiscardNote -> TODO()
            CreateNoteAction.NavigateBack -> moveBack()
        }
    }

    private fun loadNote() {
        viewModelScope.launch {
            pageRepository.getPage(pageId).firstOrNull()?.let { page ->
                originalPage = page
                _uiState.update { state ->
                    state.copy(
                        title = page.title,
                        content = page.content
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun setupAutoSave() {
        viewModelScope.launch {
            _uiState
                .map { it.title to it.content }
                .distinctUntilChanged()
                .debounce(500L)
                .filter { (title, content) ->
                    // Only save if content has actually changed from what we loaded
                    originalPage != null && (title != originalPage?.title || content != originalPage?.content)
                }
                .flatMapLatest { (title, content) ->
                    flow {
                        val pageToUpdate = originalPage?.copy(
                            title = title,
                            content = content,
                            updatedAt = System.currentTimeMillis()
                        )
                        if (pageToUpdate != null) {
                            emit(pageRepository.updatePage(pageToUpdate))
                        }
                    }
                }
                .flowOn(Dispatchers.IO)
                .collect { result ->
                    result.onFailure { error ->
                        Log.e("EditNoteViewModel", "Auto-save update failed: ${error.message}")
                    }
                }
        }
    }

    private fun moveBack() {
        viewModelScope.launch {
            // Immediate final save before exit
            val currentState = _uiState.value
            val pageToUpdate = originalPage?.copy(
                title = currentState.title,
                content = currentState.content,
                updatedAt = System.currentTimeMillis()
            )
            if (pageToUpdate != null && (currentState.title != originalPage?.title || currentState.content != originalPage?.content)) {
                pageRepository.updatePage(pageToUpdate)
            }
            _uiEvents.send(CreateNoteEvent.NavigateBack)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
