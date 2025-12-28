package com.gunishjain.workpad.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gunishjain.workpad.domain.repository.AuthRepository
import com.gunishjain.workpad.domain.repository.PageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = Channel<HomeNavigationEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    init {
        initialize()
    }

    private fun initialize() {
        viewModelScope.launch {
            // Give Supabase time to restore the session from storage
            delay(300)
            
            val isAuthenticated = authRepository.isAuthenticated()
            if (!isAuthenticated) {
                Log.d("HomeViewModel", "User not authenticated, navigating to login")
                _uiEvents.send(HomeNavigationEvent.NavigateToLogin)
            } else {
                Log.d("HomeViewModel", "User is authenticated, staying on home screen")
                // Only fetch data if authenticated
                fetchUserDetails()
                fetchPages()
            }
        }
    }

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

    private suspend fun fetchUserDetails() {
        authRepository.getCurrentUser()
            .onSuccess { user ->
                _uiState.update {
                    Log.d("HomeViewModel", "User fetched successfully: $user")
                    it.copy(userId = user?.id, username = user?.name)
                }
            }
            .onFailure { error ->
                Log.e("HomeViewModel", "Error fetching user details", error)
                _uiState.update {
                    it.copy(error = error.message)
                }
            }
    }

    private suspend fun fetchPages(){
        pagesRepository.getAllPages().catch { error ->
            Log.d("HomeViewModel", "Error fetching pages: ${error.message}")
            _uiState.update {
                it.copy(error = error.message)
            }
        }.collect { pages ->
            Log.d("HomeViewModel", "fetching pages: $pages")
            _uiState.update {
                it.copy(pages = pages)
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