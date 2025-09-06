package com.scritch.app.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scritch.app.admin.AdminRepository
import com.scritch.app.auth.AuthenticationRepository
import com.scritch.app.userprofile.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsViewState(
    val nickname: String? = null,
    val isAdmin: Boolean = false,
    val isLoadingAdmin: Boolean = true,
)

class SettingsViewModel(
    private val authenticationRepository: AuthenticationRepository,
    private val userProfileRepository: UserProfileRepository,
    private val adminRepository: AdminRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow(SettingsViewState())
    val viewState: StateFlow<SettingsViewState> = _viewState.asStateFlow()

    init {
        loadUserProfile()
        checkAdminStatus()
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

    private fun checkAdminStatus() {
        viewModelScope.launch {
            val user = authenticationRepository.user()
            if (user != null) {
                val isAdmin = adminRepository.isUserAdmin(user.id)
                _viewState.value = _viewState.value.copy(
                    isAdmin = isAdmin,
                    isLoadingAdmin = false,
                )
            } else {
                _viewState.value = _viewState.value.copy(
                    isAdmin = false,
                    isLoadingAdmin = false,
                )
            }
        }
    }
}