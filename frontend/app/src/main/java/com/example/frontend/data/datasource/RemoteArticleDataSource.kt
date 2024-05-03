package com.example.frontend.data.datasource

import com.example.frontend.data.model.Article
import com.example.frontend.network.ApiService
import java.math.BigInteger
import javax.inject.Inject

class RemoteArticleDataSource @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getArticleMetadataAndContentById(articleId: BigInteger): Article {
        val res = apiService.getArticleMetadataAndContentById(articleId)
        if (!res.isSuccessful) {
            throw Exception("Failed to get article metadata and content by id")
        }
        return res.body()!!
    }
}