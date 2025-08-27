package com.scritch.app.jam

import com.scritch.app.jam.data.SubmissionDto
import dev.gitlive.firebase.firestore.Timestamp

data class JamSubmission(
    val userId: String,
    val imageUrl: String,
    val createdAt: Timestamp,
    val status: SubmissionStatus,
) {
    companion object {
        fun fromDto(dto: SubmissionDto): JamSubmission? {
            return JamSubmission(
                userId = dto.userId ?: return null,
                imageUrl = dto.imageUrl ?: return null,
                createdAt = dto.createdAt ?: return null,
                status = submissionStatusFromString(dto.status) ?: return null,
            )
        }
    }
}

enum class SubmissionStatus {
    Pending,
    Approved,
    Rejected,
}

fun submissionStatusFromString(string: String): SubmissionStatus? = when (string) {
    "pending" -> SubmissionStatus.Pending
    "approved" -> SubmissionStatus.Approved
    "rejected" -> SubmissionStatus.Rejected
    else -> null
}
