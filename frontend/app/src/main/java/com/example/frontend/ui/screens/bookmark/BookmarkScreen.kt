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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.frontend.data.model.BookmarkList
import com.example.frontend.ui.component.BookmarkCard
import com.example.frontend.ui.component.BottomSheetNewBookmarkContent
import com.example.frontend.ui.theme.Colors
import com.example.frontend.ui.theme.UiConfig
import kotlinx.coroutines.launch
import java.math.BigInteger

object BookmarkUiConfig {
    enum class BottomSheetContentType(val id: Int) {
        NONE(1),
        NEW_BOOKMARK(2),
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkScreen(
    modifier: Modifier = Modifier,
    uiState: BookmarkUiState,
    onRefresh: () -> Unit,
    onAddNewBookmark: (name: String) -> Unit,
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

    Column(modifier = modifier.fillMaxSize()) {
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
            ),
            modifier = Modifier.zIndex(1f)
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
                if (uiState.state == State.Loading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Loading...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    BookmarkScreenContent(
                        bookmarks = uiState.bookmarks,
                        onAddNewBookmark = onAddNewBookmark,
                        onDeleteBookmark = onDeleteBookmark,
                        onShareBookmark = onShareBoookmark,
                        onBookmarkDetail = onBookmarkDetail
                    )
                }
            }
            PullToRefreshContainer(
                state = refreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                containerColor = Colors.topBarContainer
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkScreenContent(
    modifier: Modifier = Modifier,
    bookmarks: List<BookmarkList>,
    onAddNewBookmark: (name: String) -> Unit,
    onDeleteBookmark: (articleId: BigInteger) -> Unit,
    onShareBookmark: (articleId: BigInteger) -> Unit,
    onBookmarkDetail: (articleId: BigInteger) -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState()
    val bottomSheetScope = rememberCoroutineScope()
    var bottomSheetContent by rememberSaveable {
        mutableStateOf(BookmarkUiConfig.BottomSheetContentType.NONE)
    }
    val expandBottomSheet: (BookmarkUiConfig.BottomSheetContentType) -> Unit = {
        bottomSheetContent = it
        bottomSheetScope.launch { bottomSheetState.show() }
    }

    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                start = UiConfig.sideScreenPadding,
                end = UiConfig.sideScreenPadding,
                top = 16.dp,
            )
            .verticalScroll(scrollState),
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
                text = "Add new bookmark list",
                style = MaterialTheme.typography.titleMedium
            )
            Button(
                modifier = Modifier
                    .width(120.dp),
                onClick = {
                    expandBottomSheet(BookmarkUiConfig.BottomSheetContentType.NEW_BOOKMARK)
                },
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
                // TODO
                imgUrl = "https://findingtom.com/images/uploads/medium-logo/article-image-00.jpeg", //get the first article image url from bookmark.articles
                disableDelete = bookmark.isSaved,
                onBookmarkClick = { onBookmarkDetail(bookmark.id) },
                onShare = { onShareBookmark(bookmark.id) },
                onDelete = { onDeleteBookmark(bookmark.id) }
            )
            HorizontalDivider()
        }
        if (bottomSheetContent != BookmarkUiConfig.BottomSheetContentType.NONE) {
            val onClose: (() -> Unit) -> Unit = { afterClose ->
                bottomSheetScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                    if (!bottomSheetState.isVisible) {
                        bottomSheetContent = BookmarkUiConfig.BottomSheetContentType.NONE
                    }
                    afterClose()
                }
            }
            ModalBottomSheet(
                onDismissRequest = { onClose {} },
                sheetState = bottomSheetState
            ) {
                if (bottomSheetContent == BookmarkUiConfig.BottomSheetContentType.NEW_BOOKMARK) {
                    BottomSheetNewBookmarkContent(
                        onCreateNewBookmark = { name ->
                            onAddNewBookmark(name)
                            onClose {}
                        },
                        onClose = { onClose {} },
                    )
                }
            }
        }
    }
}