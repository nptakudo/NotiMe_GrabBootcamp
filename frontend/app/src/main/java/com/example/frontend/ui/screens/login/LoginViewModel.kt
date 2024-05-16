package com.example.frontend.ui.screens.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

object LoginConfig {
    const val LOG_TAG = "LoginViewModel"
}

enum class State {
    Idle,
    Loading,
}

data class LoginUiState(
    val state: State
) {
    companion object {
        val empty = LoginUiState(
            state = State.Idle
        )
    }
}

@HiltViewModel
class LoginViewModel @Inject constructor(
//    private val loginRepository: LoginRepository
) : ViewModel() {
    private var _uiState = MutableStateFlow(LoginUiState.empty)

    val uiState = _uiState

    fun onLogin(username: String, password: String) {
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(state = State.Loading)
                }
//                loginRepository.login(email, password)
            } catch (e: Exception) {
                Log.e(LoginConfig.LOG_TAG, "Failed to login")
            }
        }
    }
}
