package com.example.frontend.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.model.ArticleMetadata
import com.example.frontend.data.model.BookmarkList
import com.example.frontend.data.model.Publisher
import com.example.frontend.data.repository.BookmarkRepository
import com.example.frontend.data.repository.RecsysRepository
import com.example.frontend.data.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.util.Date
import javax.inject.Inject

object HomeConfig {
    const val LOG_TAG = "HomeViewModel"
    const val LOAD_COUNT = 20
}

data class HomeUiState(
    val articles: List<ArticleMetadata>,
    val bookmarks: List<BookmarkList>,
    val state: State,
) {
    companion object {
        val empty = HomeUiState(
            articles = emptyList(),
            bookmarks = emptyList(),
            state = State.Idle
        )
    }
}

enum class State {
    Idle,
    Loading,
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val recsysRepository: RecsysRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {
    private var _articles = MutableStateFlow(emptyList<ArticleMetadata>())
    private var _bookmarks = MutableStateFlow(emptyList<BookmarkList>())
    private var _uiState = MutableStateFlow(HomeUiState.empty)

    val uiState = _uiState.combine(_articles) { uiState, articles ->
        uiState.copy(
            articles = articles.sortedByDescending { it.date }
        )
    }.combine(_bookmarks) { uiState, bookmarks ->
        uiState.copy(
            bookmarks = bookmarks.sortedBy { it.name }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeUiState.empty,
    )

    fun onBookmarkArticle(articleId: BigInteger, bookmarkId: BigInteger) {
        viewModelScope.launch {
            try {
                bookmarkRepository.bookmarkArticle(articleId, bookmarkId)
                _bookmarks.update { bookmarkRepository.getBookmarkLists() }
                updateBookmarkedState(articleId)
            } catch (e: Exception) {
                Log.e(HomeConfig.LOG_TAG, "Failed to bookmark article")
            }
        }
    }

    fun onUnbookmarkArticle(articleId: BigInteger, bookmarkId: BigInteger) {
        viewModelScope.launch {
            try {
                bookmarkRepository.unbookmarkArticle(articleId, bookmarkId)
                _bookmarks.update { bookmarkRepository.getBookmarkLists() }
                updateBookmarkedState(articleId)
            } catch (e: Exception) {
                Log.e(HomeConfig.LOG_TAG, "Failed to unbookmark article")
            }
        }
    }

    fun onSubscribePublisher(publisherId: BigInteger) {
        viewModelScope.launch {
            try {
                subscriptionRepository.subscribePublisher(publisherId)
            } catch (e: Exception) {
                Log.e(HomeConfig.LOG_TAG, "Failed to subscribe publisher")
            }
        }
    }

    fun onUnsubscribePublisher(publisherId: BigInteger) {
        viewModelScope.launch {
            try {
                subscriptionRepository.unsubscribePublisher(publisherId)
            } catch (e: Exception) {
                Log.e(HomeConfig.LOG_TAG, "Failed to unsubscribe publisher")
            }
        }
    }

    fun onCreateNewBookmark(name: String, articleId: BigInteger) {
        viewModelScope.launch {
            try {
                val bookmarkId = bookmarkRepository.createBookmarkList(name)
                bookmarkRepository.addToBookmarkList(articleId, bookmarkId)
                // TODO
//                _bookmarks.update { bookmarkRepository.getBookmarkLists() }
                _bookmarks.update {
                    it + BookmarkList(
                        id = bookmarkId,
                        name = name,
                        articles = emptyList(),
                        isSaved = true,
                        ownerId = BigInteger.ONE
                    )
                }
                updateBookmarkedState(articleId)
            } catch (e: Exception) {
                Log.e(HomeConfig.LOG_TAG, "Failed to create new bookmark")
            }
        }
    }

    private fun updateBookmarkedState(articleId: BigInteger? = null) {
        _articles.update { articleList ->
            articleList.map { article ->
                if (articleId != null && article.id != articleId) {
                    article
                } else
                    if (_bookmarks.value.any { bookmarkList -> bookmarkList.articles.any { it.id == article.id } }) {
                        article.copy(isBookmarked = true)
                    } else {
                        article.copy(isBookmarked = false)
                    }
            }
        }
    }

    fun onLoadMoreArticles() {
        val offset = _articles.value.size
        refreshUiState(offset, HomeConfig.LOAD_COUNT)
    }

    init {
        refreshUiState()
    }

    fun refreshUiState(offset: Int = 0, count: Int = HomeConfig.LOAD_COUNT) {
        // TODO
        _uiState.update { it.copy(state = State.Loading) }
        _articles.update {
            // mock data
            listOf(
                ArticleMetadata(
                    id = BigInteger.ONE,
                    title = "Article 1",
                    url = "https://picsum.photos/200",
                    publisher = Publisher(
                        id = BigInteger.ONE,
                        name = "Publisher 1",
                        url = "https://example.com/publisher1",
                        avatarUrl = "https://picsum.photos/200",
                        isSubscribed = false
                    ),
                    date = Date(),
                    isBookmarked = false,
                    articleImageUrl = "https://picsum.photos/200",
                ),
                ArticleMetadata(
                    id = BigInteger.valueOf(2),
                    title = "ArticleArticleArticle 2Article 2Article 2Article 2Article 2",
                    url = "https://picsum.photos/200",
                    publisher = Publisher(
                        id = BigInteger.valueOf(2),
                        name = "Publisher 2",
                        url = "https://example.com/publisher2",
                        avatarUrl = "https://picsum.photos/200",
                        isSubscribed = false
                    ),
                    date = Date(),
                    isBookmarked = true,
                    articleImageUrl = "https://picsum.photos/200",
                ),
                ArticleMetadata(
                    id = BigInteger.valueOf(3),
                    title = "Article 3",
                    url = "https://picsum.photos/200",
                    publisher = Publisher(
                        id = BigInteger.valueOf(2),
                        name = "Publisher 2",
                        url = "https://example.com/publisher2",
                        avatarUrl = "https://picsum.photos/200",
                        isSubscribed = false
                    ),
                    // yesterday
                    date = Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000),
                    isBookmarked = true,
                    articleImageUrl = "https://picsum.photos/200",
                ),
                ArticleMetadata(
                    id = BigInteger.valueOf(4),
                    title = "Article 4",
                    url = "https://picsum.photos/200",
                    publisher = Publisher(
                        id = BigInteger.valueOf(2),
                        name = "Publisher 2",
                        url = "https://example.com/publisher2",
                        avatarUrl = "https://picsum.photos/200",
                        isSubscribed = false
                    ),
                    // yesterday
                    date = Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000),
                    isBookmarked = true,
                    articleImageUrl = "https://picsum.photos/200",
                ),
                ArticleMetadata(
                    id = BigInteger.valueOf(5),
                    title = "Article 5",
                    url = "https://picsum.photos/200",
                    publisher = Publisher(
                        id = BigInteger.valueOf(2),
                        name = "Publisher 2",
                        url = "https://example.com/publisher2",
                        avatarUrl = "https://picsum.photos/200",
                        isSubscribed = false
                    ),
                    // before yesterday
                    date = Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000),
                    isBookmarked = true,
                    articleImageUrl = "https://picsum.photos/200",
                ),
                ArticleMetadata(
                    id = BigInteger.valueOf(6),
                    title = "Article 6",
                    url = "https://picsum.photos/200",
                    publisher = Publisher(
                        id = BigInteger.valueOf(2),
                        name = "Publisher 2",
                        url = "https://example.com/publisher2",
                        avatarUrl = "https://picsum.photos/200",
                        isSubscribed = false
                    ),
                    // before yesterday
                    date = Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000),
                    isBookmarked = true,
                    articleImageUrl = "https://picsum.photos/200",
                ),
            )
        }
        _uiState.update { it.copy(state = State.Idle) }
//        viewModelScope.launch {
//            _uiState.update { it.copy(state = State.Loading) }
//            try {
//                _articles.update {
//                    if (offset == 0) {
//                        recsysRepository.getLatestSubscribedArticles(count, offset)
//                    } else {
//                        it.subList(0, offset) + recsysRepository.getLatestSubscribedArticles(
//                            count,
//                            offset
//                        )
//                    }
//                }
//                _bookmarks.update { bookmarkRepository.getBookmarkLists() }
//            } catch (e: Exception) {
//                Log.e(
//                    HomeConfig.LOG_TAG,
//                    "Failed to get latest subscribed articles, offset: $offset, count: $count. Error: ${e.message}"
//                )
//            }
//            _uiState.update { it.copy(state = State.Idle) }
//        }
    }
}
