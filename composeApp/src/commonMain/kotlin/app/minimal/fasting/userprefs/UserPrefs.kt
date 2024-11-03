package app.minimal.fasting.userprefs

import kotlinx.serialization.Serializable

@Serializable
data class UserPrefs(
    val clicks: Int = 0,
)
