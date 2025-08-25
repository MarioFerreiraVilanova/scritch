package com.scritch.app.jam

import com.scritch.app.jam.data.SubmissionDto
import dev.gitlive.firebase.firestore.Timestamp

data class JamSubmission(
    val userId: String,
    val imageUrl: String,
    val createdAt: Timestamp,
) {
    companion object {
        fun fromDto(dto: SubmissionDto): JamSubmission? {
            return JamSubmission(
                userId = dto.userId ?: return null,
                imageUrl = dto.imageUrl ?: return null,
                createdAt = dto.createdAt ?: return null,
            )
        }
    }
}
