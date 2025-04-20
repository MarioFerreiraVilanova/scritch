package com.scritch.app.analytics

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.analytics

class AnalyticsRepository {

    var lastScreen: String? = null

    fun onLogIn(userId: String) {
        Firebase.analytics.setUserId(userId)
    }

    fun onLogOut() {
        Firebase.analytics.setUserId(null)
    }

    fun onScreenView(
        screen: String,
    ) {
        if (screen != lastScreen){
            lastScreen = screen
            val formattedScreenName = screen.removePrefix("com.scritch.app.navigation")
            Firebase.analytics.logEvent(
                name = "screen_view",
                parameters = mapOf<String, Any>(
                    "firebase_screen" to formattedScreenName,
                ),
            )
        }
    }
}