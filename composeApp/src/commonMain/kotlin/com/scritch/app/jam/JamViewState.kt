package com.scritch.app.jam

import com.scritch.app.prompt.PromptViewState
import io.github.ismoy.imagepickerkmp.CameraPhotoHandler
import kotlinx.datetime.LocalDateTime

data class JamViewState (
    val loadingState: LoadingState,
    val promptViewState: PromptViewState,
    val endDate: LocalDateTime?,
    val showCamera: Boolean,
    val submission: CameraPhotoHandler.PhotoResult?,
){
    companion object {
        val EMPTY = JamViewState(
            loadingState = LoadingState.LOADING,
            promptViewState = PromptViewState.EMPTY,
            endDate = null,
            showCamera = false,
            submission = null,
        )
    }
}

enum class LoadingState {
    LOADING,
    LOADED,
    NO_JAM,
    REFRESHING,
}