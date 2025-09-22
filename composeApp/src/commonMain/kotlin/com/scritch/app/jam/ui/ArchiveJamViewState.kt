package com.scritch.app.jam.ui

import com.scritch.app.categories.OptionState
import com.scritch.app.prompt.buildPromptString
import kotlinx.datetime.LocalDate

data class ArchiveJamViewState(
    val id: String,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val topic: OptionState?,
    val medium: OptionState?,
    val support: OptionState?,
    val constraint: OptionState?,
    val submissionCount: Int,
    val userParticipated: Boolean,
) {
    val promptText: String?
        get() = buildPromptString(
            topic = topic,
            support = support,
            medium = medium,
            constraint = constraint,
        )
}