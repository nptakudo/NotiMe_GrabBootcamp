package com.example.frontend.data.repository

import com.example.frontend.data.datasource.RemoteUserDataSource
import com.example.frontend.network.ApiService
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val remoteUserDataSource: RemoteUserDataSource
) {
    suspend fun login(email: String, password: String) = remoteUserDataSource.login(email, password)
}