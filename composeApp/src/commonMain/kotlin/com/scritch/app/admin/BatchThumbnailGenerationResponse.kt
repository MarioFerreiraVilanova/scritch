package com.scritch.app.admin

import kotlinx.serialization.Serializable

@Serializable
data class BatchThumbnailGenerationResponse(
    val success: Boolean = true,
    val message: String,
    val processedCount: Int = 0,
    val errorCount: Int = 0,
)