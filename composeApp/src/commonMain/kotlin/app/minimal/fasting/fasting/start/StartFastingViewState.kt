package app.minimal.fasting.fasting.start

import kotlinx.datetime.LocalDateTime

data class StartFastingViewState(
    val startingTime: LocalDateTime?,
    val saving: Boolean,
    val done: Boolean,
)
