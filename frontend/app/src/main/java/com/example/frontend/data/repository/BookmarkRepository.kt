package com.example.frontend.data.repository

import com.example.frontend.data.datasource.RemoteBookmarkDataSource
import java.math.BigInteger
import javax.inject.Inject

class BookmarkRepository @Inject constructor(
    private val remoteBookmarkDataSource: RemoteBookmarkDataSource
) {
    suspend fun getBookmarkLists(isShared: Boolean? = null) = remoteBookmarkDataSource.getBookmarkLists(isShared)

    suspend fun createBookmarkList(name: String) = remoteBookmarkDataSource.createBookmarkList(name)

    suspend fun getBookmarkListById(bookmarkId: BigInteger) =
        remoteBookmarkDataSource.getBookmarkListById(bookmarkId)

    suspend fun isArticleBookmarked(articleId: BigInteger, bookmarkId: BigInteger) =
        remoteBookmarkDataSource.isArticleBookmarked(articleId, bookmarkId)

    suspend fun bookmarkArticle(articleId: BigInteger, bookmarkId: BigInteger) =
        remoteBookmarkDataSource.bookmarkArticle(articleId, bookmarkId)

    suspend fun unbookmarkArticle(articleId: BigInteger, bookmarkId: BigInteger) =
        remoteBookmarkDataSource.unbookmarkArticle(articleId, bookmarkId)

    suspend fun deleteBookmarkList(bookmarkId: BigInteger) = remoteBookmarkDataSource.deleteBookmarkList(bookmarkId)
}