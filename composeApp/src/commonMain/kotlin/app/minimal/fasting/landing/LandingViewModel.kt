package app.minimal.fasting.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch

class LandingViewModel: ViewModel() {
    fun onLogIn(){
        viewModelScope.launch {
            Firebase.auth.signInAnonymously()
        }
    }
}