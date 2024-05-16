package com.example.frontend.ui.screens.article_list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.math.BigInteger

@Composable
fun ArticleListRoute (
    viewModel: ArticleListViewModel,
    articleType: ArticleType,
    id: BigInteger,
    onBack: () -> Unit,
    onArticleClick: (articleId: BigInteger) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.articles.isEmpty()) {
        LaunchedEffect(Unit) {
            when (articleType) {
                ArticleType.PUBLISHER -> viewModel.onLoadArticlesByPublisher(id)
                ArticleType.BOOKMARK -> viewModel.onLoadArticlesByBookmarkList(id)
            }
        }
    }

    ArticleListScreen(
        uiState = uiState,
        onRefresh = {
            when (articleType) {
                ArticleType.PUBLISHER -> viewModel.onLoadArticlesByPublisher(id)
                ArticleType.BOOKMARK -> viewModel.onLoadArticlesByBookmarkList(id)
            }
        },
        onBack = onBack,
        onArticleClick = onArticleClick
    )
}
enum class ArticleType {
    PUBLISHER,
    BOOKMARK
}