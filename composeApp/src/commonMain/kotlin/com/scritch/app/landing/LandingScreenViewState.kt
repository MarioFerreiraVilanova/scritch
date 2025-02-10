package com.scritch.app.landing

data class LandingScreenViewState(
    val events: List<LandingScreenEvent> = emptyList()
)

sealed class LandingScreenEvent {
    data object NavigateToHome: LandingScreenEvent()
    data object NavigateToWizard: LandingScreenEvent()
}
