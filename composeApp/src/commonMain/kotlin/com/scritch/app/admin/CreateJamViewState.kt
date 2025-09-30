package com.scritch.app.admin

import com.scritch.app.categories.OptionState
import com.scritch.app.prompt.PromptViewState
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

data class CreateJamViewState(
    val jamName: String,
    val startDateTime: LocalDateTime?,
    val endDateTime: LocalDateTime?,
    val topicOptions: List<OptionState>,
    val mediumOptions: List<OptionState>,
    val supportOptions: List<OptionState>,
    val constraintOptions: List<OptionState>,
    val selectedTopic: OptionState?,
    val selectedMedium: OptionState?,
    val selectedSupport: OptionState?,
    val selectedConstraint: OptionState?,
    val isLoading: Boolean,
    val isCreating: Boolean,
    val error: String?,
    val validationErrors: ValidationErrors,
    val isCreateSuccessful: Boolean,
    // Original values for change tracking in edit mode
    val originalStartDateTime: LocalDateTime? = null,
    val originalEndDateTime: LocalDateTime? = null,
    val originalSelectedTopic: OptionState? = null,
    val originalSelectedMedium: OptionState? = null,
    val originalSelectedSupport: OptionState? = null,
    val originalSelectedConstraint: OptionState? = null,
) {
    val promptViewState: PromptViewState
        get() = PromptViewState(
            topic = selectedTopic,
            medium = selectedMedium,
            support = selectedSupport,
            constraint = selectedConstraint,
            selectedOption = null,
        )

    val isFormValid: Boolean
        get() = validationErrors.isEmpty() &&
                promptViewState.valid &&
                jamName.isNotBlank() &&
                startDateTime != null &&
                endDateTime != null

    val hasChanges: Boolean
        get() = startDateTime != originalStartDateTime ||
                endDateTime != originalEndDateTime ||
                selectedTopic?.id != originalSelectedTopic?.id ||
                selectedMedium?.id != originalSelectedMedium?.id ||
                selectedSupport?.id != originalSelectedSupport?.id ||
                selectedConstraint?.id != originalSelectedConstraint?.id

    companion object {
        val EMPTY = CreateJamViewState(
            jamName = generateDefaultJamName(),
            startDateTime = null,
            endDateTime = null,
            topicOptions = emptyList(),
            mediumOptions = emptyList(),
            supportOptions = emptyList(),
            constraintOptions = emptyList(),
            selectedTopic = null,
            selectedMedium = null,
            selectedSupport = null,
            selectedConstraint = null,
            isLoading = true,
            isCreating = false,
            error = null,
            validationErrors = ValidationErrors(),
            isCreateSuccessful = false,
        )

        @OptIn(ExperimentalTime::class)
        private fun generateDefaultJamName(): String {
            val now = Clock.System.now()
            val localDate = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
            val year = localDate.year
            val weekOfYear = getWeekOfYear(localDate)
            return "${year}_${weekOfYear.toString().padStart(2, '0')}"
        }

        private fun getWeekOfYear(date: LocalDate): Int {
            val dayOfYear = date.dayOfYear
            val jan1 = LocalDate(date.year, 1, 1)
            val dayOfWeek = jan1.dayOfWeek.ordinal
            return (dayOfYear + dayOfWeek - 1) / 7 + 1
        }
    }
}

data class ValidationErrors(
    val jamName: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val categories: String? = null,
) {
    fun isEmpty(): Boolean = jamName == null && startDate == null && endDate == null && categories == null
}