package com.example.frontend.data.repository

import com.example.frontend.data.datasource.RemoteRecsysDataSource
import java.math.BigInteger
import javax.inject.Inject

class RecsysRepository @Inject constructor(
    private val dataSource: RemoteRecsysDataSource
) {
    suspend fun getRelatedArticles(articleId: BigInteger, count: Int, offset: Int) =
        dataSource.getRelatedArticles(articleId, count, offset)

    suspend fun getLatestSubscribedArticles(count: Int, offset: Int) =
        dataSource.getLatestSubscribedArticles(count, offset)

    suspend fun getLatestSubscribedArticlesByPublisher(count: Int, offset: Int) =
        dataSource.getLatestSubscribedArticlesByPublisher(count, offset)

    suspend fun getExploreArticles(count: Int, offset: Int) =
        dataSource.getExploreArticles(count, offset)
}
