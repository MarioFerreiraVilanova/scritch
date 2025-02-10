package com.scritch.app.splash

import androidx.lifecycle.ViewModel
import com.scritch.app.auth.AuthenticationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SplashViewModel(
    authenticationRepository: AuthenticationRepository,
): ViewModel() {

    private val mutableViewState = MutableStateFlow(SplashViewState.Loading)
    val viewState = mutableViewState.asStateFlow()

    init {
        authenticationRepository.user()?.let {
            mutableViewState.update { SplashViewState.ToHome }
        } ?: mutableViewState.update { SplashViewState.ToLanding }
    }
}