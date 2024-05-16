package com.example.frontend.ui.screens.search

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.model.Article
import com.example.frontend.data.model.ArticleMetadata
import com.example.frontend.data.model.Publisher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.util.Date
import javax.inject.Inject

object SearchRessultConfig {
    const val LOG_TAG = "SearchResultViewModel"
}

enum class State {
    Idle,
    Loading,
}

data class SearchResultUiState(
    val subscriptions: List<Publisher> = emptyList(),
    val articles: List<ArticleMetadata> = emptyList(),
    val isNewSource: Boolean = false,
    val state: State
) {
    companion object {
        val empty = SearchResultUiState(
            subscriptions = emptyList(),
            articles = emptyList(),
            state = State.Idle
        )
    }
}

@HiltViewModel
class SearchResultViewModel @Inject constructor(

) : ViewModel() {
    private val _articles = MutableStateFlow(emptyList<ArticleMetadata>())
    private val _subscriptions = MutableStateFlow(emptyList<Publisher>())
    private val _isNewSource = MutableStateFlow(false)
    private val _uiState = MutableStateFlow(SearchResultUiState.empty)

    val uiState = _uiState
        .combine(_articles) { uiState, articles ->
            uiState.copy(
                articles = articles.sortedByDescending { it.date }
            )
        }
        .combine(_subscriptions) { uiState, subscriptions ->
            uiState.copy(
                subscriptions = subscriptions
            )
        }
        .combine(_isNewSource) { uiState, isNewSource ->
            uiState.copy(
                isNewSource = isNewSource
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SearchResultUiState.empty
        )
    fun search(query: String) {
        _uiState.update { it.copy(state = State.Loading) }
        _isNewSource.update { true }
//        _subscriptions.update {
//            listOf(
//                Publisher(
//                    id = BigInteger.valueOf(1),
//                    name = "Publisher 1",
//                    url = "Publisher 1 description",
//                    avatarUrl = "https://findingtom.com/images/uploads/medium-logo/article-image-00.jpeg",
//                    isSubscribed = true
//                ),
//                Publisher(
//                    id = BigInteger.valueOf(2),
//                    name = "Publisher 2",
//                    url = "Publisher 2 description",
//                    avatarUrl = "https://via.placeholder.com/150",
//                    isSubscribed = false
//                ),
//                Publisher(
//                    id = BigInteger.valueOf(3),
//                    name = "Publisher 3",
//                    url = "Publisher 3 description",
//                    avatarUrl = "https://via.placeholder.com/150",
//                    isSubscribed = false
//                ),
//                Publisher(
//                    id = BigInteger.valueOf(4),
//                    name = "Publisher 4",
//                    url = "Publisher 4 description",
//                    avatarUrl = "https://via.placeholder.com/150",
//                    isSubscribed = true
//                ),
//                Publisher(
//                    id = BigInteger.valueOf(5),
//                    name = "Publisher 5",
//                    url = "Publisher 5 description",
//                    avatarUrl = "https://via.placeholder.com/150",
//                    isSubscribed = false
//                ),
//                Publisher(
//                    id = BigInteger.valueOf(6),
//                    name = "Publisher 6",
//                    url = "Publisher 6 description",
//                    avatarUrl = "https://via.placeholder.com/150",
//                    isSubscribed = true
//                ),
//                Publisher(
//                    id = BigInteger.valueOf(7),
//                    name = "Publisher 7",
//                    url = "Publisher 7 description",
//                    avatarUrl = "https://via.placeholder.com/150",
//                    isSubscribed = false
//                ),
//                Publisher(
//                    id = BigInteger.valueOf(8),
//                    name = "Publisher 8",
//                    url = "Publisher 8 description",
//                    avatarUrl = "https://via.placeholder.com/150",
//                    isSubscribed = false
//                ),
//                Publisher(
//                    id = BigInteger.valueOf(9),
//                    name = "Publisher 9",
//                    url = "Publisher 9 description",
//                    avatarUrl = "https://via.placeholder.com/150",
//                    isSubscribed = false
//                ),
//                Publisher(
//                    id = BigInteger.valueOf(10),
//                    name = "Publisher 10",
//                    url = "Publisher 10 description",
//                    avatarUrl = "https://via.placeholder.com/150",
//                    isSubscribed = true
//                )
//            )
//        }
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
                    articleImageUrl = "https://picsum.photos/400",
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
                    articleImageUrl = "https://picsum.photos/400",
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
                    articleImageUrl = "https://picsum.photos/400",
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
                    articleImageUrl = "https://picsum.photos/400",
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
                    articleImageUrl = "https://picsum.photos/400",
                ),ArticleMetadata(
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
                    articleImageUrl = "https://picsum.photos/400",
                ),ArticleMetadata(
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
                    articleImageUrl = "https://picsum.photos/400",
                ),ArticleMetadata(
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
                    articleImageUrl = "https://picsum.photos/400",
                ),
            )
        }
        _uiState.update { it.copy(state = State.Idle) }
//        viewModelScope.launch {
//
//        }
    }

    // TODO: Implement the following functions
    // follow - unfollow
    // add new source

}