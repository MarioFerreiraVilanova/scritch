package com.scritch.app.admin

data class AdminPanelViewState(
    val isGeneratingThumbnails: Boolean,
    val message: String?,
    val error: String?,
) {
    companion object {
        val EMPTY = AdminPanelViewState(
            isGeneratingThumbnails = false,
            message = null,
            error = null,
        )
    }
}