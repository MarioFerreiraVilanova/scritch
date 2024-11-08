package app.minimal.fasting.fasting.status

import kotlinx.datetime.LocalDateTime

sealed class FastingStatusViewState {
    data object Loading : FastingStatusViewState()
    data object NeedsSetup : FastingStatusViewState()
    data class Loaded(
        val goal: Int,
        val window: DayWindow,
    ) : FastingStatusViewState()

}

sealed class DayWindow {
    data class EatingViewState(
        val startingTime: LocalDateTime,
    ) : DayWindow()

    data class FastingViewState(
        val startingTime: LocalDateTime,
        val endingTime: LocalDateTime,
    ) : DayWindow()
}