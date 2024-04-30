package com.example.frontend.data.repository

import com.example.frontend.data.datasource.RemoteCommonDataSource
import java.math.BigInteger
import javax.inject.Inject

class CommonRepository @Inject constructor(
    private val dataSource: RemoteCommonDataSource
) {
    suspend fun bookmarkArticle(articleId: BigInteger, bookmarkId: BigInteger) = dataSource.bookmarkArticle(articleId, bookmarkId)
    suspend fun unbookmarkArticle(articleId: BigInteger, bookmarkId: BigInteger) = dataSource.unbookmarkArticle(articleId, bookmarkId)
    suspend fun subscribePublisher(publisherId: BigInteger) = dataSource.subscribePublisher(publisherId)
    suspend fun unsubscribePublisher(publisherId: BigInteger) = dataSource.unsubscribePublisher(publisherId)
}