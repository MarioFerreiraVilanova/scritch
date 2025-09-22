package com.scritch.app.jam.data

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

enum class JamStatus {
    Planned,
    Ongoing,
    Finished
}

@OptIn(ExperimentalTime::class)
fun JamDto.getStatus(): JamStatus {
    val now = Clock.System.now()
    val startDate = this.startDate
    val endDate = this.endDate

    return when {
        startDate == null || endDate == null -> JamStatus.Planned // Default if dates missing
        now < startDate -> JamStatus.Planned
        now > endDate -> JamStatus.Finished
        else -> JamStatus.Ongoing
    }
}