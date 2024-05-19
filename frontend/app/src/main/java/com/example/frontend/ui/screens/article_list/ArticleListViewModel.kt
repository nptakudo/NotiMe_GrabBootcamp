package com.example.frontend.ui.screens.article_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.model.ArticleMetadata
import com.example.frontend.data.model.Publisher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
    val state: State
) {
    companion object {
        val empty = ArticleListUiState(
            articles = emptyList(),
            state = State.Idle
        )
    }
}
@HiltViewModel
class ArticleListViewModel @Inject constructor(
//    private val articleListRepository: ArticleListRepository
) : ViewModel() {
    private var _articles = MutableStateFlow(emptyList<ArticleMetadata>())
    private var _uiState = MutableStateFlow(ArticleListUiState.empty)

    val uiState = _uiState
        .combine(_articles) { uiState, articles ->
            uiState.copy(
                articles = articles
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ArticleListUiState.empty
        )
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
                    imageUrl = "https://picsum.photos/400",
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
                    imageUrl = "https://picsum.photos/400",
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
                    imageUrl = "https://picsum.photos/400",
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
                    imageUrl = "https://picsum.photos/400",
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