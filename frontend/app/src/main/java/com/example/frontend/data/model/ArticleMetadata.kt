package com.example.frontend.data.model

import java.math.BigInteger
import java.util.Date

data class ArticleMetadata(
    val id: BigInteger,
    val title: String,
    val url: String,
    val publisher: Publisher,
    val date: Date,
    val isBookmarked: Boolean,
    val imageUrl: String?,
)