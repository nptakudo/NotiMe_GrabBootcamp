package com.example.frontend.ui.screens.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SearchResultRoute (
    viewModel: SearchResultViewModel,
    query: String,
    obBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.search(query)
    }
    SearchResultScreen(
        uiState = uiState,
        onBack = obBack,
        query = query
    )
}