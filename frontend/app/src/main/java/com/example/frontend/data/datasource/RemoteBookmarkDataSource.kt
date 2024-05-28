package com.example.frontend.data.datasource

import com.example.frontend.data.model.BookmarkList
import com.example.frontend.data.model.request.NewBookmarkRequest
import com.example.frontend.network.ApiService
import java.math.BigInteger
import javax.inject.Inject

class RemoteBookmarkDataSource @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getBookmarkLists(isShared: Boolean? = null): List<BookmarkList> {
        val res = apiService.getBookmarkLists(isShared)
        if (!res.isSuccessful) {
            throw Exception("Failed to get bookmark lists")
        }
        return res.body()!!
    }

    suspend fun createBookmarkList(name: String): BookmarkList {
        val res = apiService.createBookmarkList(NewBookmarkRequest(name))
        if (!res.isSuccessful) {
            throw Exception("Failed to create bookmark list")
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

    suspend fun bookmarkArticle(articleId: BigInteger, bookmarkId: BigInteger) {
        val res = apiService.bookmarkArticle(articleId, bookmarkId)
        if (!res.isSuccessful) {
            throw Exception("Failed to bookmark article")
        }
    }

    suspend fun unbookmarkArticle(articleId: BigInteger, bookmarkId: BigInteger) {
        val res = apiService.unbookmarkArticle(articleId, bookmarkId)
        if (!res.isSuccessful) {
            throw Exception("Failed to unbookmark article")
        }
    }

    suspend fun deleteBookmarkList(bookmarkId: BigInteger) {
        val res = apiService.deleteBookmarkList(bookmarkId)
        if (!res.isSuccessful) {
            throw Exception("Failed to delete bookmark list")
        }
    }
}