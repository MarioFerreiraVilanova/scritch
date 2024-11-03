package app.minimal.fasting.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {

    private val _appViewState = MutableStateFlow(
        Firebase.auth.currentUser?.let { user ->
            AppViewState.Authenticated(user = user)
        } ?: AppViewState.Unauthenticated
    )
    val appViewState = _appViewState.asStateFlow()

    init {
        viewModelScope.launch {
            Firebase.auth.authStateChanged.collectLatest { user ->
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