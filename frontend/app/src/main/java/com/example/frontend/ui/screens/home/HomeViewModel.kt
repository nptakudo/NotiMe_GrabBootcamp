package com.example.frontend.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.model.Article
import com.example.frontend.data.repository.CommonRepository
import com.example.frontend.data.repository.RecsysRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigInteger
import javax.inject.Inject

object HomeConfig {
    const val LOG_TAG = "HomeViewModel"
    const val LOAD_COUNT = 20
}

data class HomeUiState(
    val articles: List<Article>,
    val state: State,
) {
    companion object {
        val empty = HomeUiState(
            articles = emptyList(),
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
    private val commonRepository: CommonRepository
) : ViewModel() {
    private var _articles = MutableStateFlow(emptyList<Article>())
    private var _uiState = MutableStateFlow(HomeUiState.empty)

    val uiState = _uiState.combine(_articles) { uiState, articles ->
        uiState.copy(
            articles = articles
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeUiState.empty,
    )

    fun onBookmarkArticle(articleId: BigInteger, bookmarkId: BigInteger) {
        viewModelScope.launch {
            try {
                commonRepository.bookmarkArticle(articleId, bookmarkId)
                _articles.update { articles ->
                    articles.map {
                        if (it.id == articleId) {
                            it.copy(isBookmarked = true)
                        } else {
                            it
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(HomeConfig.LOG_TAG, "Failed to bookmark article")
            }
        }
    }

    fun onUnbookmarkArticle(articleId: BigInteger, bookmarkId: BigInteger) {
        viewModelScope.launch {
            try {
                commonRepository.unbookmarkArticle(articleId, bookmarkId)
                _articles.update { articles ->
                    articles.map {
                        if (it.id == articleId) {
                            it.copy(isBookmarked = false)
                        } else {
                            it
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(HomeConfig.LOG_TAG, "Failed to unbookmark article")
            }
        }
    }

    fun onSubscribePublisher(publisherId: BigInteger) {
        viewModelScope.launch {
            try {
                commonRepository.subscribePublisher(publisherId)
            } catch (e: Exception) {
                Log.e(HomeConfig.LOG_TAG, "Failed to subscribe publisher")
            }
        }
    }

    fun onUnsubscribePublisher(publisherId: BigInteger) {
        viewModelScope.launch {
            try {
                commonRepository.unsubscribePublisher(publisherId)
            } catch (e: Exception) {
                Log.e(HomeConfig.LOG_TAG, "Failed to unsubscribe publisher")
            }
        }
    }

    init {
        refreshUiState()
    }

    fun refreshUiState(offset: Int = 0, count: Int = HomeConfig.LOAD_COUNT) {
        viewModelScope.launch {
            _uiState.update { it.copy(state = State.Loading) }
            try {
                _articles.update {
                    if (offset == 0) {
                        recsysRepository.getLatestSubscribedArticles(HomeConfig.LOAD_COUNT, offset)
                    } else {
                        it.subList(0, offset) + recsysRepository.getLatestSubscribedArticles(count, offset)
                    }
                }
            } catch (e: Exception) {
                Log.e(HomeConfig.LOG_TAG, "Failed to get latest subscribed articles, offset: $offset, count: $count")
            }
            _uiState.update { it.copy(state = State.Idle) }
        }
    }
}
