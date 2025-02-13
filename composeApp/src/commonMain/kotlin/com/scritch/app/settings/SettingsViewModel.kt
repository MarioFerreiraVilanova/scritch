package com.scritch.app.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scritch.app.auth.AuthenticationRepository
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authenticationRepository: AuthenticationRepository,
) : ViewModel() {

    fun onLogout() {
        viewModelScope.launch {
            authenticationRepository.logout()
        }
    }
}