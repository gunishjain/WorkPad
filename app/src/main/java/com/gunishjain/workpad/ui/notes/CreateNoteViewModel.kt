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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
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

    private val noteId = UUID.randomUUID().toString()
    private val createdAt = System.currentTimeMillis()
    private var hasBeenSaved = false

    init {
        fetchParentTitle()
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun setupAutoSave() {
        viewModelScope.launch {
            _uiState
                .map { it.title to it.content }
                .distinctUntilChanged()
                .debounce(500L)
                .filter { (title, content) -> title.isNotBlank() || content.isNotBlank() }
                .flatMapLatest { (title, content) ->
                    flow {
                        val page = Page(
                            id = noteId,
                            parentId = parentId,
                            title = title,
                            content = content,
                            createdAt = createdAt,
                            updatedAt = System.currentTimeMillis(),
                            isFavorite = false
                        )
                        
                        val result = if (!hasBeenSaved) {
                            pageRepository.createPage(page).onSuccess { hasBeenSaved = true }
                        } else {
                            pageRepository.updatePage(page)
                        }
                        emit(result)
                    }
                }
                .flowOn(Dispatchers.IO)
                .collect { result ->
                    result.onFailure { error -> 
                        Log.e("CreateNoteViewModel", "Auto-save failed: ${error.message}") 
                    }
                }
        }
    }

    private fun fetchParentTitle() {
        parentId?.let { id ->
            viewModelScope.launch {
                pageRepository.getPage(id).collect { parentPage ->
                    _uiState.update { it.copy(parentTitle = parentPage?.title) }
                }
            }
        }
    }

    private fun moveBack() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.title.isNotBlank() || currentState.content.isNotBlank()) {
                val page = Page(
                    id = noteId,
                    parentId = parentId,
                    title = currentState.title,
                    content = currentState.content,
                    createdAt = createdAt,
                    updatedAt = System.currentTimeMillis(),
                    isFavorite = false
                )
                if (!hasBeenSaved) {
                    pageRepository.createPage(page)
                } else {
                    pageRepository.updatePage(page)
                }
            }
            _uiEvents.send(CreateNoteEvent.NavigateBack)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
