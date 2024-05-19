package com.example.frontend.ui.screens.reader

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.datasource.SettingDataSource
import com.example.frontend.data.model.Article
import com.example.frontend.data.model.ArticleContent
import com.example.frontend.data.model.ArticleMetadata
import com.example.frontend.data.model.BookmarkList
import com.example.frontend.data.model.Publisher
import com.example.frontend.data.repository.ArticleRepository
import com.example.frontend.data.repository.BookmarkRepository
import com.example.frontend.data.repository.RecsysRepository
import com.example.frontend.data.repository.SubscriptionRepository
import com.example.frontend.navigation.Route
import com.example.frontend.ui.screens.home.HomeConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.util.Date
import javax.inject.Inject

object ReaderConfig {
    const val LOG_TAG = "ReaderViewModel"
    const val RELATED_ARTICLE_COUNT = 5
}

data class ReaderUiState(
    val article: Article,
    val relatedArticles: List<ArticleMetadata>,
    val bookmarks: List<BookmarkList>,
    val state: State,
) {
    companion object {
        val empty = ReaderUiState(
            article = dummyArticle(),
            relatedArticles = emptyList(),
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
class ReaderViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val recsysRepository: RecsysRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val settingDataSource: SettingDataSource,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val articleId = BigInteger(savedStateHandle.get<String>(Route.Reader.args[0])!!)

    private var _bookmarks = MutableStateFlow(emptyList<BookmarkList>())
    private var _article = MutableStateFlow(dummyArticle())
    private var _relatedArticles = MutableStateFlow(emptyList<ArticleMetadata>())
    private val _uiState = MutableStateFlow(ReaderUiState.empty)

    val uiState = _uiState.combine(_bookmarks) { uiState, bookmarks ->
        uiState.copy(
            bookmarks = bookmarks.sortedBy { it.name }
        )
    }.combine(_article) { uiState, article ->
        uiState.copy(
            article = article
        )
    }.combine(_relatedArticles) { uiState, articles ->
        uiState.copy(
            relatedArticles = articles.sortedBy { it.date }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ReaderUiState.empty,
    )

    fun onBookmarkRelatedArticle(articleId: BigInteger, bookmarkId: BigInteger) {
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

    fun onUnbookmarkRelatedArticle(articleId: BigInteger, bookmarkId: BigInteger) {
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

    fun onSubscribePublisher() {
        val publisherId = _article.value.metadata.publisher.id
        if (publisherId == BigInteger.ZERO) {
            return
        }
        viewModelScope.launch {
            try {
                val userId = settingDataSource.getUserId().first().toBigInteger()
                subscriptionRepository.subscribePublisher(userId, publisherId)
                _article.update { article ->
                    article.copy(
                        metadata = article.metadata.copy(
                            publisher = article.metadata.publisher.copy(
                                isSubscribed = true
                            )
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e(HomeConfig.LOG_TAG, "Failed to subscribe publisher")
            }
        }
    }

    fun onUnsubscribePublisher() {
        val publisherId = _article.value.metadata.publisher.id
        if (publisherId == BigInteger.ZERO) {
            return
        }
        viewModelScope.launch {
            try {
                val userId = settingDataSource.getUserId().first().toBigInteger()
                subscriptionRepository.unsubscribePublisher(userId, publisherId)
                _article.update { article ->
                    article.copy(
                        metadata = article.metadata.copy(
                            publisher = article.metadata.publisher.copy(
                                isSubscribed = false
                            )
                        )
                    )
                }
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
        _relatedArticles.update { articleList ->
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

    init {
        refreshUiState()
    }

    fun refreshUiState(offset: Int = 0, count: Int = ReaderConfig.RELATED_ARTICLE_COUNT) {
        viewModelScope.launch {
            _uiState.update { it.copy(state = State.Loading) }
            try {
                _article.update {
                    articleRepository.getArticleMetadataAndContentById(articleId)
                }
                _bookmarks.update { bookmarkRepository.getBookmarkLists() }
                _relatedArticles.update {
                    it
                    if (offset == 0) {
                        recsysRepository.getRelatedArticles(articleId, count, offset)
                    } else {
                        it.subList(0, offset) + recsysRepository.getRelatedArticles(
                            articleId,
                            count,
                            offset
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(
                    ReaderConfig.LOG_TAG,
                    "Failed to refresh ui state. Error: ${e.message}"
                )
            }
            _uiState.update { it.copy(state = State.Idle) }
        }
    }
}

fun dummyArticle(): Article {
    return Article(
        metadata = ArticleMetadata(
            id = BigInteger.ZERO,
            title = "",
            url = "",
            date = Date(),
            publisher = Publisher(
                id = BigInteger.ZERO,
                name = "",
                url = "",
                avatarUrl = null,
                isSubscribed = false
            ),
            isBookmarked = false,
            imageUrl = null,
        ),
        content = ArticleContent(
            id = BigInteger.ZERO,
            content = "",
        ),
        summary = ""
    )
}