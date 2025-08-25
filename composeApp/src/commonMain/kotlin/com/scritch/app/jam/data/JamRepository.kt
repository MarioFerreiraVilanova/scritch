package com.scritch.app.jam.data

import com.scritch.app.jam.Cursor
import com.scritch.app.jam.Page
import com.scritch.app.jam.data.SubmissionDto
import com.scritch.app.util.storageFileFromString
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage
import dev.gitlive.firebase.storage.storageMetadata
import kotlin.time.ExperimentalTime

private const val JAM_COLLECTION = "weekly_jam"
private const val SUBMISSIONS_COLLECTION = "submissions"

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
    ): SubmissionDto {
        val storagePath = "$JAM_COLLECTION/$jamId/$uid.jpg"

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
        val submission = SubmissionDto(
            userId = uid,
            storagePath = storagePath,
            imageUrl = downloadUrl,
            caption = caption,
            createdAt = null,
        )
        Firebase.firestore
            .collection(JAM_COLLECTION).document(jamId)
            .collection("submissions").document(uid)
            .set(
                submission,
                merge = false,
            )

        Firebase.firestore
            .collection(JAM_COLLECTION).document(jamId)
            .collection("submissions").document(uid)
            .update(
                "createdAt" to FieldValue.serverTimestamp,
            )

        onProgress?.invoke(100)
        return submission
    }

    /**
     * Deletes the user's weekly jam submission:
     * - Storage: weekly_jam/{jamId}/{uid}.jpg
     * - Firestore: /weekly_jam/{jamId}/submissions/{uid}
     *
     * Works from shared (common) code with GitLive SDK.
     * TODO this code should just delete the document in firebase, then a trigger should delete the file from storage
     */
    suspend fun deleteWeeklyJamSubmission(
        jamId: String,
        uid: String
    ) {
        val storagePath = "$JAM_COLLECTION/$jamId/$uid.jpg"
        val storageRef = Firebase.storage.reference(storagePath)
        val docRef = Firebase.firestore
            .collection(JAM_COLLECTION).document(jamId)
            .collection(SUBMISSIONS_COLLECTION).document(uid)

        var storageOk = false
        var docOk = false
        var storageErr: Throwable? = null
        var docErr: Throwable? = null

        // 1) Delete Storage object (ignore not-found)
        try {
            storageRef.delete()
            storageOk = true
        } catch (t: Throwable) {
            // Many SDKs surface 404 as an exception; treat it as fine.
            val msg = t.message?.lowercase() ?: ""
            if ("object does not exist" in msg || "not found" in msg || "404" in msg) {
                storageOk = true
            } else {
                storageErr = t
            }
        }

        // 2) Delete Firestore doc (ignore not-found)
        try {
            docRef.delete()
            docOk = true
        } catch (t: Throwable) {
            val msg = t.message?.lowercase() ?: ""
            if ("not found" in msg) {
                docOk = true
            } else {
                docErr = t
            }
        }

        // If both failed for unexpected reasons, surface a helpful error
        if (!storageOk && !docOk) {
            throw IllegalStateException(
                buildString {
                    append("Failed to delete submission. ")
                    storageErr?.let { append("Storage error: ${it.message}. ") }
                    docErr?.let { append("Firestore error: ${it.message}.") }
                }
            )
        }
    }

    suspend fun getUserSubmission(
        jamId: String,
        uid: String,
    ): SubmissionDto? {
        val docRef = Firebase.firestore
            .collection(JAM_COLLECTION)
            .document(jamId)
            .collection(SUBMISSIONS_COLLECTION)
            .document(uid)

        val snapshot = docRef.get()
        return if (snapshot.exists) {
            snapshot.data<SubmissionDto>()
        } else {
            null
        }
    }

    suspend fun getSubmissions(
        userId: String,
        jamId: String,
        cursor: Cursor? = null,
        pageSize: Int = 24,
    ): Page<SubmissionDto> {
        val query = Firebase.firestore
            .collection(JAM_COLLECTION)
            .document(jamId)
            .collection(SUBMISSIONS_COLLECTION)
            .where { "userId" notEqualTo userId }
            .orderBy("createdAt", Direction.DESCENDING)
            .limit(pageSize)

        val querySnapshot = if (cursor == null) {
            query.get().documents
        } else {
            query.startAfter(cursor.lastDocId).get().documents
        }

        val pageItems = querySnapshot.map { snapshot ->
            snapshot.data<SubmissionDto>()
        }

        return Page(
            items = pageItems,
            cursor = pageItems.lastOrNull()?.let { lastItem ->
                Cursor(
                    lastDocId = lastItem.userId ?: return@let null,
                )
            },
            endReached = pageItems.size < pageSize,
        )
    }
}