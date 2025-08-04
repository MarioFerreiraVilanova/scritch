package com.scritch.app.jam

import com.scritch.app.prompt.PromptViewState

data class JamViewState (
    val loadingState: LoadingState,
    val promptViewState: PromptViewState,
)

enum class LoadingState {
    LOADING,
    LOADED,
    NO_JAM,
    REFRESHING,
}