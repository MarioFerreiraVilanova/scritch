package com.scritch.app.admin

import com.scritch.app.jam.Cursor
import com.scritch.app.jam.data.JamDto

data class JamManagementViewState(
    val jams: List<JamDto>,
    val cursor: Cursor?,
    val endReached: Boolean,
    val isLoading: Boolean,
    val isLoadingMore: Boolean,
    val error: String?,
) {
    companion object {
        val EMPTY = JamManagementViewState(
            jams = emptyList(),
            cursor = null,
            endReached = false,
            isLoading = false,
            isLoadingMore = false,
            error = null,
        )
    }
}