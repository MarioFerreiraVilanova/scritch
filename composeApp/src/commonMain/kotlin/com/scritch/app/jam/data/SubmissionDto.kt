package com.scritch.app.jam.data

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class SubmissionDto(
    val userId: String? = null,
    val storagePath: String? = null,
    val imageUrl: String? = null,
    val thumbnailUrl: String? = null,
    val caption: String? = null,
    val createdAt: Timestamp? = null,
    val status: String = "pending",
    val nickname: String? = null,
)