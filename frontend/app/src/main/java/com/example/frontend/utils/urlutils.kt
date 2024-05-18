package com.example.frontend.utils

import android.webkit.URLUtil

fun isValidUrl(url: String?): Boolean {
    if (url.isNullOrBlank()) {
        return false
    }
    return URLUtil.isValidUrl(url)
}