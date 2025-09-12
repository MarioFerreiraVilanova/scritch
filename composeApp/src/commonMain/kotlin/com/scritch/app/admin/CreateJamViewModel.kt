package com.scritch.app.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scritch.app.categories.Category
import com.scritch.app.categories.CategoryRepository
import com.scritch.app.categories.OptionState
import com.scritch.app.jam.data.JamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class CreateJamViewModel(
    private val jamRepository: JamRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow(CreateJamViewState.EMPTY)
    val viewState: StateFlow<CreateJamViewState> = _viewState.asStateFlow()

    init {
        loadCategoryOptions()
    }

    private fun loadCategoryOptions() {
        viewModelScope.launch {
            try {
                val topicOptions = categoryRepository.getOptions(Category.Topic)
                    .map { OptionState.fromDto(it, selected = false) }
                    .filterNotNull()

                val mediumOptions = categoryRepository.getOptions(Category.Medium)
                    .map { OptionState.fromDto(it, selected = false) }
                    .filterNotNull()

                val supportOptions = categoryRepository.getOptions(Category.Support)
                    .map { OptionState.fromDto(it, selected = false) }
                    .filterNotNull()

                val constraintOptions = categoryRepository.getOptions(Category.Constraint)
                    .map { OptionState.fromDto(it, selected = false) }
                    .filterNotNull()

                _viewState.value = _viewState.value.copy(
                    topicOptions = topicOptions,
                    mediumOptions = mediumOptions,
                    supportOptions = supportOptions,
                    constraintOptions = constraintOptions,
                    isLoading = false,
                )
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(
                    isLoading = false,
                    error = "Failed to load category options: ${e.message}"
                )
            }
        }
    }

    fun onJamNameChanged(name: String) {
        _viewState.value = _viewState.value.copy(
            jamName = name,
            validationErrors = validateForm(_viewState.value.copy(jamName = name))
        )
    }

    fun onStartDateChanged(date: LocalDate) {
        val newState = _viewState.value.copy(startDate = date)
        _viewState.value = newState.copy(
            validationErrors = validateForm(newState)
        )
    }

    fun onEndDateChanged(date: LocalDate) {
        val newState = _viewState.value.copy(endDate = date)
        _viewState.value = newState.copy(
            validationErrors = validateForm(newState)
        )
    }

    fun onTopicSelected(option: OptionState?) {
        _viewState.value = _viewState.value.copy(
            selectedTopic = option,
            validationErrors = validateForm(_viewState.value.copy(selectedTopic = option))
        )
    }

    fun onMediumSelected(option: OptionState?) {
        val newState = _viewState.value.copy(selectedMedium = option)
        _viewState.value = newState.copy(
            validationErrors = validateForm(newState)
        )
    }

    fun onSupportSelected(option: OptionState?) {
        val newState = _viewState.value.copy(selectedSupport = option)
        _viewState.value = newState.copy(
            validationErrors = validateForm(newState)
        )
    }

    fun onConstraintSelected(option: OptionState?) {
        _viewState.value = _viewState.value.copy(
            selectedConstraint = option,
            validationErrors = validateForm(_viewState.value.copy(selectedConstraint = option))
        )
    }

    fun onCreateJam() {
        val currentState = _viewState.value
        
        if (!currentState.isFormValid) {
            _viewState.value = currentState.copy(
                validationErrors = validateForm(currentState)
            )
            return
        }

        _viewState.value = currentState.copy(isCreating = true, error = null)

        viewModelScope.launch {
            try {
                jamRepository.createJam(
                    jamId = currentState.jamName,
                    startDate = currentState.startDate!!,
                    endDate = currentState.endDate!!,
                    topicId = currentState.selectedTopic?.id,
                    mediumId = currentState.selectedMedium?.id,
                    supportId = currentState.selectedSupport?.id,
                    constraintId = currentState.selectedConstraint?.id,
                )
                
                // Success
                _viewState.value = currentState.copy(
                    isCreating = false,
                    error = null,
                    isCreateSuccessful = true
                )
            } catch (e: Exception) {
                _viewState.value = currentState.copy(
                    isCreating = false,
                    error = "Failed to create jam: ${e.message}"
                )
            }
        }
    }

    fun onDismissError() {
        _viewState.value = _viewState.value.copy(error = null)
    }

    private fun validateForm(state: CreateJamViewState): ValidationErrors {
        var jamNameError: String? = null
        var startDateError: String? = null
        var endDateError: String? = null
        var categoriesError: String? = null

        // Jam name validation
        if (state.jamName.isBlank()) {
            jamNameError = "Jam name is required"
        }

        // Date validation
        if (state.startDate == null) {
            startDateError = "Start date is required"
        }
        if (state.endDate == null) {
            endDateError = "End date is required"
        }
        if (state.startDate != null && state.endDate != null && state.startDate >= state.endDate) {
            endDateError = "End date must be after start date"
        }

        // Category validation
        if (state.selectedMedium == null && state.selectedSupport == null) {
            categoriesError = "At least one of Medium or Support must be selected"
        }

        return ValidationErrors(
            jamName = jamNameError,
            startDate = startDateError,
            endDate = endDateError,
            categories = categoriesError,
        )
    }
}