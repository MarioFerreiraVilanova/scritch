package app.minimal.fasting.fasting.timeselection

import kotlinx.datetime.LocalDateTime

data class TimeSelectionViewState(
    val startingTime: LocalDateTime?,
    val saving: Boolean,
    val done: Boolean,
)
