package com.example.frontend.data.model

import java.math.BigInteger

data class User(
    val userId: BigInteger,
    val username: String,
    val password: String
)