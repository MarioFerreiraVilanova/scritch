package com.scritch.app.admin

import kotlinx.serialization.Serializable

@Serializable
data class RecalculateStatsResponse(
    val success: Boolean,
    val message: String,
    val jamId: String,
    val submissionCount: Int = 0,
    val participantCount: Int = 0,
)