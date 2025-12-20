package com.gunishjain.workpad.ui.home

sealed class HomeNavigationEvent {

    data class NavigateToNote(val noteId: Long): HomeNavigationEvent()

}