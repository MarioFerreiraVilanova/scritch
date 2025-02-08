package com.scritch.app.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scritch.app.auth.AuthenticationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
    private val authenticationRepository: AuthenticationRepository,
) : ViewModel() {

    private val _appViewState = MutableStateFlow(
        authenticationRepository.user()?.let { user ->
            AppViewState.Authenticated(user = user)
        } ?: AppViewState.Unauthenticated
    )
    val appViewState = _appViewState.asStateFlow()

    init {
        viewModelScope.launch {
            authenticationRepository.userFlow().collectLatest { user ->
                _appViewState.update {
                    when (user) {
                        null -> AppViewState.Unauthenticated
                        else -> AppViewState.Authenticated(user = user)
                    }
                }
            }
        }
    }
}