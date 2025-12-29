package com.gunishjain.workpad.ui.notes

sealed class CreateNoteEvent {
    object NavigateBack : CreateNoteEvent()

}