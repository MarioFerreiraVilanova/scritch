package com.scritch.app.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scritch.app.categories.Category
import com.scritch.app.categories.LoadUserOptionsUseCase
import com.scritch.app.categories.OptionState
import com.scritch.app.userdata.LoadUserDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val loadUserOptions: LoadUserOptionsUseCase,
    private val loadUserData: LoadUserDataUseCase,
) : ViewModel() {

    private val mutableViewState = MutableStateFlow(HomeScreenViewState(
        topic = null,
        medium = null,
        support = null,
        constraint = null,
        selectedOption = null,
    ))
    val viewState = mutableViewState.asStateFlow()

    private var categorySettings = emptyMap<Category, Boolean>()
    private var topics = emptyList<OptionState>()
    private var mediums = emptyList<OptionState>()
    private var supports = emptyList<OptionState>()
    private var constraints = emptyList<OptionState>()

    init {
        viewModelScope.launch {
            loadOptions()
        }
        viewModelScope.launch {
            loadUserData()?.collectLatest { userData ->
                categorySettings = userData.categorySettings
            }
        }
    }

    fun onResume(){
        viewModelScope.launch {
            loadOptions()
        }
    }

    fun onGeneratePrompt() {
        mutableViewState.update {
            it.copy(
                topic = if (categorySettings[Category.Topic] == true) {
                    unImposedOption(Category.Topic)
                } else {
                    topics.randomOrNull() ?: unImposedOption(Category.Topic)
                },
                medium = if (categorySettings[Category.Medium] == true) {
                    unImposedOption(Category.Medium)
                } else {
                    mediums.randomOrNull() ?: unImposedOption(Category.Medium)
                },
                support = if (categorySettings[Category.Support] == true) {
                    unImposedOption(Category.Support)
                } else {
                    supports.randomOrNull() ?: unImposedOption(Category.Support)
                },
                constraint = if (categorySettings[Category.Constraint] == true) {
                    unImposedOption(Category.Constraint)
                } else {
                    constraints.randomOrNull() ?: unImposedOption(Category.Constraint)
                },
            )
        }
    }

    fun onCategoryClick(clickedOption: OptionState) {
        mutableViewState.update {
            it.copy(
                selectedOption = clickedOption
            )
        }
    }

    fun onTipDisplayed() {
        mutableViewState.update {
            it.copy(
                selectedOption = null,
            )
        }
    }

    private suspend fun loadOptions() {
        topics = loadUserOptions(category = Category.Topic).filter { it.selected }
        mediums = loadUserOptions(category = Category.Medium).filter { it.selected }
        supports = loadUserOptions(category = Category.Support).filter { it.selected }
        constraints = loadUserOptions(category = Category.Constraint).filter { it.selected }
    }

    private fun unImposedOption(
        category: Category,
    ): OptionState = OptionState(
        id = "un_imposed_option_${category.name}",
        name = "un_imposed_option_${category.name}",
        selected = true,
        description = null,
        tips = null,
        prompt = when (category){
            Category.Medium -> "with a medium of your choice"
            Category.Support -> "on the support of your choice"
            Category.Topic -> "something"
            Category.Constraint -> ""
        }
    )
}