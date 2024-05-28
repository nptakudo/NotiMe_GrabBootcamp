package com.example.frontend.data.repository

import com.example.frontend.data.datasource.RemoteSubscriptionDataSource
import java.math.BigInteger
import javax.inject.Inject

class SubscriptionRepository @Inject constructor(
    private val remoteSubscriptionDataSource: RemoteSubscriptionDataSource
) {
    suspend fun getSubscriptions() = remoteSubscriptionDataSource.getSubscriptions()
    suspend fun isPublisherSubscribed(publisherId: BigInteger) =
        remoteSubscriptionDataSource.isPublisherSubscribed(publisherId)

    suspend fun subscribePublisher(publisherId: BigInteger) =
        remoteSubscriptionDataSource.subscribePublisher(publisherId)

    suspend fun unsubscribePublisher(publisherId: BigInteger) =
        remoteSubscriptionDataSource.unsubscribePublisher(publisherId)

    suspend fun searchPublishers(query: String) =
        remoteSubscriptionDataSource.searchPublishers(query)
}