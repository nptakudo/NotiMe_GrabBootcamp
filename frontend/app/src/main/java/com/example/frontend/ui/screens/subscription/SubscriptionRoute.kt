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
    onSearchIconClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.subscriptions.isEmpty()) {
        LaunchedEffect(Unit) {
            viewModel.loadSources()
        }
    }

    SubscriptionScreen(
        uiState = uiState,
        onRefresh = { viewModel.loadSources() },
        onSearchIconClick = onSearchIconClick,
        onSubscriptionClick = onSubscriptionClick
    )
}
