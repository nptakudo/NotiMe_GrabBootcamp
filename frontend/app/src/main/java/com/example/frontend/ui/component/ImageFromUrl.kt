package com.example.frontend.ui.component

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun ImageFromUrl(
    modifier: Modifier = Modifier,
    url: String,
    contentDescription: String,
    contentScale: ContentScale = ContentScale.Crop,
) {
    Log.i("ImageFromUrl", "url: $url")
    Box(
        modifier = modifier
    ) {
        AsyncImage(
            model = url,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize()
        )
    }
}