package com.example.frontend.data.repository

import com.example.frontend.data.datasource.RemoteUserDataSource
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val remoteUserDataSource: RemoteUserDataSource
) {
    suspend fun login(email: String, password: String) = remoteUserDataSource.login(email, password)
}