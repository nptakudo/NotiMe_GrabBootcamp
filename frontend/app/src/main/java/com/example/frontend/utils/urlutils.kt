package com.example.frontend.utils

import android.content.Context
import android.webkit.URLUtil
import coil.imageLoader
import coil.request.ImageRequest

fun isValidUrl(url: String?): Boolean {
    if (url.isNullOrBlank()) {
        return false
    }
    return URLUtil.isValidUrl(url)
}

suspend fun isValidImageUrl(context: Context, url: String?): Boolean {
    if (!isValidUrl(url)) {
        return false
    }
    var fetchSuccess = false
    val request = ImageRequest.Builder(context)
        .data(url!!)
        .listener(
            onSuccess = { _, _ -> fetchSuccess = true },
            onError = { _, _ -> fetchSuccess = false }
        )
        .build()
    context.imageLoader.execute(request)
    return fetchSuccess
}