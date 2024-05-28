package com.example.frontend.ui.screens.reader

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NewReaderRoute(
    viewModel: NewReaderViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ReaderScreenForNewArticle (
        uiState = uiState,
        onToBrowser = { ctx ->
            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(ctx, Uri.parse(uiState.article.metadata.url))
        },
        onBack = onBack,
        onRefresh = { viewModel.refreshUiState() },
    )
}