package com.scritch.app.jam.ui

data class JamArchivesViewState(
    val jams: List<ArchiveJamViewState> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val endReached: Boolean = false,
    val error: String? = null,
)