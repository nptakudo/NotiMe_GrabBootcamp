package com.example.frontend.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.frontend.utils.isValidImageUrl
import kotlinx.coroutines.async

@Composable
fun BigArticleCard(
    modifier: Modifier = Modifier,
    textPadding: Dp = 5.dp,
    isBookmarked: Boolean,
    publisherAvatarUrl: String?,
    articleImageUrl: String?,
    title: String,
    publisher: String,
    date: String,
    onClick: () -> Unit,
    onBookmarkClick: (isBookmarked: Boolean) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val isValidArticleImage = remember { mutableStateOf(false) }
    LaunchedEffect(articleImageUrl) {
        isValidArticleImage.value = articleImageUrl?.let { url ->
            coroutineScope.async {
                isValidImageUrl(context, url)
            }.await()
        } ?: false
    }
    val isValidPublisherImage = remember { mutableStateOf(false) }
    LaunchedEffect(publisherAvatarUrl) {
        isValidPublisherImage.value = publisherAvatarUrl?.let { url ->
            coroutineScope.async {
                isValidImageUrl(context, url)
            }.await()
        } ?: false
    }

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        ),
        shape = MaterialTheme.shapes.large,
        modifier = modifier.heightIn(max = 320.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (isValidArticleImage.value) {
                ImageFromUrl(
                    url = articleImageUrl!!,
                    contentDescription = "Article Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.large)
                )
            }
            Column(
                modifier = Modifier.padding(
                    start = textPadding,
                    end = textPadding,
                    bottom = textPadding
                )
            ) {
                Text(
                    text = title.trim(),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isValidPublisherImage.value) {
                            ImageFromUrl(
                                url = publisherAvatarUrl!!,
                                contentDescription = "Publisher Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .requiredWidth(24.dp)
                                    .requiredHeight(24.dp)
                                    .clip(MaterialTheme.shapes.small)
                            )
                        }
                        Text(
                            text = publisher.trim(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                        Text(
                            text = "·",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                        Text(
                            text = date,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                    IconButton(
                        onClick = { onBookmarkClick(isBookmarked) },
                        modifier = Modifier
                            .padding(0.dp)
                    ) {
                        if (isBookmarked) {
                            Icon(
                                imageVector = Icons.Filled.Bookmark,
                                contentDescription = "Click to unbookmark",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.BookmarkBorder,
                                contentDescription = "Click to bookmark",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
