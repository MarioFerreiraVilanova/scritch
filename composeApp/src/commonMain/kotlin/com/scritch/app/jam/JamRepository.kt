package com.scritch.app.jam

import com.scritch.app.util.readBytesFromUriString
import com.scritch.app.util.uploadWithOptionalProgress
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FieldValue.Companion.serverTimestamp
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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

    @OptIn(ExperimentalTime::class)
    suspend fun submitWeeklyJamImage(
        jamId: String,               // "2025_33" etc.
        uid: String,
        imageUriString: String,
        caption: String? = null,
        mimeType: String = "image/jpeg",
        onProgress: ((Int) -> Unit)? = null
    ): String /* downloadUrl */ {
        // 0) Read file bytes (platform actual)
        val bytes = readBytesFromUriString(imageUriString)

        val ts = Clock.System.now().toEpochMilliseconds()
        val storagePath = "weekly_jam/$jamId/$uid/$ts.jpg"

        // 1) Upload (platform actual can emit granular progress; common fallback will just do 0%→100%)
        onProgress?.invoke(0)
        val downloadUrl = uploadWithOptionalProgress(storagePath, bytes, mimeType, onProgress)

        // 2) Write/merge submission metadata (doc id == uid → one submission per week)
        val submissionRef = Firebase.firestore
            .collection("weekly_jam").document(jamId)
            .collection("submissions").document(uid)

        submissionRef.set(
            mapOf(
                "userId" to uid,
                "storagePath" to storagePath,
                "imageUrl" to downloadUrl,
                "caption" to caption,
                "createdAt" to serverTimestamp()
            ),
            merge = true
        )

        onProgress?.invoke(100)
        return downloadUrl
    }
}