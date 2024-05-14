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
        onShare = { /* TODO */ },
        onToBrowser = { /* TODO */ },
        onRelatedArticleClick = onReadAnotherArticle,
        onBookmark = viewModel::onBookmarkRelatedArticle,
        onUnBookmark = viewModel::onUnbookmarkRelatedArticle,
        onNewBookmark = viewModel::onCreateNewBookmark,
        onLoadMoreRelatedArticles = { /* TODO */ },
        onBack = onBack,
    )
}