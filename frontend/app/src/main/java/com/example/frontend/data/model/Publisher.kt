package com.example.frontend.data.model

import java.math.BigInteger

data class Publisher(
    val publisherId: BigInteger,
    val name: String,
    val url: String,
    val avatarUrl: String
)