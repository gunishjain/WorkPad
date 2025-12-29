package com.gunishjain.workpad.ui.notes

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun CreateNoteScreen(
    onNavigate: (CreateNoteEvent) -> Unit,
    viewModel: CreateNoteViewModel = hiltViewModel()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    onAction: (CreateNoteAction) -> Unit,
    uiState: CreateNoteUiState,
) {

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { onAction(CreateNoteAction.NavigateBack) }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (!uiState.isEditMode) {
                        AssistChip(
                            onClick = {},
                            label = { Text(uiState.parentTitle ?: "Private") },
                            leadingIcon = {
                                if (uiState.parentTitle == null) {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                    }

                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }

                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            // Title
            BasicTextField(
                value = uiState.title,
                onValueChange = {
                    // When user presses enter in title → move to content
                    if (it.contains("\n")) {
                        onAction(CreateNoteAction.OnTitleChange(it.substringBefore("\n")))
                        onAction(CreateNoteAction.OnContentChange(it.substringAfter("\n") + uiState.content))
                        focusManager.moveFocus(FocusDirection.Down)
                    } else {
                        onAction(CreateNoteAction.OnTitleChange(it))
                    }
                },
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                decorationBox = { inner ->
                    inner()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Content
            BasicTextField(
                value = uiState.content,
                onValueChange = { onAction(CreateNoteAction.OnContentChange(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 18.sp,
                    color = Color.Black
                ),
                decorationBox = { inner ->
                    inner()
                }
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun CreateNoteScreenPreview() {
    MaterialTheme {
        NoteEditorScreen(
            onAction = {},
            uiState = CreateNoteUiState()
        )
    }
}