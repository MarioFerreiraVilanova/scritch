package app.minimal.fasting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch

class AppViewModel: ViewModel() {
    fun onLogIn(){
        viewModelScope.launch {
            println("This is a test")
            //Firebase.auth.signInAnonymously()
        }
    }
}