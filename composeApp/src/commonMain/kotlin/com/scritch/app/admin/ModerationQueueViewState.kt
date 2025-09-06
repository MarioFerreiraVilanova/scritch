package com.scritch.app.admin

data class ModerationQueueViewState(
    val queue: List<ModerationQueueItem> = emptyList(),
    val isLoading: Boolean = true,
    val processingItems: Set<String> = emptySet(), // Set of "jamId/userId" being processed
    val error: String? = null,
)