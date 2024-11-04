package app.minimal.fasting.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.minimal.fasting.auth.AuthenticationRepository
import app.minimal.fasting.fasting.FastingRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val fastingRepository: FastingRepository,
    private val authenticationRepository: AuthenticationRepository,
): ViewModel() {

    fun onClick(){
        viewModelScope.launch {
            authenticationRepository.user()?.let {
                fastingRepository.click(
                    userId = it.id
                )
            }
        }
    }

    fun onLogOut(){
        viewModelScope.launch {
            authenticationRepository.logout()
        }
    }
}