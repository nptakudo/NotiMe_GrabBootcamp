package com.example.frontend.ui.screens.reader

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat.startActivity
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
        onShare = { ctx ->
            val shareIntent = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, uiState.article.metadata.url)
                type = "text/html"
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }, "Share article")
            startActivity(ctx, shareIntent, null)
        },
        onToBrowser = { ctx ->
            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(ctx, Uri.parse(uiState.article.metadata.url))
        },
        onRelatedArticleClick = onReadAnotherArticle,
        onBookmark = viewModel::onBookmarkRelatedArticle,
        onUnBookmark = viewModel::onUnbookmarkRelatedArticle,
        onNewBookmark = viewModel::onCreateNewBookmark,
        onLoadMoreRelatedArticles = { /* TODO */ },
        onBack = onBack,
    )
}