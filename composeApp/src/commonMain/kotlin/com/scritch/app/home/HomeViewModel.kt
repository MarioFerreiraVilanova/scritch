package com.scritch.app.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scritch.app.categories.Category
import com.scritch.app.categories.LoadUserOptionsUseCase
import com.scritch.app.categories.OptionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val loadUserOptions: LoadUserOptionsUseCase,
) : ViewModel() {

    private val mutableViewState = MutableStateFlow(HomeScreenViewState(
        medium = null,
        support = null,
    ))
    val viewState = mutableViewState.asStateFlow()

    private var mediums = emptyList<OptionState>()
    private var supports = emptyList<OptionState>()

    init {
        viewModelScope.launch {
            initOptions()
        }
    }

    fun onGeneratePrompt() {
        mutableViewState.update {
            it.copy(
                medium = mediums.random(),
                support = supports.random()
            )
        }
    }

    private suspend fun initOptions(){
        mediums = loadUserOptions(category = Category.Medium).filter { it.selected }
        supports = loadUserOptions(category = Category.Support).filter { it.selected }
    }
}