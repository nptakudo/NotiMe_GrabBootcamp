package com.example.frontend.network

import android.util.Log
import okhttp3.Interceptor
import javax.inject.Inject

class RequestInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        Log.i("RequestInterceptor", "Sent request: ${request.url}")
        return chain.proceed(request)
    }
}