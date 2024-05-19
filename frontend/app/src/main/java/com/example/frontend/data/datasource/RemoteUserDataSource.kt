package com.example.frontend.data.datasource

import com.example.frontend.data.model.request.LoginRequest
import com.example.frontend.network.ApiService
import javax.inject.Inject

class RemoteUserDataSource @Inject constructor(
    private val apiService: ApiService,
    private val settingDataSource: SettingDataSource
) {
    suspend fun login(email: String, password: String) {
        val req = LoginRequest(email, password)
        val res = apiService.login(req)
        if (!res.isSuccessful) {
            throw Exception("Failed to login")
        }
        settingDataSource.setAccessToken(res.body()!!.token)
        settingDataSource.setUsername(res.body()!!.username)
        settingDataSource.setUserId(res.body()!!.id)
    }

}