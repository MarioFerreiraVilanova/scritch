package com.scritch.app.jam.data

import com.scritch.app.jam.Cursor
import com.scritch.app.jam.Page
import com.scritch.app.userprofile.UserProfileRepository
import com.scritch.app.util.storageFileFromString
import com.scritch.app.util.uploadFileToStorage
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.fromMilliseconds
import dev.gitlive.firebase.storage.storage
import dev.gitlive.firebase.storage.storageMetadata
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

private const val JAM_COLLECTION = "weekly_jam"
private const val SUBMISSIONS_COLLECTION = "submissions"

class JamRepository(
    private val userProfileRepository: UserProfileRepository,
) {

    fun getCurrentJamFlow(): Flow<JamDto?> {
        val now = Timestamp.now()
        return Firebase.firestore
            .collection(JAM_COLLECTION)
            .where { "startDate" lessThanOrEqualTo now }
            .where { "endDate" greaterThanOrEqualTo now }
            .orderBy("startDate", Direction.DESCENDING)
            .limit(1)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.firstOrNull()?.let { snapshot ->
                    JamDto(snapshot)
                }
            }
    }

    suspend fun loadCurrentJam(): JamDto? {
        val now = Timestamp.now()
        return Firebase.firestore
            .collection(JAM_COLLECTION)
            .where { "startDate" lessThanOrEqualTo now }
            .where { "endDate" greaterThanOrEqualTo now }
            .orderBy("startDate", Direction.DESCENDING)
            .limit(1)
            .get()
            .documents
            .firstOrNull()?.let { snapshot ->
                JamDto(snapshot)
            }
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
        println("JamRepository: Starting submitWeeklyJamImageResumable")
        println("JamRepository: jamId=$jamId, uid=$uid, pathOrUri=$pathOrUri")
        deleteWeeklyJamSubmission(
            jamId = jamId,
            uid = uid,
        )

        val storagePath = "$JAM_COLLECTION/$jamId/$uid.jpg"
        println("JamRepository: Storage path: $storagePath")

        val ref = Firebase.storage.reference(storagePath)
        val file = storageFileFromString(pathOrUri)
        println("JamRepository: Got storage file reference")

        val meta = storageMetadata { contentType = mimeType }
        println("JamRepository: Starting Firebase Storage upload...")

        // Use platform-specific upload (Android: resumable with progress, iOS: simple for reliability)
        uploadFileToStorage(
            ref = ref,
            file = file,
            meta = meta,
            onProgress = onProgress
        )
        println("JamRepository: Upload completed successfully")

        println("JamRepository: Getting download URL...")
        val downloadUrl = try {
            // Sometimes there's a brief delay before download URL is available
            kotlinx.coroutines.delay(1000) // Wait 1 second
            ref.getDownloadUrl()
        } catch (e: Exception) {
            println("JamRepository: Failed to get download URL: ${e.message}")
            throw e
        }
        println("JamRepository: Got download URL: $downloadUrl")

        // Get user's nickname
        println("JamRepository: Getting user profile for uid: $uid")
        val userProfile = userProfileRepository.userProfile(uid)
        val nickname =
            userProfile?.nickname ?: throw Exception("User must have a nickname to submit")
        println("JamRepository: Got nickname: $nickname")

        // Write/merge submission metadata (doc id == uid → one per user per week)
        val submission = SubmissionDto(
            userId = uid,
            storagePath = storagePath,
            imageUrl = downloadUrl,
            caption = caption,
            createdAt = Timestamp.now(),
            nickname = nickname,
        )

        val data = mapOf(
            "userId" to uid,
            "storagePath" to storagePath,
            "imageUrl" to downloadUrl,
            "caption" to caption,
            "status" to "pending",
            "createdAt" to FieldValue.serverTimestamp,
            "nickname" to nickname
        )

        println("JamRepository: Saving submission to Firestore...")
        try {
            Firebase.firestore
                .collection(JAM_COLLECTION).document(jamId)
                .collection("submissions").document(uid)
                .set(data)
            println("JamRepository: Successfully saved submission to Firestore")
        } catch (e: Exception) {
            println("JamRepository: Failed to save to Firestore: ${e.message}")
            throw e
        }

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
        pageSize: Int = 2,
    ): Page<SubmissionDto> {
        val query = Firebase.firestore
            .collection(JAM_COLLECTION)
            .document(jamId)
            .collection(SUBMISSIONS_COLLECTION)
            .where { "status" equalTo "approved" }
            .where { "userId" notEqualTo userId }
            .orderBy("createdAt", Direction.DESCENDING)

        val finalQuery = if (cursor == null) {
            query
        } else {
            query.startAfter(cursor.lastDoc)
        }

        val documents = finalQuery.limit(pageSize).get().documents

        val pageItems = documents.map { snapshot ->
            snapshot.data<SubmissionDto>()
        }

        return Page(
            items = pageItems,
            cursor = documents.lastOrNull()?.let { Cursor(it) },
            endReached = pageItems.size < pageSize,
        )
    }

    @OptIn(ExperimentalTime::class)
    suspend fun createJam(
        jamId: String,
        startDate: LocalDate,
        endDate: LocalDate,
        topicId: String? = null,
        mediumId: String? = null,
        supportId: String? = null,
        constraintId: String? = null,
    ) {
        // Convert LocalDate to Firebase Timestamps using the proper companion method
        val startInstant = startDate.atStartOfDayIn(TimeZone.currentSystemDefault())
        val endInstant = endDate.atStartOfDayIn(TimeZone.currentSystemDefault()).plus(1.days)
        
        val startTimestamp = Timestamp.Companion.fromMilliseconds(startInstant.toEpochMilliseconds().toDouble())
        val endTimestamp = Timestamp.Companion.fromMilliseconds(endInstant.toEpochMilliseconds().toDouble())

        val jamData = buildMap<String, Any?> {
            put("startDate", startTimestamp)
            put("endDate", endTimestamp)
            topicId?.let { put("topic", it) }
            mediumId?.let { put("medium", it) }
            supportId?.let { put("support", it) }
            constraintId?.let { put("constraint", it) }
        }

        Firebase.firestore
            .collection(JAM_COLLECTION)
            .document(jamId)
            .set(jamData)
    }

    suspend fun getAllJams(
        cursor: Cursor? = null,
        pageSize: Int = 20,
    ): Page<JamDto> {
        val query = Firebase.firestore
            .collection(JAM_COLLECTION)
            .orderBy("endDate", Direction.DESCENDING)

        val finalQuery = if (cursor == null) {
            query
        } else {
            query.startAfter(cursor.lastDoc)
        }

        val documents = finalQuery.limit(pageSize).get().documents

        val pageItems = documents.map { snapshot ->
            JamDto(snapshot)
        }

        return Page(
            items = pageItems,
            cursor = documents.lastOrNull()?.let { Cursor(it) },
            endReached = pageItems.size < pageSize,
        )
    }

    suspend fun getJam(jamId: String): JamDto? {
        return Firebase.firestore
            .collection(JAM_COLLECTION)
            .document(jamId)
            .get()
            .takeIf { it.exists }
            ?.let { JamDto(it) }
    }

    @OptIn(ExperimentalTime::class)
    suspend fun updateJam(
        jamId: String,
        startDate: LocalDate,
        endDate: LocalDate,
        topicId: String? = null,
        mediumId: String? = null,
        supportId: String? = null,
        constraintId: String? = null,
    ) {
        val startInstant = startDate.atStartOfDayIn(TimeZone.currentSystemDefault())
        val endInstant = endDate.atStartOfDayIn(TimeZone.currentSystemDefault()).plus(1.days)

        val startTimestamp = Timestamp.Companion.fromMilliseconds(startInstant.toEpochMilliseconds().toDouble())
        val endTimestamp = Timestamp.Companion.fromMilliseconds(endInstant.toEpochMilliseconds().toDouble())

        val jamData = buildMap<String, Any?> {
            put("startDate", startTimestamp)
            put("endDate", endTimestamp)
            topicId?.let { put("topic", it) }
            mediumId?.let { put("medium", it) }
            supportId?.let { put("support", it) }
            constraintId?.let { put("constraint", it) }
        }

        Firebase.firestore
            .collection(JAM_COLLECTION)
            .document(jamId)
            .update(jamData)
    }

    suspend fun deleteJam(jamId: String) {
        Firebase.firestore
            .collection(JAM_COLLECTION)
            .document(jamId)
            .delete()
    }

    suspend fun getPastJams(
        cursor: Cursor? = null,
        pageSize: Int = 20,
    ): Page<JamDto> {
        val now = Timestamp.now()
        val query = Firebase.firestore
            .collection(JAM_COLLECTION)
            .where { "endDate" lessThan now }
            .orderBy("endDate", Direction.DESCENDING)

        val finalQuery = if (cursor == null) {
            query
        } else {
            query.startAfter(cursor.lastDoc)
        }

        val documents = finalQuery.limit(pageSize).get().documents

        val pageItems = documents.map { snapshot ->
            JamDto(snapshot)
        }

        return Page(
            items = pageItems,
            cursor = documents.lastOrNull()?.let { Cursor(it) },
            endReached = pageItems.size < pageSize,
        )
    }

    fun userParticipatedInJam(jam: JamDto, userId: String): Boolean {
        return jam.participants.contains(userId)
    }
}