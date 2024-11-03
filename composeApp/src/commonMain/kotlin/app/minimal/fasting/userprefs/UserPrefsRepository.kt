package app.minimal.fasting.userprefs

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

private const val USER_PREF_COLLECTION = "UserPref"

class UserPrefsRepository {
    suspend fun click (userId: String){
        val userPrefsSnapshot = Firebase.firestore
            .collection(USER_PREF_COLLECTION)
            .document(userId)
            .get()

        val userPrefs = if (userPrefsSnapshot.exists){
            userPrefsSnapshot.data<UserPrefs>()
        } else {
            UserPrefs()
        }
        println("Click!")
        Firebase.firestore.collection(USER_PREF_COLLECTION).document(userId).set(
           data = userPrefs.copy(clicks = userPrefs.clicks + 1)
        )
    }
}