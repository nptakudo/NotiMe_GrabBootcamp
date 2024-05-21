package com.example.frontend.ui.screens.subscription

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.math.BigInteger

@Composable
fun SubscriptionRoute(
    viewModel: SubscriptionViewModel,
    onSubscriptionClick: (publisherId: BigInteger) -> Unit,
    onSearchIconClick: () -> Unit,
    onToExplore: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.subscriptions) {
        viewModel.loadSources()
    }

    SubscriptionScreen(
        uiState = uiState,
        onRefresh = { viewModel.loadSources() },
        onSearchIconClick = onSearchIconClick,
        onSubscriptionClick = onSubscriptionClick,
        onSubscribe = { publisherId -> viewModel.onSubscribePublisher(publisherId) },
        onUnSubscribe = { publisherId -> viewModel.onUnsubscribePublisher(publisherId) },
        onToExplore = onToExplore
    )
}
