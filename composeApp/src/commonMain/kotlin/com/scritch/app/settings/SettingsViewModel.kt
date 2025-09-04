package com.scritch.app.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scritch.app.auth.AuthenticationRepository
import com.scritch.app.userprofile.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsViewState(
    val nickname: String? = null,
)

class SettingsViewModel(
    private val authenticationRepository: AuthenticationRepository,
    private val userProfileRepository: UserProfileRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow(SettingsViewState())
    val viewState: StateFlow<SettingsViewState> = _viewState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val user = authenticationRepository.user()
            if (user != null) {
                val userProfile = userProfileRepository.userProfile(user.id)
                _viewState.value = _viewState.value.copy(
                    nickname = userProfile?.nickname
                )
            }
        }
    }
}