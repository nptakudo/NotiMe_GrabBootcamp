package com.example.frontend.data.model

data class Article(
    val metadata: ArticleMetadata,
    val content: ArticleContent,
    val summary: String
)