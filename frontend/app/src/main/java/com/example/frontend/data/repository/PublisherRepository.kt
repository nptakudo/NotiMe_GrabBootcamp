package com.example.frontend.data.repository

import com.example.frontend.data.datasource.RemotePublisherDataSource
import java.math.BigInteger
import javax.inject.Inject

class PublisherRepository @Inject constructor(
    private val remotePublisherDataSource: RemotePublisherDataSource
) {
    suspend fun getPublisherById(publisherId: BigInteger) =
        remotePublisherDataSource.getPublisherById(publisherId)
}