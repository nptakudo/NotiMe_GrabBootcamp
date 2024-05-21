package com.example.frontend.data.repository

import com.example.frontend.data.datasource.RemoteArticleDataSource
import com.example.frontend.data.model.Article
import com.example.frontend.data.model.ArticleMetadata
import java.math.BigInteger
import javax.inject.Inject

class ArticleRepository @Inject constructor(
    private val remoteArticleDataSource: RemoteArticleDataSource
) {
    suspend fun getArticleMetadataAndContentById(articleId: BigInteger): Article =
        remoteArticleDataSource.getArticleMetadataAndContentById(articleId)

    suspend fun search(query: String, count: Int, offset: Int): List<ArticleMetadata> =
        remoteArticleDataSource.search(query, count, offset)

    suspend fun getArticlesByPublisher(publisherId: BigInteger, count: Int, offset: Int): List<ArticleMetadata> =
        remoteArticleDataSource.getArticlesByPublisher(publisherId, count, offset)

    suspend fun getNewArticle(url: String): Article =
        remoteArticleDataSource.getNewArticle(url)
}