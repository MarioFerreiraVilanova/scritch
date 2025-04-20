package com.scritch.app.analytics

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.FirebaseAnalyticsEvents
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
                name = FirebaseAnalyticsEvents.SCREEN_VIEW,
                parameters = mapOf<String, Any>(
                    "firebase_screen" to formattedScreenName,
                ),
            )
        }
    }

    fun onPromptGenerated(
        topic: String?,
        medium: String?,
        support: String?,
        constraint: String?,
    ){
        val params = listOfNotNull(
            if (topic != null) "topic" to topic else null,
            if (medium != null) "medium" to medium else null,
            if (support != null) "support" to support else null,
            if (constraint != null) "constraint" to constraint else null,
        ).toMap()
        Firebase.analytics.logEvent(
            name = "promptGenerated",
            parameters = params,
        )
    }
}