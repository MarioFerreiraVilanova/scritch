package app.minimal.fasting.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.minimal.fasting.auth.AuthenticationRepository
import kotlinx.coroutines.launch

class LandingViewModel(
    private val authenticationRepository: AuthenticationRepository,
): ViewModel() {
    fun onLogIn(){
        viewModelScope.launch {
            authenticationRepository.login()
        }
    }
}