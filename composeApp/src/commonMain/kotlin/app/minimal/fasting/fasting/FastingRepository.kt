package app.minimal.fasting.fasting

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val FASTING_PREFS_COLLECTION = "FastingPrefs"

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
}