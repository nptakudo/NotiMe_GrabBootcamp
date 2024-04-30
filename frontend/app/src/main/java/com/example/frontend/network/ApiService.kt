package com.example.frontend.network

import com.example.frontend.data.model.request.LoginRequest
import com.example.frontend.data.model.response.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    // User
    @POST("/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse>
}