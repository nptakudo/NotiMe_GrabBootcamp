package com.example.frontend.data.model.response

data class ApiResponse(
    val message: String,
    val data: Any
)

// create the login response data class wiht id, username and token
data class LoginResponse(
    val id: Int,
    val username: String,
    val token: String
)