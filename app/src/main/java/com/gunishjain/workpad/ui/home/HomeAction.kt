package com.gunishjain.workpad.ui.home

sealed class HomeAction {

    object CollapsePrivateList : HomeAction()
    object AddNote : HomeAction()
    object ToggleSort : HomeAction()
    object SearchInNotes : HomeAction()

}
