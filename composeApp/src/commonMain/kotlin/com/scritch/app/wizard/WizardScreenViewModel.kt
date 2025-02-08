package com.scritch.app.wizard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WizardScreenViewModel (
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val mutableViewState = MutableStateFlow(
        savedStateHandle.toRoute<WizardScreenViewState>().let {
            WizardScreenViewState(
                category = it.category,
                step = it.step,
            )
        }
    )
    val viewState = mutableViewState.asStateFlow()
}