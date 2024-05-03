package com.example.frontend.data.repository

import com.example.frontend.data.datasource.RemoteArticleDataSource
import com.example.frontend.data.model.Article
import java.math.BigInteger
import javax.inject.Inject

class ArticleRepository @Inject constructor(
    private val remoteArticleDataSource: RemoteArticleDataSource
) {
    suspend fun getArticleMetadataAndContentById(articleId: BigInteger): Article =
        remoteArticleDataSource.getArticleMetadataAndContentById(articleId)
}