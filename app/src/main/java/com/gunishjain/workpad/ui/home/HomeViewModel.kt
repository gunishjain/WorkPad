package com.gunishjain.workpad.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gunishjain.workpad.domain.repository.AuthRepository
import com.gunishjain.workpad.domain.repository.PageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val pagesRepository: PageRepository
) : ViewModel() {

    init {
        fetchUserDetails()
        fetchPages()
    }
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = Channel<HomeNavigationEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    fun onAction(action: HomeAction){
        when(action){
            is HomeAction.AddNote -> addNote(action.parentId)
            HomeAction.CollapsePrivateList -> {
               _uiState.update {
                   it.copy(
                       isPrivateListCollapsed = !it.isPrivateListCollapsed
                   )
               }
            }
            HomeAction.SearchInNotes -> TODO()
            HomeAction.ToggleSort -> TODO()
            is HomeAction.AddChildPage -> addNote(action.parentId)
            is HomeAction.OpenPage -> toggleParentPage(action.pageId)
        }
    }

    private fun toggleParentPage(pageId: String) {
        _uiState.update {
            it.copy(
                isParentPageExpanded = it.isParentPageExpanded.apply {
                    this[pageId] = !(this[pageId] ?: false)
                }
            )
        }
    }

    private fun fetchUserDetails() {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                _uiState.update {
                    it.copy(userId = user?.id, username = user?.name)
                }
            } catch (e: Exception) {
                Log.d("HomeViewModel", "Error fetching user details: ${e.message}")
                TODO("Navigate to login screen")
            }
        }
    }

    private fun fetchPages(){
        viewModelScope.launch {
           pagesRepository.getAllPages().catch { error ->
               Log.d("HomeViewModel", "Error fetching pages: ${error.message}")
               _uiState.update {
                   it.copy(error = error.message)
               }
           }.collect { pages ->
               _uiState.update {
                   it.copy(pages = pages)
               }
           }
        }
    }

    private fun addNote(parentId: String?){

        Log.d("HomeViewModel", "Add Page clicked with parentId: $parentId")
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }



}