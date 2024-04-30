package com.example.frontend.data.datasource

import com.example.frontend.network.ApiService
import java.math.BigInteger
import javax.inject.Inject

class RemoteCommonDataSource @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun bookmarkArticle(articleId: BigInteger, bookmarkId: BigInteger) {
        val res = apiService.bookmarkArticle(articleId, bookmarkId)
        if (!res.isSuccessful) {
            throw Exception("Failed to bookmark article")
        }
    }
    suspend fun unbookmarkArticle(articleId: BigInteger, bookmarkId: BigInteger) {
        val res = apiService.unbookmarkArticle(articleId, bookmarkId)
        if (!res.isSuccessful) {
            throw Exception("Failed to unbookmark article")
        }
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
}