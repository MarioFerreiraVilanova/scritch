package com.scritch.app.jam

import com.scritch.app.prompt.PromptViewState
import io.github.ismoy.imagepickerkmp.CameraPhotoHandler
import kotlinx.datetime.LocalDateTime

data class JamViewState (
    val loadingState: LoadingState,
    val jamId: String?,
    val promptViewState: PromptViewState,
    val endDate: LocalDateTime?,
    val showCamera: Boolean,
    val submissionState: SubmissionViewState,
    val dialog: JamScreenDialog?,
){
    companion object {
        val EMPTY = JamViewState(
            loadingState = LoadingState.LOADING,
            promptViewState = PromptViewState.EMPTY,
            jamId = null,
            endDate = null,
            showCamera = false,
            submissionState = SubmissionViewState.NotSubmitted,
            dialog = null,
        )
    }
}

enum class LoadingState {
    LOADING,
    LOADED,
    NO_JAM,
    REFRESHING,
}

enum class JamScreenDialog {
    SubmissionPreview,
    SubmissionDeleteConfirmation,
}