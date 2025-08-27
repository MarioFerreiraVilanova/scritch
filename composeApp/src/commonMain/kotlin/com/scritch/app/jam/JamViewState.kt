package com.scritch.app.jam

import com.scritch.app.prompt.PromptViewState
import kotlinx.datetime.LocalDateTime

data class JamViewState (
    val loadingState: LoadingState,
    val jamId: String?,
    val promptViewState: PromptViewState,
    val endDate: LocalDateTime?,
    val submissionState: SubmissionViewState,
    val dialog: JamScreenDialog?,
    val feedState: JamFeedState,
){
    companion object {
        val EMPTY = JamViewState(
            loadingState = LoadingState.INITIAL_LOADING,
            promptViewState = PromptViewState.EMPTY,
            jamId = null,
            endDate = null,
            submissionState = SubmissionViewState.NotSubmitted,
            dialog = null,
            feedState = JamFeedState.EMPTY,
        )
    }
}

enum class LoadingState {
    INITIAL_LOADING,
    LOADED,
    NO_JAM,
    REFRESHING,
}

enum class JamScreenDialog {
    EntryPreview,
    SubmissionDeleteConfirmation,
    ImageSourceSheet,
    GalleryPicker,
    ModerationStatus,
}