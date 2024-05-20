package com.example.frontend.network

import com.example.frontend.data.model.Article
import com.example.frontend.data.model.ArticleMetadata
import com.example.frontend.data.model.BookmarkList
import com.example.frontend.data.model.Publisher
import com.example.frontend.data.model.request.LoginRequest
import com.example.frontend.data.model.response.ApiResponse
import com.example.frontend.data.model.response.LoginResponse
import com.example.frontend.data.model.response.SearchResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.math.BigInteger

interface ApiService {
    // ---------------- USER ----------------
    @POST("auth/login/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // ---------------- HOME ----------------
    @GET("home/latest_subscribed_articles/")
    suspend fun getLatestSubscribedArticles(
        @Query("count") count: Int,
        @Query("offset") offset: Int
    ): Response<List<ArticleMetadata>>

    @GET("home/latest_subscribed_articles_by_publisher/")
    suspend fun getLatestSubscribedArticlesByPublisher(
        @Query("count") count: Int,
        @Query("offset") offset: Int
    ): Response<List<ArticleMetadata>>

    @GET("home/search/{query}/")
    suspend fun searchArticles(
        @Path("query") query: String,
        @Query("count") count: Int,
        @Query("offset") offset: Int
    ): Response<List<ArticleMetadata>>

    // ---------------- EXPLORE ---------------
    @GET("home/explore_articles/")
    suspend fun getExploreArticles(
        @Query("count") count: Int,
        @Query("offset") offset: Int
    ): Response<List<ArticleMetadata>>

    // ---------------- READER ----------------
    @GET("reader/{article_id}/")
    suspend fun getArticleMetadataAndContentById(@Path("article_id") articleId: BigInteger): Response<Article>

    @GET("reader/{article_id}/related_articles/")
    suspend fun getRelatedArticles(
        @Path("article_id") articleId: BigInteger,
        @Query("count") count: Int,
        @Query("offset") offset: Int
    ): Response<List<ArticleMetadata>>

    // ---------------- Model: BOOKMARK ----------------
    @GET("bookmarks/")
    suspend fun getBookmarkLists(): Response<List<BookmarkList>>

    @GET("bookmarks/{bookmark_id}/")
    suspend fun getBookmarkListById(@Path("bookmark_id") bookmarkId: BigInteger): Response<BookmarkList>

    @GET("bookmarks/{bookmark_id}/{article_id}/")
    suspend fun isArticleBookmarked(
        @Path("article_id") articleId: BigInteger,
        @Path("bookmark_id") bookmarkId: BigInteger
    ): Response<ApiResponse>

    @PUT("bookmarks/{bookmark_id}/{article_id}/")
    suspend fun bookmarkArticle(
        @Path("article_id") articleId: BigInteger,
        @Path("bookmark_id") bookmarkId: BigInteger
    ): Response<ApiResponse>

    @DELETE("bookmarks/{bookmark_id}/{article_id}/")
    suspend fun unbookmarkArticle(
        @Path("article_id") articleId: BigInteger,
        @Path("bookmark_id") bookmarkId: BigInteger
    ): Response<ApiResponse>

    // ---------------- Model: SUBSCRIPTION ----------------
    @GET("subscriptions/user/{user_id}/")
    suspend fun getSubscriptions(@Path("user_id") userId: BigInteger): Response<List<Publisher>>

    @GET("subscriptions/publisher/{publisher_id}/")
    suspend fun isPublisherSubscribed(@Path("publisher_id") publisherId: BigInteger): Response<ApiResponse>

    @GET("subscriptions/{user_id}/search/")
    suspend fun searchPublishers(
        @Path("user_id") userId: BigInteger,
        @Query("query") query: String
    ): Response<SearchResponse>

    @PUT("subscriptions/{user_id}/{publisher_id}/")
    suspend fun subscribePublisher(
        @Path("user_id") userId: BigInteger,
        @Path("publisher_id") publisherId: BigInteger
    ): Response<ApiResponse>

    @DELETE("subscriptions/{user_id}/{publisher_id}/")
    suspend fun unsubscribePublisher(
        @Path("user_id") userId: BigInteger,
        @Path("publisher_id") publisherId: BigInteger
    ): Response<ApiResponse>

    // ---------------- Model: PUBLISHER ----------------
    @GET("publishers/{publisher_id}/")
    suspend fun getPublisherById(@Path("publisher_id") publisherId: BigInteger): Response<Publisher>
}