package com.example.frontend.network

import okhttp3.Interceptor
import javax.inject.Inject

class RequestInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        println("[Network] Sent request: ${request.url}")
        return chain.proceed(request)
    }
}