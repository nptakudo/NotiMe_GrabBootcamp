package com.example.frontend.ui.screens.article_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.model.ArticleMetadata
import com.example.frontend.data.model.BookmarkList
import com.example.frontend.data.model.Publisher
import com.example.frontend.data.repository.BookmarkRepository
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
//    private val articleListRepository: ArticleListRepository
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
                val bookmarkId = bookmarkRepository.createBookmarkList(name)
                bookmarkRepository.addToBookmarkList(articleId, bookmarkId)
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
                    if (_bookmarks.value.any { bookmarkList -> bookmarkList.articles.any { it.id == article.id } }) {
                        article.copy(isBookmarked = true)
                    } else {
                        article.copy(isBookmarked = false)
                    }
            }
        }
    }

    fun onLoadArticlesByPublisher(publisherId: BigInteger) {
        _uiState.update { it.copy(state = State.Loading) }
        _articles.update {
            listOf(
                ArticleMetadata(
                    id = BigInteger.ONE,
                    title = "Ukraine's President Zelensky to BBC: Blood money being paid for Russian oil",
                    url = "",
                    date = Date(),
                    publisher = Publisher(
                        id = BigInteger.ONE,
                        name = "BBC News",
                        url = "",
                        avatarUrl = "https://picsum.photos/200",
                        isSubscribed = false
                    ),
                    isBookmarked = true,
                    imageUrl = "https://picsum.photos/400",
                ),
                ArticleMetadata(
                    id = BigInteger.ONE,
                    title = "Ukraine's President Zelensky to BBC: Blood money being paid for Russian oil",
                    url = "",
                    date = Date(),
                    publisher = Publisher(
                        id = BigInteger.ONE,
                        name = "BBC News",
                        url = "",
                        avatarUrl = "https://picsum.photos/200",
                        isSubscribed = false
                    ),
                    isBookmarked = true,
                    imageUrl = "https://picsum.photos/400",
                ),
                ArticleMetadata(
                    id = BigInteger.ONE,
                    title = "Ukraine's President Zelensky to BBC: Blood money being paid for Russian oil",
                    url = "",
                    date = Date(),
                    publisher = Publisher(
                        id = BigInteger.ONE,
                        name = "BBC News",
                        url = "",
                        avatarUrl = "https://picsum.photos/200",
                        isSubscribed = false
                    ),
                    isBookmarked = true,
                    imageUrl = "https://picsum.photos/400",
                ),
                ArticleMetadata(
                    id = BigInteger.ONE,
                    title = "Ukraine's President Zelensky to BBC: Blood money being paid for Russian oil",
                    url = "",
                    date = Date(),
                    publisher = Publisher(
                        id = BigInteger.ONE,
                        name = "BBC News",
                        url = "",
                        avatarUrl = "https://picsum.photos/200",
                        isSubscribed = false
                    ),
                    isBookmarked = true,
                    imageUrl = "https://picsum.photos/400",
                ),
                ArticleMetadata(
                    id = BigInteger.ONE,
                    title = "Ukraine's President Zelensky to BBC: Blood money being paid for Russian oil",
                    url = "",
                    date = Date(),
                    publisher = Publisher(
                        id = BigInteger.ONE,
                        name = "BBC News",
                        url = "",
                        avatarUrl = "https://picsum.photos/200",
                        isSubscribed = false
                    ),
                    isBookmarked = true,
                    imageUrl = "https://picsum.photos/400",
                ),
                ArticleMetadata(
                    id = BigInteger.ONE,
                    title = "Ukraine's President Zelensky to BBC: Blood money being paid for Russian oil",
                    url = "",
                    date = Date(),
                    publisher = Publisher(
                        id = BigInteger.ONE,
                        name = "BBC News",
                        url = "",
                        avatarUrl = "https://picsum.photos/200",
                        isSubscribed = false
                    ),
                    isBookmarked = true,
                    imageUrl = "https://picsum.photos/400",
                ),
                ArticleMetadata(
                    id = BigInteger.ONE,
                    title = "Ukraine's President Zelensky to BBC: Blood money being paid for Russian oil",
                    url = "",
                    date = Date(),
                    publisher = Publisher(
                        id = BigInteger.ONE,
                        name = "BBC News",
                        url = "",
                        avatarUrl = "https://picsum.photos/200",
                        isSubscribed = false
                    ),
                    isBookmarked = true,
                    imageUrl = "https://picsum.photos/400",
                ),
                ArticleMetadata(
                    id = BigInteger.ONE,
                    title = "Ukraine's President Zelensky to BBC: Blood money being paid for Russian oil",
                    url = "",
                    date = Date(),
                    publisher = Publisher(
                        id = BigInteger.ONE,
                        name = "BBC News",
                        url = "",
                        avatarUrl = "https://picsum.photos/200",
                        isSubscribed = false
                    ),
                    isBookmarked = true,
                    imageUrl = "https://picsum.photos/400",
                ),
            )
        }
        _uiState.update { it.copy(state = State.Idle) }
        //viewModelScope.launch {
        //    try {
        //        val articles = articleListRepository.getArticlesByPublisher(publisher)
        //        _articles.update { articles }
        //    } catch (e: Exception) {
        //        Log.e(ArticleListConfig.LOG_TAG, "Failed to load articles by publisher")
        //    }
        //}
    }

    fun onLoadArticlesByBookmarkList(bookmarkListId: BigInteger) {
        _uiState.update { it.copy(state = State.Loading) }
        _articles.update {
            listOf(
                ArticleMetadata(
                    id = BigInteger.ONE,
                    title = "Ukraine's President Zelensky to BBC: Blood money being paid for Russian oil",
                    url = "",
                    date = Date(),
                    publisher = Publisher(
                        id = BigInteger.ONE,
                        name = "BBC News",
                        url = "",
                        avatarUrl = "https://picsum.photos/200",
                        isSubscribed = false
                    ),
                    isBookmarked = true,
                    imageUrl = "https://picsum.photos/400",
                ),
                ArticleMetadata(
                    id = BigInteger.ONE,
                    title = "Ukraine's President Zelensky to BBC: Blood money being paid for Russian oil",
                    url = "",
                    date = Date(),
                    publisher = Publisher(
                        id = BigInteger.ONE,
                        name = "BBC News",
                        url = "",
                        avatarUrl = "https://picsum.photos/200",
                        isSubscribed = false
                    ),
                    isBookmarked = true,
                    imageUrl = "https://picsum.photos/400",
                ),
                ArticleMetadata(
                    id = BigInteger.ONE,
                    title = "Ukraine's President Zelensky to BBC: Blood money being paid for Russian oil",
                    url = "",
                    date = Date(),
                    publisher = Publisher(
                        id = BigInteger.ONE,
                        name = "BBC News",
                        url = "",
                        avatarUrl = "https://picsum.photos/200",
                        isSubscribed = false
                    ),
                    isBookmarked = true,
                    imageUrl = "https://picsum.photos/400",
                ),
                ArticleMetadata(
                    id = BigInteger.ONE,
                    title = "Ukraine's President Zelensky to BBC: Blood money being paid for Russian oil",
                    url = "",
                    date = Date(),
                    publisher = Publisher(
                        id = BigInteger.ONE,
                        name = "BBC News",
                        url = "",
                        avatarUrl = "https://picsum.photos/200",
                        isSubscribed = false
                    ),
                    isBookmarked = true,
                    imageUrl = "https://picsum.photos/400",
                ),
                ArticleMetadata(
                    id = BigInteger.ONE,
                    title = "Ukraine's President Zelensky to BBC: Blood money being paid for Russian oil",
                    url = "",
                    date = Date(),
                    publisher = Publisher(
                        id = BigInteger.ONE,
                        name = "BBC News",
                        url = "",
                        avatarUrl = "https://picsum.photos/200",
                        isSubscribed = false
                    ),
                    isBookmarked = true,
                    imageUrl = "https://picsum.photos/400",
                ),
                ArticleMetadata(
                    id = BigInteger.ONE,
                    title = "Ukraine's President Zelensky to BBC: Blood money being paid for Russian oil",
                    url = "",
                    date = Date(),
                    publisher = Publisher(
                        id = BigInteger.ONE,
                        name = "BBC News",
                        url = "",
                        avatarUrl = "https://picsum.photos/200",
                        isSubscribed = false
                    ),
                    isBookmarked = true,
                    imageUrl = "https://picsum.photos/400",
                ),
                ArticleMetadata(
                    id = BigInteger.ONE,
                    title = "Ukraine's President Zelensky to BBC: Blood money being paid for Russian oil",
                    url = "",
                    date = Date(),
                    publisher = Publisher(
                        id = BigInteger.ONE,
                        name = "BBC News",
                        url = "",
                        avatarUrl = "https://picsum.photos/200",
                        isSubscribed = false
                    ),
                    isBookmarked = true,
                    imageUrl = "https://picsum.photos/400",
                ),
                ArticleMetadata(
                    id = BigInteger.ONE,
                    title = "Ukraine's President Zelensky to BBC: Blood money being paid for Russian oil",
                    url = "",
                    date = Date(),
                    publisher = Publisher(
                        id = BigInteger.ONE,
                        name = "BBC News",
                        url = "",
                        avatarUrl = "https://picsum.photos/200",
                        isSubscribed = false
                    ),
                    isBookmarked = true,
                    imageUrl = "https://picsum.photos/400",
                ),
            )
        }
        _uiState.update { it.copy(state = State.Idle) }
        //viewModelScope.launch {
        //    try {
        //        val articles = articleListRepository.getArticlesByBookmarkList(bookmarkListId)
        //        _articles.update { articles }
        //    } catch (e: Exception) {
        //        Log.e(ArticleListConfig.LOG_TAG, "Failed to load articles by bookmark list")
        //    }
        //}
    }
}