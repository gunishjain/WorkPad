package com.gunishjain.workpad.ui.notes.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gunishjain.workpad.ui.notes.CreateNoteAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteSettingBottomSheet(
    toggleBottomSheet: Boolean,
    isFavorite: Boolean,
    onAction: (CreateNoteAction) -> Unit
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (toggleBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { onAction(CreateNoteAction.ToggleBottomSheet) },
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = null
        ) {
            BottomSheetContent(
                isFavorite = isFavorite,
                onAction = onAction
            )
        }
    }
}


@Composable
fun BottomSheetContent(
    isFavorite: Boolean,
    onAction: (CreateNoteAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 48.dp)
    ) {
        ListItem(
            headlineContent = { Text("Delete Note") },
            leadingContent = {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
            },
            colors = ListItemDefaults.colors(
                headlineColor = Color.Red,
                containerColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth().clickable { 
                onAction(CreateNoteAction.DeleteNote)
            }
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))

        ListItem(
            headlineContent = { 
                Text(if (isFavorite) "Remove from Favorites" else "Add to Favorites") 
            },
            leadingContent = {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorite) Color.Red else Color.Black
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth().clickable { 
                onAction(CreateNoteAction.ToggleFavorite)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ModalBottomSheetExamplePreview() {
    MaterialTheme {
        NoteSettingBottomSheet(
            toggleBottomSheet = true,
            isFavorite = false,
            onAction = {}
        )
    }
}


