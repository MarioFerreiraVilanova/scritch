package com.scritch.app.admin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.scritch.app.categories.Category
import com.scritch.app.categories.CategoryRepository
import com.scritch.app.categories.OptionState
import com.scritch.app.jam.data.JamDto
import com.scritch.app.jam.data.JamRepository
import com.scritch.app.navigation.Authenticated
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

class CreateJamViewModel(
    savedStateHandle: SavedStateHandle,
    private val jamRepository: JamRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow(CreateJamViewState.EMPTY)
    val viewState: StateFlow<CreateJamViewState> = _viewState.asStateFlow()

    private val editJamArgs: Authenticated.EditJam? = try {
        savedStateHandle.toRoute<Authenticated.EditJam>()
    } catch (_: Exception) {
        null
    }

    val isEditMode = editJamArgs != null
    private val originalJamId = editJamArgs?.jamId

    init {
        loadCategoryOptions()
    }

    private fun loadCategoryOptions() {
        viewModelScope.launch {
            try {
                val topicOptions = categoryRepository.getOptions(Category.Topic)
                    .mapNotNull { OptionState.fromDto(it, selected = false) }

                val mediumOptions = categoryRepository.getOptions(Category.Medium)
                    .mapNotNull { OptionState.fromDto(it, selected = false) }

                val supportOptions = categoryRepository.getOptions(Category.Support)
                    .mapNotNull { OptionState.fromDto(it, selected = false) }

                val constraintOptions = categoryRepository.getOptions(Category.Constraint)
                    .mapNotNull { OptionState.fromDto(it, selected = false) }

                _viewState.value = _viewState.value.copy(
                    topicOptions = topicOptions,
                    mediumOptions = mediumOptions,
                    supportOptions = supportOptions,
                    constraintOptions = constraintOptions,
                    isLoading = false,
                )

                // After options are loaded, load existing jam data if in edit mode
                originalJamId?.let { jamId ->
                    loadExistingJam(jamId)
                }
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(
                    isLoading = false,
                    error = "Failed to load category options: ${e.message}"
                )
            }
        }
    }

    private fun loadExistingJam(jamId: String) {
        viewModelScope.launch {
            try {
                val jam = jamRepository.getJam(jamId)
                if (jam != null) {
                    populateFormWithJamData(jam)
                } else {
                    _viewState.value = _viewState.value.copy(
                        error = "Jam not found: $jamId"
                    )
                }
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(
                    error = "Failed to load jam: ${e.message}"
                )
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun populateFormWithJamData(jam: JamDto) {
        val currentState = _viewState.value

        println("CreateJamViewModel: Populating form with jam data:")
        println("  jamId: ${jam.id}")
        println("  topic: ${jam.topic}")
        println("  medium: ${jam.medium}")
        println("  support: ${jam.support}")
        println("  constraint: ${jam.constraint}")
        println("  Available topic options: ${currentState.topicOptions.map { "${it.id}: ${it.name}" }}")
        println("  Available medium options: ${currentState.mediumOptions.map { "${it.id}: ${it.name}" }}")

        val startDateTime = jam.startDate?.toLocalDateTime(TimeZone.currentSystemDefault())
        val endDateTime = jam.endDate?.toLocalDateTime(TimeZone.currentSystemDefault())

        val selectedTopic = jam.topic?.let { topicId ->
            currentState.topicOptions.find { it.id == topicId }
        }
        val selectedMedium = jam.medium?.let { mediumId ->
            currentState.mediumOptions.find { it.id == mediumId }
        }
        val selectedSupport = jam.support?.let { supportId ->
            currentState.supportOptions.find { it.id == supportId }
        }
        val selectedConstraint = jam.constraint?.let { constraintId ->
            currentState.constraintOptions.find { it.id == constraintId }
        }

        println("  Found selectedTopic: ${selectedTopic?.name}")
        println("  Found selectedMedium: ${selectedMedium?.name}")
        println("  Found selectedSupport: ${selectedSupport?.name}")
        println("  Found selectedConstraint: ${selectedConstraint?.name}")

        val newState = currentState.copy(
            jamName = jam.id,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            selectedTopic = selectedTopic,
            selectedMedium = selectedMedium,
            selectedSupport = selectedSupport,
            selectedConstraint = selectedConstraint,
            // Store original values for change tracking
            originalStartDateTime = startDateTime,
            originalEndDateTime = endDateTime,
            originalSelectedTopic = selectedTopic,
            originalSelectedMedium = selectedMedium,
            originalSelectedSupport = selectedSupport,
            originalSelectedConstraint = selectedConstraint,
        )

        _viewState.value = newState.copy(
            validationErrors = validateForm(newState)
        )
    }

    fun onJamNameChanged(name: String) {
        _viewState.value = _viewState.value.copy(
            jamName = name,
            validationErrors = validateForm(_viewState.value.copy(jamName = name))
        )
    }

    fun onStartDateTimeChanged(dateTime: LocalDateTime) {
        val newState = _viewState.value.copy(startDateTime = dateTime)
        _viewState.value = newState.copy(
            validationErrors = validateForm(newState)
        )
    }

    fun onEndDateTimeChanged(dateTime: LocalDateTime) {
        val newState = _viewState.value.copy(endDateTime = dateTime)
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
                if (isEditMode && originalJamId != null) {
                    jamRepository.updateJam(
                        jamId = originalJamId,
                        startDateTime = currentState.startDateTime!!,
                        endDateTime = currentState.endDateTime!!,
                        topicId = currentState.selectedTopic?.id,
                        mediumId = currentState.selectedMedium?.id,
                        supportId = currentState.selectedSupport?.id,
                        constraintId = currentState.selectedConstraint?.id,
                    )
                } else {
                    jamRepository.createJam(
                        jamId = currentState.jamName,
                        startDateTime = currentState.startDateTime!!,
                        endDateTime = currentState.endDateTime!!,
                        topicId = currentState.selectedTopic?.id,
                        mediumId = currentState.selectedMedium?.id,
                        supportId = currentState.selectedSupport?.id,
                        constraintId = currentState.selectedConstraint?.id,
                    )
                }
                
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
        if (state.startDateTime == null) {
            startDateError = "Start date is required"
        }
        if (state.endDateTime == null) {
            endDateError = "End date is required"
        }
        if (state.startDateTime != null && state.endDateTime != null && state.startDateTime >= state.endDateTime) {
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