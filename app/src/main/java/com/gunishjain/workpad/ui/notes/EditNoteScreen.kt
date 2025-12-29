package com.gunishjain.workpad.ui.notes

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun EditNoteScreen(
    onNavigate: (CreateNoteEvent) -> Unit,
    viewModel: EditNoteViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.error) {
        uiState.error?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            onNavigate(event)
        }
    }

    NoteEditorScreen(
        onAction = viewModel::onAction,
        uiState = uiState
    )

}
