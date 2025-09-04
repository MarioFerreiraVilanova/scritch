package com.scritch.app.jam

import com.scritch.app.jam.data.JamStatus
import com.scritch.app.prompt.PromptViewState
import kotlinx.datetime.LocalDateTime

data class JamViewState (
    val loadingState: LoadingState,
    val jamId: String?,
    val promptViewState: PromptViewState,
    val endDate: LocalDateTime?,
    val jamStatus: JamStatus?,
    val submissionState: SubmissionViewState,
    val dialog: JamScreenDialog?,
    val feedState: JamFeedState,
    val showContributions: Boolean,
){
    companion object {
        val EMPTY = JamViewState(
            loadingState = LoadingState.INITIAL_LOADING,
            promptViewState = PromptViewState.EMPTY,
            jamId = null,
            endDate = null,
            jamStatus = null,
            submissionState = SubmissionViewState.NotSubmitted,
            dialog = null,
            feedState = JamFeedState.EMPTY,
            showContributions = false,
        )
    }
}

enum class LoadingState {
    INITIAL_LOADING,
    LOADED,
    NO_JAM,
    REFRESHING,
}

sealed class JamScreenDialog {
    data class EntryPreview(
        val imageUrl: String,
        val isUserSubmission: Boolean,
        val moderationStatus: ModerationStatus? = null
    ) : JamScreenDialog()
    data object SubmissionDeleteConfirmation : JamScreenDialog()
    data object ImageSourceSheet : JamScreenDialog()
    data object GalleryPicker : JamScreenDialog()
    data object ModerationStatusExplanation : JamScreenDialog()
}