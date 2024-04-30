package com.example.frontend.network

import com.example.frontend.data.model.Article
import com.example.frontend.data.model.request.LoginRequest
import com.example.frontend.data.model.response.ApiResponse
import com.example.frontend.navigation.Route
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.math.BigInteger

interface ApiService {
    // ---------------- USER ----------------
    @POST("login/")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse>

    // ---------------- HOME ----------------
    @GET("home/latest_subscribed_articles/")
    suspend fun getLatestSubscribedArticles(@Query("count") count: Int, @Query("offset") offset: Int): Response<List<Article>>
    @GET("home/latest_subscribed_articles_by_publisher/")
    suspend fun getLatestSubscribedArticlesByPublisher(@Query("count") count: Int, @Query("offset") offset: Int): Response<List<Article>>
    @GET("home/explore_articles/")
    suspend fun getExploreArticles(@Query("count") count: Int, @Query("offset") offset: Int): Response<List<Article>>

    // ---------------- READER ----------------
    @GET("reader/{article_id}/")
    suspend fun getArticle(@Path("article_id") articleId: BigInteger): Response<Article>
    @GET("reader/{article_id}/related_articles/")
    suspend fun getRelatedArticles(@Path("article_id") articleId: BigInteger, @Query("count") count: Int, @Query("offset") offset: Int): Response<List<Article>>

    // ---------------- COMMON ----------------
    @GET("common/{article_id}/bookmark/{bookmark_id}/")
    suspend fun bookmarkArticle(@Path("article_id") articleId: BigInteger, @Path("bookmark_id") bookmarkId: BigInteger): Response<ApiResponse>
    @GET("common/{article_id}/unbookmark/{bookmark_id}")
    suspend fun unbookmarkArticle(@Path("article_id") articleId: BigInteger, @Path("bookmark_id") bookmarkId: BigInteger): Response<ApiResponse>
    @GET("common/{publisher_id}/subscribe/")
    suspend fun subscribePublisher(@Path("publisher_id") publisherId: BigInteger): Response<ApiResponse>
    @GET("common/{publisher_id}/unsubscribe/")
    suspend fun unsubscribePublisher(@Path("publisher_id") publisherId: BigInteger): Response<ApiResponse>
}