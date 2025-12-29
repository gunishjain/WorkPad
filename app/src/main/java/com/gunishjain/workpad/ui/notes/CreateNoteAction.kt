package com.gunishjain.workpad.ui.notes

sealed class CreateNoteAction {
    data class OnTitleChange(val title: String) : CreateNoteAction()
    data class OnContentChange(val content: String) : CreateNoteAction()
    object SaveNote : CreateNoteAction()
    object DiscardNote : CreateNoteAction()
    object NavigateBack : CreateNoteAction()

}