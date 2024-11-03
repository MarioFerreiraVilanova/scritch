package app.minimal.fasting.fasting

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val FASTING_PREFS_COLLECTION = "FastingPrefs"

class FastingRepository {
    fun fastingPrefs (userId: String): Flow<FastingPrefs?> = Firebase.firestore
        .collection(FASTING_PREFS_COLLECTION)
        .document(userId)
        .snapshots
        .map { value: DocumentSnapshot ->
            if (value.exists) {
                value.data<FastingPrefs>()
            } else {
                null
            }
        }

    suspend fun click (userId: String){
        val userPrefsSnapshot = Firebase.firestore
            .collection(FASTING_PREFS_COLLECTION)
            .document(userId)
            .get()

        val fastingPrefs = if (userPrefsSnapshot.exists){
            userPrefsSnapshot.data<FastingPrefs>()
        } else {
            FastingPrefs()
        }
        println("Click!")
        Firebase.firestore.collection(FASTING_PREFS_COLLECTION).document(userId).set(fastingPrefs)
    }
}