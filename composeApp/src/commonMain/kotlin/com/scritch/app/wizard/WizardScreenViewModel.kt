package com.scritch.app.wizard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.scritch.app.auth.AuthenticationRepository
import com.scritch.app.categories.LoadUserOptionsUseCase
import com.scritch.app.navigation.Authenticated
import com.scritch.app.userdata.UserDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WizardScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val authenticationRepository: AuthenticationRepository,
    private val userDataRepository: UserDataRepository,
    private val loadUserOptions: LoadUserOptionsUseCase,
) : ViewModel() {
    private val navArgs = savedStateHandle.toRoute<Authenticated.WizardMediumSelection>()
    private val mutableViewState = MutableStateFlow(
        WizardScreenViewState(
            category = navArgs.category,
            step = navArgs.step,
            optionStates = null,
            unselectAll = false,
        )
    )

    val viewState = mutableViewState.asStateFlow()

    init {
        viewModelScope.launch {
            loadOptions()
        }
        viewModelScope.launch {
            loadUserData()
        }
    }

    fun onOptionCheckChangeRequest (optionId: String){
        mutableViewState.value.optionStates?.find { it.id == optionId }?.let { option ->
            mutableViewState.update { state ->
                val options = state.optionStates?.minus(option)?.plus(option.copy(selected = !option.selected))
                state.copy(
                    optionStates = options?.sortedBy { it.name }
                )
            }    
        }
    }

    fun onContinue(){
        viewModelScope.launch {
            saveOptions()
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            saveOptions()
        }
    }

    fun onUnselectAll() {
        authenticationRepository.user()?.id?.let { userId ->
            viewModelScope.launch {
                userDataRepository.toggleCategory(
                    userId = userId,
                    category = viewState.value.category,
                    newValue = !viewState.value.unselectAll,
                )
            }
        }
    }

    private suspend fun loadOptions() {
        mutableViewState.update {
            it.copy(
                optionStates = loadUserOptions(category = navArgs.category),
            )
        }
    }

    private suspend fun saveOptions(){
        authenticationRepository.user()?.id?.let { userId ->
            mutableViewState.value.optionStates?.let { optionStates ->
                userDataRepository.disableOptions(
                    userId = userId,
                    category = navArgs.category,
                    optionIds = optionStates.filter { !it.selected }.map { it.id }
                )
            }
        }
    }

    private suspend fun loadUserData(){
        authenticationRepository.user()?.id?.let { userId ->
            userDataRepository.userDataFlow(userId).collectLatest { userData ->
                mutableViewState.update {
                    it.copy(
                        unselectAll = userData.categorySettings[navArgs.category] ?: false,
                    )
                }
            }
        }
    }
}