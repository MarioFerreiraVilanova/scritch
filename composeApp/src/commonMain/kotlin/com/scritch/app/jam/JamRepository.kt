package com.scritch.app.jam

import com.scritch.app.util.storageFileFromString
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FieldValue.Companion.serverTimestamp
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage
import dev.gitlive.firebase.storage.storageMetadata
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
    suspend fun submitWeeklyJamImageResumable(
        jamId: String,                 // e.g. "2025_33"
        uid: String,
        pathOrUri: String,             // platform string: "content://...", "file:///...", or "/abs/path.jpg"
        caption: String? = null,
        mimeType: String = "image/jpeg",
        onProgress: ((Int) -> Unit)? = null
    ): String {
        val ts = Clock.System.now().toEpochMilliseconds()
        val storagePath = "weekly_jam/$jamId/$uid/$ts.jpg"

        val ref = Firebase.storage.reference(storagePath)
        val file = storageFileFromString(pathOrUri)

        val meta = storageMetadata { contentType = mimeType }

        // Resumable upload with progress updates
        ref.putFileResumable(file, meta).collect { prog ->
            val transferred = prog.bytesTransferred.toLong()
            val total = prog.totalByteCount.toLong()
            val pct = if (total > 0L) ((transferred * 100L) / total).toInt() else 0
            onProgress?.invoke(pct.coerceIn(0, 99)) // we'll send 100 after success
        }

        val downloadUrl = ref.getDownloadUrl()

        // Write/merge submission metadata (doc id == uid → one per user per week)
        Firebase.firestore
            .collection("weekly_jam").document(jamId)
            .collection("submissions").document(uid)
            .set(
                mapOf(
                    "userId" to uid,
                    "storagePath" to storagePath,
                    "imageUrl" to downloadUrl,
                    "caption" to caption,
                    "createdAt" to serverTimestamp,
                ),
                merge = true
            )

        onProgress?.invoke(100)
        return downloadUrl
    }
}