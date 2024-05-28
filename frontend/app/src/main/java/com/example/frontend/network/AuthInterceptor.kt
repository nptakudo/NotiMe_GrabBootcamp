package com.example.frontend.network

import com.example.frontend.data.datasource.SettingDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val settingDataSource: SettingDataSource) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val accessToken: String
        runBlocking {
            accessToken = settingDataSource.getAccessToken().first()
        }
        val requestWithHeader = chain.request()
            .newBuilder()
            .header(
                "Authorization", "Bearer $accessToken"
            ).build()
        return chain.proceed(requestWithHeader)
    }
}