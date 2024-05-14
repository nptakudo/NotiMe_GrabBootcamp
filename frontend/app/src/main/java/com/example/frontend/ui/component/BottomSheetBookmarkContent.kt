package com.example.frontend.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.frontend.data.model.BookmarkList
import com.example.frontend.ui.theme.UiConfig
import java.math.BigInteger

@Composable
fun BottomSheetBookmarkContent(
    modifier: Modifier = Modifier,
    articleId: BigInteger,
    bookmarkLists: List<BookmarkList>,
    onNewBookmarkList: () -> Unit,
    onBookmark: (bookmarkId: BigInteger) -> Unit,
    onUnBookmark: (bookmarkId: BigInteger) -> Unit,
    onClose: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(
                start = UiConfig.sideScreenPadding,
                end = UiConfig.sideScreenPadding,
                bottom = 16.dp,
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Bookmark to...",
                style = MaterialTheme.typography.titleMedium,
            )
            Row(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onNewBookmarkList() }
                    .padding(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "New bookmark"
                )
                Text(
                    text = "New",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                    ),
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp),
        )
        bookmarkLists.forEach { bookmarkList ->
            val isContaining = bookmarkList.articles.any { it.id == articleId }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = isContaining,
                    onCheckedChange = {
                        if (it) {
                            onBookmark(bookmarkList.id)
                        } else {
                            onUnBookmark(bookmarkList.id)
                        }
                    }
                )
                Text(
                    text = bookmarkList.name,
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp),
        )
        Row(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .clickable { onClose() }
                .padding(5.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = "Done"
            )
            Text("Done")
        }
    }
}