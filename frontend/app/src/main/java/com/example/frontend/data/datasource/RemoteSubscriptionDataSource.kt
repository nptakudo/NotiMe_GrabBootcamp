package com.example.frontend.data.datasource

import com.example.frontend.data.model.Publisher
import com.example.frontend.network.ApiService
import java.math.BigInteger
import javax.inject.Inject

class RemoteSubscriptionDataSource @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getSubscriptions(userId: BigInteger): List<Publisher> {
        val res = apiService.getSubscriptions(userId)
        if (!res.isSuccessful) {
            throw Exception("Failed to get subscribed publishers")
        }
        return res.body()!!
    }

    suspend fun isPublisherSubscribed(publisherId: BigInteger): Boolean {
        val res = apiService.isPublisherSubscribed(publisherId)
        if (!res.isSuccessful) {
            throw Exception("Failed to check if publisher is subscribed")
        }
        return res.body()!!.message.toBoolean()
    }

    suspend fun subscribePublisher(userId: BigInteger, publisherId: BigInteger) {
        val res = apiService.subscribePublisher(userId, publisherId)
        if (!res.isSuccessful) {
            throw Exception("Failed to subscribe publisher")
        }
    }

    suspend fun unsubscribePublisher(userId: BigInteger, publisherId: BigInteger) {
        val res = apiService.unsubscribePublisher(userId, publisherId)
        if (!res.isSuccessful) {
            throw Exception("Failed to unsubscribe publisher")
        }
    }
}