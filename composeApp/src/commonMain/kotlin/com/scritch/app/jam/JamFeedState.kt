package com.scritch.app.jam

import dev.gitlive.firebase.firestore.DocumentSnapshot

data class JamFeedState(
    val isLoading: Boolean,
    val items: List<JamSubmission>,
    val cursor: Cursor?,
    val endReached: Boolean,
    val error: String?,
) {
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
    val lastDoc: DocumentSnapshot,
)