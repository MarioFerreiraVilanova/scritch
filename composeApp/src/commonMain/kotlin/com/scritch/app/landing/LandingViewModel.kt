package com.scritch.app.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scritch.app.auth.AuthenticationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LandingViewModel(
    private val authenticationRepository: AuthenticationRepository,
): ViewModel() {

    fun onSkip(){
        viewModelScope.launch {
            authenticationRepository.login()
        }
    }

    fun onContinue(){
        viewModelScope.launch {
            authenticationRepository.login()
        }
    }
}