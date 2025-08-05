package com.scritch.app.jam

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore

private const val JAM_COLLECTION = "weekly_jam"

class JamRepository {

    suspend fun loadCurrentJam(): JamDto? {
        val now = Timestamp.now()
        Firebase
            .firestore
            .collection(JAM_COLLECTION)
            .where {
                "startDate" lessThanOrEqualTo now
                "endDate" greaterThanOrEqualTo now
            }
            .get()
            .documents
            .firstOrNull()?.let { snapshot ->
                return JamDto(snapshot)
            }
        return null
    }
}