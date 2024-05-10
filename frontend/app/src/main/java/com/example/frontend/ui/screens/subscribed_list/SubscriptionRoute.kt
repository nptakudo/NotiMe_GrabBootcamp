package com.example.frontend.ui.screens.subscribed_list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.frontend.data.model.Subscription
import com.example.frontend.navigation.Route

@Composable
fun SubscriptionRoute (
    viewModel: SubscriptionViewModel,
    onNavigateNavBar: (route: Route) -> Unit
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
        onSearchIconClick = { /* TODO */ },
        onNavigateNavBar = onNavigateNavBar
    )
}
