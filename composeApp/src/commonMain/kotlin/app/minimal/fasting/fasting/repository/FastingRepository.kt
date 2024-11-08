package app.minimal.fasting.fasting.repository

import app.minimal.fasting.common.toEpochMilliseconds
import app.minimal.fasting.common.toTimestamp
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.fromMilliseconds
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val FASTING_PREFS_COLLECTION = "FastingPrefs"
private const val FASTING_ENTRIES_COLLECTION = "Entries"

class FastingRepository {
    fun fastingPrefs(userId: String): Flow<FastingPrefsDto?> = Firebase.firestore
        .collection(FASTING_PREFS_COLLECTION)
        .document(userId)
        .snapshots
        .map { value: DocumentSnapshot ->
            if (value.exists) {
                value.data<FastingPrefsDto>()
            } else {
                null
            }
        }

    suspend fun savePrefs(
        userId: String,
        prefs: FastingPrefsDto,
    ) {
        Firebase.firestore.collection(FASTING_PREFS_COLLECTION).document(userId).set(prefs)
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun startFast(
        userId: String,
        startTime: LocalDateTime,
    ) {
        // Get the latest active fast
        val activeFast = Firebase.firestore
            .collection(FASTING_PREFS_COLLECTION)
            .document(userId)
            .collection(FASTING_ENTRIES_COLLECTION)
            .where {
                this.all(
                    "active".equalTo(true)
                )
            }
            .get()
            .documents
            .firstOrNull()

        // if it exists, edit the starting time
        if (activeFast != null) {
            val updatedFast = activeFast.data<FastingEntryDto>().copy(
                startTime = startTime.toTimestamp(),
            )
            Firebase.firestore
                .collection(FASTING_PREFS_COLLECTION)
                .document(userId)
                .collection(FASTING_ENTRIES_COLLECTION)
                .document(activeFast.id)
                .set(updatedFast)
        } else {
            val newFastEntry = FastingEntryDto(
                active = true,
                startTime = startTime.toTimestamp()
            )
            Firebase.firestore
                .collection(FASTING_PREFS_COLLECTION)
                .document(userId)
                .collection(FASTING_ENTRIES_COLLECTION)
                .document(Uuid.random().toString())
                .set(newFastEntry)
        }
        // if it doesn't, create a new active fast
    }
}