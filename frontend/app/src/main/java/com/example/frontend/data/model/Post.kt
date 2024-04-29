package com.example.frontend.data.model

import java.math.BigInteger

data class Post(
    val postId: BigInteger,
    val title: String,
    val url: String,
    val sourceId: BigInteger,
    // Consider about the cover image of each Post
)