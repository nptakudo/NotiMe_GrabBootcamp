package com.example.frontend.ui.screens.article_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.model.ArticleMetadata
import com.example.frontend.data.model.BookmarkList
import com.example.frontend.data.repository.ArticleRepository
import com.example.frontend.data.repository.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigInteger
import javax.inject.Inject

object ArticleListConfig {
    const val LOG_TAG = "SubscribedListViewModel"
}

enum class State {
    Idle,
    Loading,
}

data class ArticleListUiState(
    val articles: List<ArticleMetadata> = emptyList(),
    val bookmarks: List<BookmarkList> = emptyList(),
    val state: State
) {
    companion object {
        val empty = ArticleListUiState(
            articles = emptyList(),
            state = State.Idle
        )
    }
}

// TODO: Remember to update bookmarks when refresh!!
@HiltViewModel
class ArticleListViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
    private val articleRepository: ArticleRepository
) : ViewModel() {
    private var _articles = MutableStateFlow(emptyList<ArticleMetadata>())
    private var _bookmarks = MutableStateFlow(emptyList<BookmarkList>())
    private var _uiState = MutableStateFlow(ArticleListUiState.empty)

    val uiState = _uiState
        .combine(_articles) { uiState, articles ->
            uiState.copy(
                articles = articles
            )
        }.combine(_bookmarks) { uiState, bookmarks ->
            uiState.copy(
                bookmarks = bookmarks.sortedBy { it.name }
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ArticleListUiState.empty
        )

    fun onBookmarkArticle(articleId: BigInteger, bookmarkId: BigInteger) {
        viewModelScope.launch {
            try {
                bookmarkRepository.bookmarkArticle(articleId, bookmarkId)
                _bookmarks.update { bookmarkRepository.getBookmarkLists() }
                updateBookmarkedState(articleId)
            } catch (e: Exception) {
                Log.e(ArticleListConfig.LOG_TAG, "Failed to bookmark article")
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
                Log.e(ArticleListConfig.LOG_TAG, "Failed to unbookmark article")
            }
        }
    }

    fun onCreateNewBookmark(name: String, articleId: BigInteger) {
        viewModelScope.launch {
            try {
                val bookmark = bookmarkRepository.createBookmarkList(name)
                bookmarkRepository.bookmarkArticle(articleId, bookmark.id)
                _bookmarks.update { bookmarkRepository.getBookmarkLists() }
                updateBookmarkedState(articleId)
            } catch (e: Exception) {
                Log.e(ArticleListConfig.LOG_TAG, "Failed to create new bookmark")
            }
        }
    }

    private fun updateBookmarkedState(articleId: BigInteger? = null) {
        _articles.update { articleList ->
            articleList.map { article ->
                if (articleId != null && article.id != articleId) {
                    article
                } else
                    Log.i(ArticleListConfig.LOG_TAG, "bookmark updated ${article.id}")
                    if (_bookmarks.value.any { bookmarkList -> bookmarkList.articles?.any { it.id == article.id } == true }) {
                        article.copy(isBookmarked = true)
                    } else {
                        article.copy(isBookmarked = false)
                    }
            }
        }
    }

    fun onLoadArticlesByPublisher(publisherId: BigInteger) {
        viewModelScope.launch {
            _uiState.update { it.copy(state = State.Loading) }
            try {
                val articles = articleRepository.getArticlesByPublisher(publisherId, 20, 0)
                _articles.update { articles }
                _uiState.update { it.copy(state = State.Idle) }
            } catch (e: Exception) {
                Log.e(ArticleListConfig.LOG_TAG, "Failed to load articles by publisher")
            }
            _uiState.update { it.copy(state = State.Idle) }
        }
    }

    fun onLoadArticlesByBookmarkList(bookmarkListId: BigInteger) {
        viewModelScope.launch {
            _uiState.update { it.copy(state = State.Loading) }
            try {
                val bookmarkList = bookmarkRepository.getBookmarkListById(bookmarkListId)
                _articles.update { bookmarkList.articles ?: emptyList() }
                _uiState.update { it.copy(state = State.Idle) }
            } catch (e: Exception) {
                Log.e(ArticleListConfig.LOG_TAG, "Failed to load articles by bookmark list")
            }
            _uiState.update { it.copy(state = State.Idle) }
        }
    }

    init {
        viewModelScope.launch {
            _bookmarks.update { bookmarkRepository.getBookmarkLists() }
        }
    }
}