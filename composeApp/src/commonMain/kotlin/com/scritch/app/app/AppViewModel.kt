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

    private fun onUserChanged(user: User?){
        if (user != null){
            _appViewState.update {
                AppViewState.Authenticated(
                    user = user,
                )
            }
        } else {
            _appViewState.update { AppViewState.Unauthenticated }
        }

    }
}