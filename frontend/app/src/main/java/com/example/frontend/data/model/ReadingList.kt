package com.example.frontend.data.model

import java.math.BigInteger

data class ReadingList(
    val listId: BigInteger,
    val listName: String,
    val owner: BigInteger,
    val isSaved: Boolean,
    val articleList: List<Article>
)
