package com.example.frontend.data.model

import java.math.BigInteger

data class User(
    val id: BigInteger,
    val username: String,
    val password: String
)