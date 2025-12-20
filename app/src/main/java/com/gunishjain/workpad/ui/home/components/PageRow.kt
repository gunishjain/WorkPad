package com.gunishjain.workpad.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gunishjain.workpad.domain.model.Page
import com.gunishjain.workpad.ui.home.HomeAction


@Composable
fun PageRow(
    onAction: (HomeAction) -> Unit,
    page: Page
) {

    Column(){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAction(HomeAction.CollapsePrivateList) }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand/Collapse",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )


                Text(
                    text = "Private",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )

            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onAction(HomeAction.AddNote) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Page",
                        tint = Color.Gray
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
        PageRow(
            onAction = {},
            page = Page(
                id = "214124",
                parentId = null,
                title = "Page 1",
                content = "This is the content of page 1",
                createdAt = 2141414124,
                updatedAt = 21414141412,
                isFavorite = false
            )
        )
    }
}