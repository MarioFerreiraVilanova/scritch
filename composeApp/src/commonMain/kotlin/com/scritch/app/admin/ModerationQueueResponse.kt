package com.scritch.app.admin

import kotlinx.serialization.Serializable

@Serializable
data class ModerationQueueResponse(
    val success: Boolean,
    val queue: List<ModerationQueueItem>,
    val total: Int,
)