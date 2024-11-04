package app.minimal.fasting.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.minimal.fasting.auth.AuthenticationRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
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