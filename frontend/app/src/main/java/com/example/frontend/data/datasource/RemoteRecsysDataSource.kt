package com.example.frontend.data.datasource

import com.example.frontend.data.model.Article
import com.example.frontend.network.ApiService
import java.math.BigInteger
import javax.inject.Inject

class RemoteRecsysDataSource @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getRelatedArticles(articleId: BigInteger, count: Int, offset: Int): List<Article> {
        val res = apiService.getRelatedArticles(articleId, count, offset)
        if (res.isSuccessful) {
            return res.body()!!
        } else {
            throw Exception("Failed to get related articles")
        }
    }
    suspend fun getLatestSubscribedArticles(count: Int, offset: Int): List<Article> {
        val res = apiService.getLatestSubscribedArticles(count, offset)
        if (res.isSuccessful) {
            return res.body()!!
        } else {
            throw Exception("Failed to get latest subscribed articles")
        }
    }
    suspend fun getLatestSubscribedArticlesByPublisher(count: Int, offset: Int): List<Article> {
        val res = apiService.getLatestSubscribedArticlesByPublisher(count, offset)
        if (res.isSuccessful) {
            return res.body()!!
        } else {
            throw Exception("Failed to get latest subscribed articles by publisher")
        }
    }
    suspend fun getExploreArticles(count: Int, offset: Int): List<Article> {
        val res = apiService.getExploreArticles(count, offset)
        if (res.isSuccessful) {
            return res.body()!!
        } else {
            throw Exception("Failed to get explore articles")
        }
    }
}