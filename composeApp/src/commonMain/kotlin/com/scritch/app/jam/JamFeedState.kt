package com.scritch.app.jam

import com.scritch.app.jam.data.SubmissionDto

data class JamFeedState(
    val isLoading: Boolean,
    val items: List<SubmissionDto>,
    val cursor: Cursor?,
    val endReached: Boolean,
    val error: String?,
){
    companion object {
        val EMPTY = JamFeedState(
            isLoading = true,
            items = emptyList(),
            cursor = null,
            endReached = false,
            error = null,
        )
    }
}

data class Page<T>(
    val items: List<T>,
    val cursor: Cursor?,
    val endReached: Boolean,
)

data class Cursor(
    val lastDocId: String,
)