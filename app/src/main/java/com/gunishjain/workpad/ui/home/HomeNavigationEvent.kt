package com.gunishjain.workpad.ui.home

sealed class HomeNavigationEvent {

    data class NavigateToCreateNote(val parentId: String?) : HomeNavigationEvent()
    data class NavigateToNote(val noteId: Long): HomeNavigationEvent()
    object NavigateToLogin : HomeNavigationEvent()

}