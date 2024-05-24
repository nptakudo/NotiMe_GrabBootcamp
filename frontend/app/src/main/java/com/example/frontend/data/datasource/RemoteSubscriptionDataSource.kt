package com.example.frontend.data.datasource

import android.util.Log
import com.example.frontend.data.model.Publisher
import com.example.frontend.data.model.response.SearchResponse
import com.example.frontend.network.ApiService
import java.math.BigInteger
import javax.inject.Inject

class RemoteSubscriptionDataSource @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getSubscriptions(): List<Publisher> {
        val res = apiService.getSubscriptions()
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

    suspend fun subscribePublisher(publisherId: BigInteger) {
        val res = apiService.subscribePublisher(publisherId)
        if (!res.isSuccessful) {
            throw Exception("Failed to subscribe publisher")
        }
    }

    suspend fun unsubscribePublisher(publisherId: BigInteger) {
        val res = apiService.unsubscribePublisher(publisherId)
        if (!res.isSuccessful) {
            throw Exception("Failed to unsubscribe publisher")
        }
    }

    suspend fun searchPublishers(query: String): SearchResponse {
        val res = apiService.searchPublishers(query)
        if (!res.isSuccessful) {
            throw Exception("Failed to search publishers")
        }
        Log.i("RemoteSubscriptionDataSource", res.body().toString())
        return res.body()!!
    }
}