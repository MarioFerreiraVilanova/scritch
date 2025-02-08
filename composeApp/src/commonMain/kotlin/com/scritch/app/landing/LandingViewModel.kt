package com.scritch.app.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scritch.app.auth.AuthenticationRepository
import kotlinx.coroutines.launch

class LandingViewModel(
    private val authenticationRepository: AuthenticationRepository,
): ViewModel() {
    fun onLogIn(){
        viewModelScope.launch {
            authenticationRepository.login()
        }
    }
}