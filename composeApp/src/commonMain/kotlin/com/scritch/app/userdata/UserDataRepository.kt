package com.scritch.app.userdata

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

private const val USER_DATA_COLLECTION = "user_data"

class UserDataRepository {

    suspend fun userData(userId: String): UserData?{
        val doc = Firebase.firestore
            .collection(USER_DATA_COLLECTION)
            .document(userId)
            .get()

        return if (!doc.exists){
            initialiseUserDocument(userId = userId)
            UserData.fromDto(UserDataDto(doc.reference.get()))
        } else {
            UserData.fromDto(UserDataDto(doc))
        }
    }

    private suspend fun initialiseUserDocument(userId: String){
        Firebase.firestore
            .collection(USER_DATA_COLLECTION)
            .document(userId)
            .set(UserDataDto.initial().asMap())
    }
}