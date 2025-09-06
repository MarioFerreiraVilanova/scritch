package com.scritch.app.admin

import kotlinx.serialization.Serializable

@Serializable
data class ModerateSubmissionResponse(
    val success: Boolean,
    val message: String,
    val jamId: String,
    val userId: String,
    val newStatus: String,
)