package com.scritch.app.wizard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.scritch.app.categories.CategoryRepository
import com.scritch.app.categories.Option
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WizardScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {
    private val navArgs = savedStateHandle.toRoute<WizardScreenViewState>()
    private val mutableViewState = MutableStateFlow(
        WizardScreenViewState(
            category = navArgs.category,
            step = navArgs.step,
            options = null,
        )
    )

    val viewState = mutableViewState.asStateFlow()

    init {
        viewModelScope.launch {
            loadOptions()
        }
    }

    private suspend fun loadOptions() {
        val optionDtos = categoryRepository.getOptions(category = navArgs.category)
        mutableViewState.update {
            it.copy(
                options = optionDtos.mapNotNull { dto ->
                    Option.fromDto(
                        dto = dto,
                        selected = true,
                    )
                }
            )
        }
    }
}