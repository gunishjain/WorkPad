package com.gunishjain.workpad.ui.home

sealed class HomeAction {

    object CollapsePrivateList : HomeAction()
    data class AddNote(val parentId: String?) : HomeAction()
    object ToggleSort : HomeAction()
    object SearchInNotes : HomeAction()

    data class OpenPage(val pageId: String) : HomeAction()
    data class AddChildPage(val parentId: String) : HomeAction()
}
