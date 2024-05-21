package com.example.frontend.ui.screens.reader

import android.util.Log
import androidx.lifecycle.SavedStateHandle
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
import com.example.frontend.navigation.Route
import com.example.frontend.ui.screens.home.HomeConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.inject.Inject

object NewReaderConfig {
    const val LOG_TAG = "ReaderViewModel"
}

data class NewReaderUiState(
    val article: Article,
    val state: State,
) {
    companion object {
        val empty = NewReaderUiState(
            article = dummyArticle(),
            state = State.Idle
        )
    }
}

@HiltViewModel
class NewReaderViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val url = savedStateHandle.get<String>(Route.Reader.args[0])!!
    private val articleUrl = URLDecoder.decode(url, StandardCharsets.UTF_8.toString())

    private var _article = MutableStateFlow(dummyArticle())
    private val _uiState = MutableStateFlow(NewReaderUiState.empty)

    val uiState = _uiState.combine(_article) { uiState, article ->
        uiState.copy(
            article = article
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        NewReaderUiState.empty,
    )
    init {
        Log.i(NewReaderConfig.LOG_TAG, "NewReaderViewModel: $articleUrl")
        refreshUiState()
    }

    fun refreshUiState() {
        viewModelScope.launch {
            _uiState.update { it.copy(state = State.Loading) }
            try {
                _article.update {
                    articleRepository.getNewArticle(articleUrl)
                }
            } catch (e: Exception) {
                Log.e(
                    NewReaderConfig.LOG_TAG,
                    "Failed to refresh ui state. Error: ${e.message}"
                )
            }
            _uiState.update { it.copy(state = State.Idle) }
        }
    }
}
