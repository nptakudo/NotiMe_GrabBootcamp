package com.example.frontend.data.model

import java.math.BigInteger

data class ReadingList(
    val id: BigInteger,
    val name: String,
    val ownerId: BigInteger,
    val isSaved: Boolean,
    val articles: List<Article>
)
