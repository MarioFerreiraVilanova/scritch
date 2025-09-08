package com.scritch.app.reporting

import dev.gitlive.firebase.firestore.Timestamp

data class UserReport(
    val reporterId: String,
    val reportedUserId: String,
    val submissionId: String,
    val jamId: String,
    val reason: String,
    val createdAt: Timestamp = Timestamp.now(),
    val status: String = "pending", // pending, confirmed
)

enum class ReportReason(val displayText: String) {
    INAPPROPRIATE_CONTENT("Inappropriate content"),
    SPAM("Spam"),
    HARASSMENT("Harassment"),
    COPYRIGHT("Copyright violation"),
    OTHER("Other")
}