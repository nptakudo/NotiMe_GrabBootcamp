package com.example.frontend.data.model

import java.math.BigInteger

data class Article(
    val articleId: BigInteger,
    val title: String,
    val url: String,
    val publisherId: BigInteger,
    // Consider about the cover image of each Article
)