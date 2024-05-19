package com.example.frontend.data.repository

import com.example.frontend.data.datasource.RemoteSubscriptionDataSource
import java.math.BigInteger
import javax.inject.Inject

class SubscriptionRepository @Inject constructor(
    private val remoteSubscriptionDataSource: RemoteSubscriptionDataSource
) {
    suspend fun getSubscriptions(userId: BigInteger) = remoteSubscriptionDataSource.getSubscriptions(userId)
    suspend fun isPublisherSubscribed(publisherId: BigInteger) =
        remoteSubscriptionDataSource.isPublisherSubscribed(publisherId)

    suspend fun subscribePublisher(userId: BigInteger, publisherId: BigInteger) =
        remoteSubscriptionDataSource.subscribePublisher(userId, publisherId)

    suspend fun unsubscribePublisher(userId: BigInteger, publisherId: BigInteger) =
        remoteSubscriptionDataSource.unsubscribePublisher(userId, publisherId)
}