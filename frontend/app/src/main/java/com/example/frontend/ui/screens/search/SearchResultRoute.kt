package com.example.frontend.ui.screens.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.math.BigInteger

@Composable
fun SearchResultRoute(
    viewModel: SearchResultViewModel,
    query: String,
    obBack: () -> Unit,
    onSubscriptionClick: (publisherId: BigInteger) -> Unit,
    onReadNewArticle: (url: String) -> Unit,
    onAddSourceSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.search(query)
    }
    SearchResultScreen(
        uiState = uiState,
        onBack = obBack,
        query = query,
        onSubscriptionClick = onSubscriptionClick,
        onSubscribe = { publisherId -> viewModel.onSubscribePublisher(publisherId) },
        onUnSubscribe = { publisherId -> viewModel.onUnsubscribePublisher(publisherId) },
        onLoadNewArticle = onReadNewArticle,
        onAddSource = { publisher ->
            viewModel.onAddSource(publisher)
            onAddSourceSuccess()
        }
    )
}