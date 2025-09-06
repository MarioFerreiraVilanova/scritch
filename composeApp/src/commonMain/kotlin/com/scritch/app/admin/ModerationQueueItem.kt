package com.scritch.app.admin

import kotlinx.serialization.Serializable

@Serializable
data class ModerationQueueItem(
    val jamId: String,
    val userId: String,
    val submissionId: String,
    val imageUrl: String,
    val nickname: String,
    val confirmedReports: Int,
    val effectiveReports: Int,
    val createdAt: Long,
)