package com.scritch.app.wizard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.scritch.app.auth.AuthenticationRepository
import com.scritch.app.categories.Category
import com.scritch.app.categories.CategoryRepository
import com.scritch.app.categories.OptionState
import com.scritch.app.navigation.Authenticated
import com.scritch.app.userdata.UserData
import com.scritch.app.userdata.UserDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WizardScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val authenticationRepository: AuthenticationRepository,
    private val categoryRepository: CategoryRepository,
    private val userDataRepository: UserDataRepository,
) : ViewModel() {
    private val navArgs = savedStateHandle.toRoute<Authenticated.WizardMediumSelection>()
    private val mutableViewState = MutableStateFlow(
        WizardScreenViewState(
            category = navArgs.category,
            step = navArgs.step,
            optionStates = null,
        )
    )

    val viewState = mutableViewState.asStateFlow()

    init {
        viewModelScope.launch {
            loadOptions()
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
    }

    private suspend fun loadOptions() {
        authenticationRepository.user()?.id?.let { userId ->
            val optionDtos = categoryRepository.getOptions(category = navArgs.category)
            val userData = userDataRepository.userData(userId = userId)
            val disabledOptions = when (navArgs.category){
                Category.Medium -> userData.disabledMediumIds
                Category.Support -> userData.disabledSupportIds
            }
            mutableViewState.update {
                it.copy(
                    optionStates = optionDtos.mapNotNull { dto ->
                        OptionState.fromDto(
                            dto = dto,
                            selected = !disabledOptions.contains(dto.id),
                        )
                    }.sortedBy { it.name }
                )
            }
        }
    }
}