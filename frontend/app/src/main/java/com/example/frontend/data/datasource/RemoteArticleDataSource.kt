package com.example.frontend.data.datasource

import com.example.frontend.data.model.Article
import com.example.frontend.data.model.ArticleMetadata
import com.example.frontend.data.model.Publisher
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

    suspend fun search(query: String, count: Int, offset: Int): List<ArticleMetadata> {
        val res = apiService.searchArticles(query, count, offset)
        if (!res.isSuccessful) {
            throw Exception("Failed to search articles")
        }
        return res.body()!!
    }

    suspend fun getArticlesByPublisher(publisherId: BigInteger, count: Int, offset: Int): List<ArticleMetadata> {
        val res = apiService.getArticlesByPublisher(publisherId, count, offset)
        if (!res.isSuccessful) {
            throw Exception("Failed to get articles by publisher")
        }
        return res.body()!!
    }

    suspend fun getNewArticle (url: String): Article {
        val res = apiService.getNewArticle(url)
        if (!res.isSuccessful) {
            throw Exception("Failed to get new article")
        }
        return res.body()!!
    }
}