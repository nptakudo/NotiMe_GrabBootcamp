package com.example.frontend.data.repository

import com.example.frontend.data.datasource.ApiService
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun login(email: String, password: String) {

    }
}