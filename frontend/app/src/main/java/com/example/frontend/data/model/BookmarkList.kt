package com.example.frontend.data.model

import java.math.BigInteger

data class BookmarkList(
    val id: BigInteger,
    val name: String,
    val ownerId: BigInteger,
    val isSaved: Boolean,
    val articles: List<Article>
)
