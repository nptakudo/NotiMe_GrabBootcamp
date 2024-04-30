package com.example.frontend.data.datasource

import com.example.frontend.data.model.BookmarkList
import com.example.frontend.network.ApiService
import java.math.BigInteger
import javax.inject.Inject

class RemoteBookmarkDataSource @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getBookmarkLists(): List<BookmarkList> {
        val res = apiService.getBookmarkLists()
        if (!res.isSuccessful) {
            throw Exception("Failed to get bookmark lists")
        }
        return res.body()!!
    }

    suspend fun getBookmarkListById(bookmarkId: BigInteger): BookmarkList {
        val res = apiService.getBookmarkListById(bookmarkId)
        if (!res.isSuccessful) {
            throw Exception("Failed to get bookmark list by id")
        }
        return res.body()!!
    }

    suspend fun isArticleBookmarked(articleId: BigInteger, bookmarkId: BigInteger): Boolean {
        val res = apiService.isArticleBookmarked(articleId, bookmarkId)
        if (!res.isSuccessful) {
            throw Exception("Failed to check if article is bookmarked")
        }
        return res.body()!!.message.toBoolean()
    }

    suspend fun bookmarkArticle(articleId: BigInteger, bookmarkId: BigInteger): String {
        val res = apiService.bookmarkArticle(articleId, bookmarkId)
        if (!res.isSuccessful) {
            throw Exception("Failed to bookmark article")
        }
        return res.body()!!.message
    }

    suspend fun unbookmarkArticle(articleId: BigInteger, bookmarkId: BigInteger): String {
        val res = apiService.unbookmarkArticle(articleId, bookmarkId)
        if (!res.isSuccessful) {
            throw Exception("Failed to unbookmark article")
        }
        return res.body()!!.message
    }
}