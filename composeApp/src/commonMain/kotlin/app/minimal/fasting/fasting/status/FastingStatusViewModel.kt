package app.minimal.fasting.fasting.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.minimal.fasting.auth.AuthenticationRepository
import app.minimal.fasting.common.now
import app.minimal.fasting.fasting.repository.FastingPrefsDto
import app.minimal.fasting.fasting.repository.FastingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class FastingStatusViewModel(
    fastingRepository: FastingRepository,
    authenticationRepository: AuthenticationRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow<FastingStatusViewState>(
        value = FastingStatusViewState.Loading,
    )
    val viewState = _viewState.asStateFlow()

    private val fastingPrefs = fastingRepository.fastingPrefs(
        userId = authenticationRepository.user()?.id
            ?: throw (RuntimeException("We shouldn't be here if the user is not authenticated"))
    )

    init {
        viewModelScope.launch {
            fastingPrefs.collectLatest { fastingPrefs ->
                if (fastingPrefs == null) {
                    _viewState.update {
                        FastingStatusViewState.NeedsSetup
                    }
                } else {
                    _viewState.update {
                        FastingStatusViewState.Loaded(
                            goal = fastingPrefs.fastingHours,
                            window = DayWindow.EatingViewState(
                                fastStartingTime = fastingPrefs.nextFastingStartTime()
                            ),
                        )
                    }
                }
            }
        }
    }

    /**
     * Returns the date in which the user should start the next fast
     */
    private fun FastingPrefsDto.nextFastingStartTime(
    ): LocalDateTime {
        val currentDateTime = now()
        val currentDayOfWeek = currentDateTime.date.dayOfWeek

        // Check if today is a fasting day and if starting time hasn't passed
        if (fastingDays[currentDayOfWeek.name.lowercase()] == true) {
            val todayStartTime = currentDateTime.date
                .atTime(startingTimeHour, startingTimeMinute)

            if (currentDateTime < todayStartTime) {
                return todayStartTime
            }
        }

        // Find the next fasting day from today
        for (i in 1..7) {
            val nextDay = DayOfWeek.entries[currentDayOfWeek.ordinal.plus(i)]
            if (fastingDays[nextDay.name.lowercase()] == true) {
                val nextDate = currentDateTime.date.plus(i, DateTimeUnit.DAY)
                return nextDate.atTime(startingTimeHour, startingTimeMinute)
            }
        }

        // This line should theoretically never be reached if there's always at least one fasting day.
        throw IllegalStateException("No fasting days are enabled.")
    }
}