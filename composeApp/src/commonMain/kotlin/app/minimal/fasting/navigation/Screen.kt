package app.minimal.fasting.navigation

import kotlinx.serialization.Serializable

// Main sections of the app
@Serializable object Unauthenticated {
    @Serializable object LandingScreen
}
@Serializable object Authenticated {
    @Serializable object FastingStatus
    @Serializable object FastingWizard
    @Serializable object StartFasting
}