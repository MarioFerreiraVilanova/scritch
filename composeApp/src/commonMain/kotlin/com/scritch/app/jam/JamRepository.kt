package com.scritch.app.jam

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

private const val JAM_COLLECTION = "weekly_jam"

class JamRepository {

    suspend fun loadLatestJam(): JamDto? {
        Firebase
            .firestore
            .collection(JAM_COLLECTION)
            .get()
            .documents
            .firstOrNull()?.let { snapshot ->
                return JamDto(snapshot)
            }
        return null
    }
}