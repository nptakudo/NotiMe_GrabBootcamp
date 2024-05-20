package com.example.frontend.data.model.response

import com.example.frontend.data.model.ArticleMetadata
import com.example.frontend.data.model.Publisher

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

data class SearchResponse(
    val isExisting: Boolean,
    val articles: List<ArticleMetadata>,
    val publishers: List<Publisher>
)