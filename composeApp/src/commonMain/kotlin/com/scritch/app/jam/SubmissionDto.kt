package com.scritch.app.jam

import dev.gitlive.firebase.firestore.FieldValue
import kotlinx.serialization.Serializable

@Serializable
data class SubmissionDto(
    val userId: String,
    val storagePath: String,
    val imageUrl: String? = null,
    val caption: String? = null,
    val createdAt: FieldValue? = null,
)
