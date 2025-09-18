package com.scritch.app.jam.ui

import com.scritch.app.jam.data.JamDto

data class JamArchivesViewState(
    val jams: List<JamDto> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val endReached: Boolean = false,
    val error: String? = null,
)