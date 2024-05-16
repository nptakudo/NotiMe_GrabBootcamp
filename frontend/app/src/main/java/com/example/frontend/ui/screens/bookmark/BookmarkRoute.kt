package com.example.frontend.ui.screens.bookmark

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.math.BigInteger


@Composable
fun BookmarkRoute (
    viewModel: BookmarkViewModel,
    onBookmarkDetail: (articleId: BigInteger) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.bookmarks.isEmpty()) {
        LaunchedEffect(Unit) {
            viewModel.onLoadBookmark()
        }
    }

    BookmarkScreen(
        uiState = uiState,
        onRefresh = { viewModel.onLoadBookmark() },
        onAddNewBookmark = {  },
        onDeleteBookmark = { articleId -> viewModel.onDeleteBookmark(articleId) },
        onShareBoookmark = { articleId -> viewModel.onShareBoookmark(articleId) },
        onBookmarkDetail = onBookmarkDetail
    )
}