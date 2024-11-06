package app.minimal.fasting.fasting.wizard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.minimal.fasting.auth.AuthenticationRepository
import app.minimal.fasting.common.next
import app.minimal.fasting.fasting.FastingPrefsDto
import app.minimal.fasting.fasting.FastingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime

class FastingWizardViewModel(
    private val fastingRepository: FastingRepository,
    private val authenticationRepository: AuthenticationRepository,
) : ViewModel() {
    private val _viewState = MutableStateFlow(
        WizardViewState(
            fastingPrefs = FastingPrefsDto(),
            currentPage = WizardPage.entries.first(),
        )
    )
    val viewState = _viewState.asStateFlow()

    fun onNext(
        selectedTime: LocalTime? = null,
        selectedFastingGoal: Int? = null,
        selectedDays: Map<String, Boolean>? = null,
    ) {
        _viewState.update {
            it.copy(
                currentPage = if (it.currentPage != WizardPage.entries.last()) {
                    it.currentPage.next()
                } else {
                    it.currentPage
                },
                fastingPrefs = it.fastingPrefs.copy(
                    startingTimeHour = selectedTime?.hour ?: it.fastingPrefs.startingTimeHour,
                    startingTimeMinute = selectedTime?.minute ?: it.fastingPrefs.startingTimeMinute,
                    fastingHours = selectedFastingGoal ?: it.fastingPrefs.fastingHours,
                    fastingDays = selectedDays ?: it.fastingPrefs.fastingDays,
                )
            )
        }

        viewModelScope.launch {
            fastingRepository.savePrefs(
                userId = authenticationRepository.user()?.id
                    ?: throw (
                            RuntimeException("Can't save preferences if the user is not logged in")
                            ),
                prefs = _viewState.value.fastingPrefs,
            )
        }
    }
}