package com.gunishjain.workpad.ui.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gunishjain.workpad.ui.home.components.PageRow

@Composable
fun HomeScreen(
    onNavigate: (HomeNavigationEvent) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
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

    HomeScreen(
        onAction = viewModel::onAction,
        uiState = uiState
    )

}

@Composable
fun HomeScreen(
    onAction: (HomeAction) -> Unit,
    uiState: HomeUiState
) {


    Scaffold(
        containerColor = Color.White,
        floatingActionButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FloatingActionButton(
                    onClick = { onAction(HomeAction.SearchInNotes) },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }

                FloatingActionButton(
                    onClick = { onAction(HomeAction.AddNote(null)) },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Page"
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "${uiState.username}'s Notes",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if(uiState.favorites.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAction(HomeAction.CollapseFavList) }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Favourites",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            imageVector = if (uiState.isFavListCollapsed) Icons.AutoMirrored.Filled.KeyboardArrowLeft else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand/Collapse",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                }
            }

            if (!uiState.isFavListCollapsed) {
                uiState.favorites.forEach { page ->
                    PageRow(
                        page = page,
                        allPages = uiState.pages,
                        onAction = onAction
                    ) 
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction(HomeAction.CollapsePrivateList) }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Private",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = if(uiState.isPrivateListCollapsed) Icons.AutoMirrored.Filled.KeyboardArrowLeft else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand/Collapse",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {  }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options",
                            tint = Color.Gray
                        )
                    }

                    IconButton(
                        onClick = { onAction(HomeAction.AddNote(null)) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Page",
                            tint = Color.Gray
                        )
                    }
                }
            }

            if (!uiState.isPrivateListCollapsed) {
                val rootPages = uiState.pages.filter { it.parentId == null }

                if (rootPages.isEmpty()) {
                    Text(
                        text = "No Pages Found, press on '+' to add a new page",
                        modifier = Modifier.padding(vertical = 8.dp).padding(start = 16.dp),
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                } else {
                    rootPages.forEach { page ->
                        PageRow(
                            page = page,
                            allPages = uiState.pages,
                            onAction = onAction
                        )
                    }
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            onAction = {},
            uiState = HomeUiState(username = "Gunish")
        )
    }
}