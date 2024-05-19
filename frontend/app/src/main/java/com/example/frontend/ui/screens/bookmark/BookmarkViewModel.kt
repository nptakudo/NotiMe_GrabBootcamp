package com.example.frontend.ui.screens.bookmark

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
import java.math.BigInteger
import java.util.Date
import javax.inject.Inject

object BookmarkConfig {
    const val LOG_TAG = "BookmarkViewModel"
}

enum class State {
    Idle,
    Loading,
}

data class BookmarkUiState(
    val bookmarks: List<BookmarkList> = emptyList(),
    val state: State
) {
    companion object {
        val empty = BookmarkUiState(
            bookmarks = emptyList(),
            state = State.Idle
        )
    }
}

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {
    private var _bookmarks = MutableStateFlow(emptyList<BookmarkList>())
    private var _uiState = MutableStateFlow(BookmarkUiState.empty)

    val uiState = _uiState
        .combine(_bookmarks) { uiState, bookmarks ->
            uiState.copy(
                bookmarks = bookmarks
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            BookmarkUiState.empty
        )

    fun onLoadBookmark() {
        _uiState.update { it.copy(state = State.Loading) }
        _bookmarks.update {
            listOf(
                BookmarkList(
                    id = BigInteger.ONE,
                    name = "Article 1",
                    ownerId = BigInteger.ONE,
                    isSaved = true,
                    articles = listOf(
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
                ),
                BookmarkList(
                    id = BigInteger.ONE,
                    name = "Article 2",
                    ownerId = BigInteger.ONE,
                    isSaved = false,
                    articles = emptyList()
                ),
                BookmarkList(
                    id = BigInteger.ONE,
                    name = "Article 3",
                    ownerId = BigInteger.ONE,
                    isSaved = false,
                    articles = emptyList()
                ),
            )
        }
        _uiState.update { it.copy(state = State.Idle) }
//        viewModelScope.launch {
//            try {
//                bookmarkRepository.bookmarkArticle(articleId)
//            } catch (e: Exception) {
//                Log.e(BookmarkConfig.LOG_TAG, "Failed to bookmark article")
//            }
//        }
    }

    fun onDeleteBookmark(articleId: BigInteger) {

    }

    fun onShareBoookmark(articleId: BigInteger) {

    }
}
