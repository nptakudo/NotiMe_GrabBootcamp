package com.example.frontend.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.frontend.navigation.Route
import java.math.BigInteger

@Composable
fun HomeRoute(
    viewModel: HomeViewModel,
    onArticleClick: (BigInteger) -> Unit,
    onNavigateNavBar: (route: Route) -> Unit,
    onAboutClick: () -> Unit,
    onLogOutClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onLoadMoreArticles = { viewModel.onLoadMoreArticles() },
        onBookmark = viewModel::onBookmarkArticle,
        onUnbookmark = viewModel::onUnbookmarkArticle,
        onNewBookmark = viewModel::onCreateNewBookmark,
        onSubscribePublisher = { publisherId -> viewModel.onSubscribePublisher(publisherId) },
        onUnsubscribePublisher = { publisherId -> viewModel.onUnsubscribePublisher(publisherId) },
        onRefresh = { viewModel.refreshUiState() },
        onSearchIconClick = viewModel::onStartSearch,
        onSearchSubmit = viewModel::onSearchSubmit,
        onSearchCancel = viewModel::onCancelSearch,
        onArticleClick = onArticleClick,
        onNavigateNavBar = onNavigateNavBar,
        onAboutClick = onAboutClick,
        onLogOutClick = onLogOutClick
    )
}