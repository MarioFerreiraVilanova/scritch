package app.minimal.fasting.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.minimal.fasting.userprefs.UserPrefsRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userPrefsRepository: UserPrefsRepository,
): ViewModel() {

    fun onClick(){
        viewModelScope.launch {
            Firebase.auth.currentUser?.let {
                userPrefsRepository.click(
                    userId = it.uid
                )
            }
        }
    }

    fun onLogOut(){
        viewModelScope.launch {
            Firebase.auth.signOut()
        }
    }
}