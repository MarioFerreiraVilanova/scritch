package com.scritch.app.reporting

import dev.gitlive.firebase.firestore.Timestamp

data class UserReportDto(
    val reporterId: String? = null,
    val reportedUserId: String? = null,
    val submissionId: String? = null,
    val jamId: String? = null,
    val reason: String? = null,
    val createdAt: Timestamp? = null,
    val status: String? = null,
)