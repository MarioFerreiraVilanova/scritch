package com.scritch.app.jam

import com.scritch.app.jam.data.SubmissionDto
import dev.gitlive.firebase.firestore.Timestamp

data class JamSubmission(
    val userId: String,
    val imageUrl: String,
    val thumbnailUrl: String?,
    val createdAt: Timestamp,
    val status: ModerationStatus,
    val nickname: String,
) {
    companion object {
        fun fromDto(dto: SubmissionDto): JamSubmission? {
            return JamSubmission(
                userId = dto.userId ?: return null,
                imageUrl = dto.imageUrl ?: return null,
                thumbnailUrl = dto.thumbnailUrl,
                createdAt = dto.createdAt ?: return null,
                status = submissionStatusFromString(dto.status) ?: return null,
                nickname = dto.nickname ?: return null,
            )
        }
    }
}

enum class ModerationStatus {
    Pending,
    Approved,
    Rejected,
}

fun submissionStatusFromString(string: String): ModerationStatus? = when (string) {
    "pending" -> ModerationStatus.Pending
    "approved" -> ModerationStatus.Approved
    "rejected" -> ModerationStatus.Rejected
    else -> null
}
