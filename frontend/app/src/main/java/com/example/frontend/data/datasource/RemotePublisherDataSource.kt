package com.example.frontend.data.datasource

import com.example.frontend.data.model.Publisher
import com.example.frontend.network.ApiService
import java.math.BigInteger
import javax.inject.Inject

class RemotePublisherDataSource @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getPublisherById(publisherId: BigInteger): Publisher {
        val res = apiService.getPublisherById(publisherId)
        if (!res.isSuccessful) {
            throw Exception("Failed to get publisher by id")
        }
        return res.body()!!
    }
}