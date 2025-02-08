package com.scritch.app.navigation

import kotlinx.serialization.Serializable

// Main sections of the app
@Serializable object Unauthenticated {
    @Serializable object LandingScreen
}
@Serializable object Authenticated {
    @Serializable object Home
}