package com.example.frontend.ui.screens.bookmark

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.frontend.data.model.BookmarkList
import com.example.frontend.ui.component.BookmarkCard
import com.example.frontend.ui.screens.home.Divider
import com.example.frontend.ui.theme.Colors
import com.example.frontend.ui.theme.UiConfig
import kotlinx.coroutines.launch
import java.math.BigInteger

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkScreen(
    modifier: Modifier = Modifier,
    uiState: BookmarkUiState,
    onRefresh: () -> Unit,
    onAddNewBookmark: () -> Unit,
    onDeleteBookmark: (articleId: BigInteger) -> Unit,
    onShareBoookmark: (articleId: BigInteger) -> Unit,
    onBookmarkDetail: (articleId: BigInteger) -> Unit
) {
    val refreshScope = rememberCoroutineScope()
    val refreshState = rememberPullToRefreshState()
    if (refreshState.isRefreshing) {
        refreshScope.launch {
            onRefresh()
            refreshState.endRefresh()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = "Your Bookmarks",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(start = 10.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Colors.topBarContainer
                )
            )

            Box(
                modifier = Modifier
                    .padding(
                        start = UiConfig.sideScreenPadding,
                        end = UiConfig.sideScreenPadding,
                    )
                    .fillMaxSize()
                    .nestedScroll(refreshState.nestedScrollConnection)
            ) {
                if (!refreshState.isRefreshing) {
                    BookmarkScreenContent(
                        bookmarks = uiState.bookmarks,
                        onAddNewBookmark = onAddNewBookmark,
                        onDeleteBookmark = onDeleteBookmark,
                        onShareBoookmark = onShareBoookmark,
                        onBookmarkDetail = onBookmarkDetail
                    )
                }
            }
        }
    }
}

@Composable
fun BookmarkScreenContent(
    modifier: Modifier = Modifier,
    bookmarks: List<BookmarkList>,
    onAddNewBookmark: () -> Unit,
    onDeleteBookmark: (articleId: BigInteger) -> Unit,
    onShareBoookmark: (articleId: BigInteger) -> Unit,
    onBookmarkDetail: (articleId: BigInteger) -> Unit
) {
    if (bookmarks.isNotEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    start = UiConfig.sideScreenPadding,
                    end = UiConfig.sideScreenPadding,
                    top = 16.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add new bookmarks list",
                    style = MaterialTheme.typography.titleMedium
                )
                Button(
                    modifier = Modifier
                        .width(120.dp),
                    onClick = onAddNewBookmark,
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "New list",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            bookmarks.forEach { bookmark ->
                BookmarkCard(
                    listName = if (bookmark.isSaved) "Saved Posts" else bookmark.name,
                    numArticle = bookmark.articles.size,
                    imgUrl = "https://findingtom.com/images/uploads/medium-logo/article-image-00.jpeg", //get the first article image url from bookmark.articles
                    onBookmarkClick = { onBookmarkDetail(bookmark.id) },
                    onShare = { onShareBoookmark(bookmark.id) },
                    onDelete = { onDeleteBookmark(bookmark.id) }
                )
                Divider()
            }
        }
    } else {
        Text(
            text = "Start subscribing to publishers to see articles here! Hop over to Explore to find new publishers.",
            style = MaterialTheme.typography.titleMedium
        )
    }
}