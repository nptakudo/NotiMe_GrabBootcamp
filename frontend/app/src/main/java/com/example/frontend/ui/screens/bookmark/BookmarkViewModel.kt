package com.example.frontend.ui.screens.bookmark

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.datasource.SettingDataSource
import com.example.frontend.data.model.BookmarkList
import com.example.frontend.data.repository.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigInteger
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
    val userId: BigInteger,
    val state: State
) {
    companion object {
        val empty = BookmarkUiState(
            bookmarks = emptyList(),
            userId = BigInteger.ZERO,
            state = State.Idle
        )
    }
}

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
    private val settingDataSource: SettingDataSource
) : ViewModel() {
    private var _bookmarks = MutableStateFlow(emptyList<BookmarkList>())
    private var _uiState = MutableStateFlow(BookmarkUiState.empty)
    private var _userId = MutableStateFlow(BigInteger.ZERO)

    init {
        onLoadBookmark()
        viewModelScope.launch {
            _userId.update { settingDataSource.getUserId().first().toBigInteger() }
        }
    }

    val uiState = _uiState
        .combine(_bookmarks) { uiState, bookmarks ->
            uiState.copy(
                bookmarks = bookmarks
            )
        }
        .combine(_userId) { uiState, userId ->
            uiState.copy(
                userId = userId
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            BookmarkUiState.empty
        )

    fun onCreateNewBookmark(name: String) {
        viewModelScope.launch {
            try {
                val bookmark = bookmarkRepository.createBookmarkList(name)
                _bookmarks.update { it + bookmark }
            } catch (e: Exception) {
                Log.e(BookmarkConfig.LOG_TAG, "Failed to create new bookmark")
            }
        }
    }

    fun onLoadBookmark() {
        viewModelScope.launch {
            _uiState.update { it.copy(state = State.Loading)}
            try {
                _bookmarks.update { bookmarkRepository.getBookmarkLists(isShared = true) }
                _bookmarks.update { bookmarks ->
                    bookmarks.sortedWith(compareByDescending<BookmarkList> { it.isSaved }.thenByDescending { it.ownerId == _userId.value })
                }
            } catch (e: Exception) {
                Log.e(BookmarkConfig.LOG_TAG, "Failed to load bookmarks")
            }
            _uiState.update { it.copy(state = State.Idle) }
        }
    }

    fun onDeleteBookmark(bookmarkId: BigInteger) {
        viewModelScope.launch {
            try {
                bookmarkRepository.deleteBookmarkList(bookmarkId)
                _bookmarks.update { bookmarkRepository.getBookmarkLists() }
            } catch (e: Exception) {
                Log.e(BookmarkConfig.LOG_TAG, "Failed to delete bookmark")
            }
        }
    }

    fun onShareBookmark(bookmarkId: BigInteger) {

    }
}
