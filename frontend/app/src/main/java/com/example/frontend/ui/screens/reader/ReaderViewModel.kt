package com.example.frontend.ui.screens.reader

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.model.Article
import com.example.frontend.data.model.ArticleContent
import com.example.frontend.data.model.ArticleMetadata
import com.example.frontend.data.model.BookmarkList
import com.example.frontend.data.model.Publisher
import com.example.frontend.data.repository.ArticleRepository
import com.example.frontend.data.repository.BookmarkRepository
import com.example.frontend.data.repository.RecsysRepository
import com.example.frontend.data.repository.SubscriptionRepository
import com.example.frontend.ui.screens.home.HomeConfig
import com.example.frontend.ui.screens.home.State
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
//    val articleId: BigInteger,
    private val articleRepository: ArticleRepository,
    private val recsysRepository: RecsysRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {
    // TODO
    val articleId = BigInteger.ONE
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
            relatedArticles = articles
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
                _article.update { article ->
                    article.copy(
                        metadata = article.metadata.copy(isBookmarked = true)
                    )
                }
            } catch (e: Exception) {
                Log.e(HomeConfig.LOG_TAG, "Failed to bookmark article")
            }
        }
    }

    fun onUnbookmarkRelatedArticle(articleId: BigInteger, bookmarkId: BigInteger) {
        viewModelScope.launch {
            try {
                bookmarkRepository.unbookmarkArticle(articleId, bookmarkId)
                _article.update { article ->
                    article.copy(
                        metadata = article.metadata.copy(isBookmarked = false)
                    )
                }
            } catch (e: Exception) {
                Log.e(HomeConfig.LOG_TAG, "Failed to unbookmark article")
            }
        }
    }

    fun onBookmarkArticle(bookmarkId: BigInteger) {
        viewModelScope.launch {
            try {
                bookmarkRepository.bookmarkArticle(articleId, bookmarkId)
                _article.update { article ->
                    article.copy(
                        metadata = article.metadata.copy(isBookmarked = true)
                    )
                }
            } catch (e: Exception) {
                Log.e(HomeConfig.LOG_TAG, "Failed to bookmark article")
            }
        }
    }

    fun onUnbookmarkArticle(bookmarkId: BigInteger) {
        viewModelScope.launch {
            try {
                bookmarkRepository.unbookmarkArticle(articleId, bookmarkId)
                _article.update { article ->
                    article.copy(
                        metadata = article.metadata.copy(isBookmarked = false)
                    )
                }
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
                subscriptionRepository.subscribePublisher(publisherId)
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
                subscriptionRepository.unsubscribePublisher(publisherId)
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

    init {
        refreshUiState()
    }

    fun refreshUiState(offset: Int = 0, count: Int = ReaderConfig.RELATED_ARTICLE_COUNT) {
        viewModelScope.launch {
            // TODO
            val title =
                "Ukraine's President Zelensky to BBC: Blood money being paid for Russian oil"
            val summary =
                "Ukrainian President Volodymyr Zelensky has accused European countries that continue to buy Russian oil of \"earning their money in other people's blood\"."
            val content =
                "## First\n### Second\nUkrainian President Volodymyr Zelensky has accused European countries that continue to buy Russian oil of \"earning their money in other people's blood\".\n\n" +
                        "In an interview with the BBC, President Zelensky singled out Germany and Hungary, accusing them of blocking efforts to embargo energy sales, from which Russia stands to make up to £250bn (\$326bn) this year.\n\n" +
                        "There has been a growing frustration among Ukraine's leadership with Berlin, which has backed some sanctions against Russia but so far resisted calls to back tougher action on oil sales.\n\n" +
                        "Ukrainian President Volodymyr Zelensky has accused European countries that continue to buy Russian oil of \"earning their money in other people's blood\".\n\n" +
                        "In an interview with the BBC, President Zelensky singled out Germany and Hungary, accusing them of blocking efforts to embargo energy sales, from which Russia stands to make up to £250bn (\$326bn) this year.\n\n" +
                        "There has been a growing frustration among Ukraine's leadership with Berlin, which has backed some sanctions against Russia but so far resisted calls to back tougher action on oil sales.\n\n" +
                        "Ukrainian President Volodymyr Zelensky has accused European countries that continue to buy Russian oil of \"earning their money in other people's blood\".\n\n" +
                        "In an interview with the BBC, President Zelensky singled out Germany and Hungary, accusing them of blocking efforts to embargo energy sales, from which Russia stands to make up to £250bn (\$326bn) this year.\n\n" +
                        "There has been a growing frustration among Ukraine's leadership with Berlin, which has backed some sanctions against Russia but so far resisted calls to back tougher action on oil sales."
            val publisherName = "BBC News"
            val publisherAvatarUrl = "https://picsum.photos/200"
            val articleImageUrl = "https://picsum.photos/400"
            _article.update {
                Article(
                    metadata = ArticleMetadata(
                        id = BigInteger.ONE,
                        title = title,
                        url = "",
                        date = Date(),
                        publisher = Publisher(
                            id = BigInteger.ONE,
                            name = publisherName,
                            url = "",
                            avatarUrl = publisherAvatarUrl,
                            isSubscribed = true
                        ),
                        isBookmarked = true,
                        articleImageUrl = articleImageUrl,
                    ),
                    content = ArticleContent(
                        id = BigInteger.ONE,
                        content = content,
                        imageUrl = articleImageUrl
                    ),
                    summary = summary
                )
            }
            _relatedArticles.update {
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
                    ),
                )
            }
//            _uiState.update { it.copy(state = State.Loading) }
//            try {
//                _article.update {
//                    articleRepository.getArticleMetadataAndContentById(articleId)
//                }
//                _bookmarks.update { bookmarkRepository.getBookmarkLists() }
//                _relatedArticles.update {
//                    it
//                    if (offset == 0) {
//                        recsysRepository.getRelatedArticles(articleId, count, offset)
//                    } else {
//                        it.subList(0, offset) + recsysRepository.getRelatedArticles(
//                            articleId,
//                            count,
//                            offset
//                        )
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e(
//                    ReaderConfig.LOG_TAG,
//                    "Failed to refresh ui state.Error: ${e.message}"
//                )
//            }
//            _uiState.update { it.copy(state = State.Idle) }
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
            articleImageUrl = null,
        ),
        content = ArticleContent(
            id = BigInteger.ZERO,
            content = "",
            imageUrl = ""
        ),
        summary = ""
    )
}