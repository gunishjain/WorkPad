package com.gunishjain.workpad.ui.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue.EndToStart
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gunishjain.workpad.domain.model.Page
import com.gunishjain.workpad.ui.home.HomeAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageRow(
    page: Page,
    allPages: List<Page>,
    depth: Int = 0,
    onAction: (HomeAction) -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    // Animate arrow rotation for smooth UI
    val rotation by animateFloatAsState(targetValue = if (isExpanded) 90f else 0f)

    // Find children of this page from the provided list
    val children = allPages.filter { it.parentId == page.id }

    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == EndToStart) {
                onAction(HomeAction.DeletePage(page.id))
                true
            } else false
        }
    )

    Column(modifier = Modifier.fillMaxWidth()) {

        SwipeToDismissBox(
            state = swipeToDismissBoxState,
            enableDismissFromStartToEnd = false,
            backgroundContent = {
                val color = when (swipeToDismissBoxState.dismissDirection) {
                    EndToStart -> Color.Red
                    else -> Color.Transparent
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    if (swipeToDismissBoxState.dismissDirection == EndToStart) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.White
                        )
                    }
                }
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .clickable { onAction(HomeAction.OpenPage(page.id)) }
                    .padding(vertical = 4.dp)
                    .padding(start = (depth * 16).dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Expansion Icon
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(rotation)
                            .clickable { isExpanded = !isExpanded }
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = page.title.ifEmpty { "New Note" },
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Normal
                    )
                }

                if (isExpanded) {
                    IconButton(
                        onClick = { onAction(HomeAction.AddChildPage(page.id)) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Child Page",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        if (isExpanded) {
            if (children.isEmpty()) {
                // Base Case: No children found
                Text(
                    text = "No pages inside",
                    fontSize = 14.sp,
                    color = Color.LightGray,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .padding(start = ((depth + 1) * 24).dp)
                )
            } else {
                // Recursive Step: Render each child as a PageRow with depth + 1
                children.forEach { child ->
                    PageRow(
                        page = child,
                        allPages = allPages,
                        depth = depth + 1,
                        onAction = onAction
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PageRowPreview() {
    MaterialTheme {
        val mockPages = listOf(
            Page("1", null, "Parent", "", 0, 0, false),
            Page("2", "1", "Child 1", "", 0, 0, false),
            Page("3", "2", "Grandchild", "", 0, 0, false)
        )
        Column {
            PageRow(
                page = mockPages[0],
                allPages = mockPages,
                onAction = {}
            )
        }
    }
}