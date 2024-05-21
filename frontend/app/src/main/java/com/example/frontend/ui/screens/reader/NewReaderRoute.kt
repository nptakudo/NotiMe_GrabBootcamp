package com.example.frontend.ui.screens.reader

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.math.BigInteger

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