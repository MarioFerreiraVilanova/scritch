package app.minimal.fasting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel: ViewModel() {

    private val _appViewState = MutableStateFlow(
        AppViewState(
            user = Firebase.auth.currentUser
        )
    )
    val appViewState = _appViewState.asStateFlow()

    init {
        viewModelScope.launch {
            Firebase.auth.authStateChanged.collectLatest { firebaseUser ->
                _appViewState.update {
                    it.copy(
                        user = firebaseUser
                    )
                }
            }
        }
    }

    fun onLogOut(){
        viewModelScope.launch {
            Firebase.auth.signOut()
        }
    }
}