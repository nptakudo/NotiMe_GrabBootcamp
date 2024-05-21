package com.example.frontend.ui.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.model.ArticleMetadata
import com.example.frontend.data.model.Publisher
import com.example.frontend.data.repository.PublisherRepository
import com.example.frontend.data.repository.SubscriptionRepository
import com.example.frontend.ui.screens.subscription.SubscriptionConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigInteger
import javax.inject.Inject

object SearchResultConfig {
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
    private val subscriptionRepository: SubscriptionRepository,
    private val publisherRepository: PublisherRepository
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

    fun onSubscribePublisher(publisherId: BigInteger) {
        viewModelScope.launch {
            try {
                subscriptionRepository.subscribePublisher(publisherId)
            } catch (e: Exception) {
                Log.e(SubscriptionConfig.LOG_TAG, "Failed to subscribe publisher")
            }
        }
    }

    fun onUnsubscribePublisher(publisherId: BigInteger) {
        viewModelScope.launch {
            try {
                subscriptionRepository.unsubscribePublisher(publisherId)
            } catch (e: Exception) {
                Log.e(SubscriptionConfig.LOG_TAG, "Failed to unsubscribe publisher")
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(state = State.Loading) }
            try {
                val searchResponse = subscriptionRepository.searchPublishers(query)
                Log.i(SearchResultConfig.LOG_TAG, searchResponse.toString())
                _subscriptions.update { searchResponse.publishers }
                _articles.update { searchResponse.articles }
                _isNewSource.update { searchResponse.isExisting }
            } catch (e: Exception) {
                Log.e(SearchResultConfig.LOG_TAG, "Failed to search publishers")
            }
            _uiState.update { it.copy(state = State.Idle) }
        }
    }

    fun onAddSource (publisher: Publisher) {
        viewModelScope.launch {
            try {
                publisherRepository.addNewSource(publisher)
            } catch (e: Exception) {
                Log.e(SearchResultConfig.LOG_TAG, "Failed to add new source")
            }
        }
    }

}