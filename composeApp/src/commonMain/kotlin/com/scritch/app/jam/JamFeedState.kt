package com.scritch.app.jam

import com.scritch.app.jam.data.SubmissionDto

data class JamFeedState(
    val isLoading: Boolean = true,
    val items: List<SubmissionDto> = emptyList(),
    val cursor: Cursor? = null,
    val endReached: Boolean = false,
    val error: String? = null
)

data class Page<T>(
    val items: List<T>,
    val cursor: Cursor?,
    val endReached: Boolean,
)

data class Cursor(
    val lastDocId: String,
)