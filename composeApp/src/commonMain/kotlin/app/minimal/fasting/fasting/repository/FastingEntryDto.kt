package app.minimal.fasting.fasting.repository

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class FastingEntryDto(
    val startTime: Timestamp,
    val active: Boolean,
)
