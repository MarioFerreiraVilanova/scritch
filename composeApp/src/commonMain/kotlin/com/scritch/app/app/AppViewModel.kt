package com.scritch.app.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scritch.app.auth.AuthenticationRepository
import com.scritch.app.auth.User
import com.scritch.app.userdata.UserDataRepository
import dev.gitlive.firebase.auth.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
    private val authenticationRepository: AuthenticationRepository,
    private val userDataRepository: UserDataRepository,
) : ViewModel() {

    private val _appViewState = MutableStateFlow<AppViewState>(AppViewState.StatingApp)
    val appViewState = _appViewState.asStateFlow()

    init {
        viewModelScope.launch {
            authenticationRepository.userFlow().collectLatest { user ->
                onUserChanged(user)
            }
        }
    }

    private suspend fun onUserChanged(user: User?){
        user?.id?.let { userId ->
            val userData = userDataRepository.userData(userId)
            _appViewState.update {
                AppViewState.Authenticated(
                    user = user,
                    needsInitialSetup = userData?.needsInitialSetup == true,
                )
            }
        } ?: _appViewState.update { AppViewState.Unauthenticated }

    }
}