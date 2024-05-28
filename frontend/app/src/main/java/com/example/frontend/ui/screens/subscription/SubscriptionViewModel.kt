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
    private val subscriptionRepository: SubscriptionRepository,
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
        viewModelScope.launch {
            _uiState.update { it.copy(state = State.Loading) }
            try {
                val sources = subscriptionRepository.getSubscriptions()
                _subscribedSources.update { sources }
            } catch (e: Exception) {
                Log.e(
                    SubscriptionConfig.LOG_TAG,
                    "Failed to load subscribed sources"
                )
            }
            _uiState.update { it.copy(state = State.Idle) }
        }
    }
}