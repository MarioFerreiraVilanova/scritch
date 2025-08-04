package com.scritch.app.jam

import com.scritch.app.prompt.PromptViewState
import kotlinx.datetime.LocalDateTime

data class JamViewState (
    val loadingState: LoadingState,
    val promptViewState: PromptViewState,
    val endDate: LocalDateTime?,
){
    companion object {
        val EMPTY = JamViewState(
            loadingState = LoadingState.LOADING,
            promptViewState = PromptViewState.EMPTY,
            endDate = null,
        )
    }
}

enum class LoadingState {
    LOADING,
    LOADED,
    NO_JAM,
    REFRESHING,
}