package com.scritch.app.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scritch.app.auth.AuthenticationRepository
import com.scritch.app.userdata.UserDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LandingViewModel(
    private val authenticationRepository: AuthenticationRepository,
): ViewModel() {

    private val mutableViewState = MutableStateFlow(LandingViewState())
    val viewState = mutableViewState.asStateFlow()

    fun onSkip(){
        viewModelScope.launch {
            authenticationRepository.login()
            mutableViewState.update { it.copy(events = it.events.plus(LandingEvent.GoHome)) }
        }
    }

    fun onContinue(){
        viewModelScope.launch {
            authenticationRepository.login()
            mutableViewState.update { it.copy(events = it.events.plus(LandingEvent.GoToWizard)) }
        }
    }

    fun consumeEvent(event: LandingEvent){
        mutableViewState.update { it.copy(events = it.events.minus(event)) }
    }
}