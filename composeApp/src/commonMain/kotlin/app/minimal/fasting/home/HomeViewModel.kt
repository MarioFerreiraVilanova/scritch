package app.minimal.fasting.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {
    fun onLogOut(){
        viewModelScope.launch {
            Firebase.auth.signOut()
        }
    }
}