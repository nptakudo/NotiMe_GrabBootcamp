package com.example.frontend.ui.screens.subscription

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.model.Publisher
import com.example.frontend.data.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigInteger
import javax.inject.Inject

object SubscriptionConfig {
    const val LOG_TAG = "SubscribedListViewModel"
    const val PAGE_SIZE = 10
}

enum class State {
    Idle,
    Loading,
}

data class SubscriptionUiState(
    val subscriptions: List<Publisher> = emptyList(),
    val state: State
) {
    companion object {
        val empty = SubscriptionUiState(
            subscriptions = emptyList(),
            state = State.Idle
        )
    }
}

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {
    private var _subscribedSources = MutableStateFlow(emptyList<Publisher>())
    private var _uiState = MutableStateFlow(SubscriptionUiState.empty)

    val uiState = _uiState
        .combine(_subscribedSources) { uiState, subscribedSources ->
            uiState.copy(
                subscriptions = subscribedSources
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SubscriptionUiState.empty
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

    init {
        loadSources()
    }

    fun loadSources() {
        _uiState.update { it.copy(state = State.Loading) }
        _subscribedSources.update {
            listOf(
                Publisher(
                    id = BigInteger.valueOf(1),
                    name = "Publisher 1",
                    url = "Publisher 1 description",
                    avatarUrl = "https://findingtom.com/images/uploads/medium-logo/article-image-00.jpeg",
                    isSubscribed = true
                ),
                Publisher(
                    id = BigInteger.valueOf(2),
                    name = "Publisher 2",
                    url = "Publisher 2 description",
                    avatarUrl = "https://via.placeholder.com/150",
                    isSubscribed = true
                ),
                Publisher(
                    id = BigInteger.valueOf(3),
                    name = "Publisher 3",
                    url = "Publisher 3 description",
                    avatarUrl = "https://via.placeholder.com/150",
                    isSubscribed = true
                ),
                Publisher(
                    id = BigInteger.valueOf(4),
                    name = "Publisher 4",
                    url = "Publisher 4 description",
                    avatarUrl = "https://via.placeholder.com/150",
                    isSubscribed = true
                ),
                Publisher(
                    id = BigInteger.valueOf(5),
                    name = "Publisher 5",
                    url = "Publisher 5 description",
                    avatarUrl = "https://via.placeholder.com/150",
                    isSubscribed = true
                ),
                Publisher(
                    id = BigInteger.valueOf(6),
                    name = "Publisher 6",
                    url = "Publisher 6 description",
                    avatarUrl = "https://via.placeholder.com/150",
                    isSubscribed = true
                ),
                Publisher(
                    id = BigInteger.valueOf(7),
                    name = "Publisher 7",
                    url = "Publisher 7 description",
                    avatarUrl = "https://via.placeholder.com/150",
                    isSubscribed = true
                ),
                Publisher(
                    id = BigInteger.valueOf(8),
                    name = "Publisher 8",
                    url = "Publisher 8 description",
                    avatarUrl = "https://via.placeholder.com/150",
                    isSubscribed = true
                ),
                Publisher(
                    id = BigInteger.valueOf(9),
                    name = "Publisher 9",
                    url = "Publisher 9 description",
                    avatarUrl = "https://via.placeholder.com/150",
                    isSubscribed = true
                ),
                Publisher(
                    id = BigInteger.valueOf(10),
                    name = "Publisher 10",
                    url = "Publisher 10 description",
                    avatarUrl = "https://via.placeholder.com/150",
                    isSubscribed = true
                )
            )
        }
        _uiState.update { it.copy(state = State.Idle) }

        // for fetch if use repo
//        viewModelScope.launch {
//            _uiState.update { it.copy(state = State.Loading) }
//            try {
//                val sources = subscriptionRepository.getSubscriptions()
//                _subscribedSources.update { sources }
//            } catch (e: Exception) {
//                Log.e(
//                    SubscriptionConfig.LOG_TAG,
//                    "Failed to load subscribed sources"
//                )
//            }
//            _uiState.update { it.copy(state = State.Idle) }
//        }
    }
}