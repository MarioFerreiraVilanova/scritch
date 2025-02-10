package com.scritch.app.landing

data class LandingViewState(
    val events: List<LandingEvent> = emptyList()
)

enum class LandingEvent {
    GoHome,
    GoToWizard,
}
