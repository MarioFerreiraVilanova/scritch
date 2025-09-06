package com.scritch.app.admin

import kotlinx.serialization.Serializable

@Serializable
data class ModerateSubmissionRequest(
    val jamId: String,
    val userId: String,
    val status: String, // "approved" | "rejected" | "pending"
    val reason: String,
)