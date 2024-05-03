package com.example.frontend.ui.screens.reader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.math.BigInteger

@Composable
fun ReaderRoute(
    viewModel: ReaderViewModel,
    onReadAnotherArticle: (articleId: BigInteger) -> Unit,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState == ReaderUiState.empty) {
        LaunchedEffect(Unit) {
            viewModel.refreshUiState()
        }
    }

    ReaderScreen(
        uiState = uiState,
        onRefresh = viewModel::refreshUiState,
        onFollow = viewModel::onSubscribePublisher,
        onUnfollow = viewModel::onUnsubscribePublisher,
        onBookmark = viewModel::onBookmarkArticle,
        onUnbookmark = viewModel::onUnbookmarkArticle,
        onShare = { /* TODO */ },
        onToBrowser = { /* TODO */ },
        onRelatedArticleClick = onReadAnotherArticle,
        onBookmarkRelatedArticle = viewModel::onBookmarkRelatedArticle,
        onUnbookmarkRelatedArticle = viewModel::onUnbookmarkRelatedArticle,
        onLoadMoreRelatedArticles = { /* TODO */ },
        onBack = onBack,
    )
}