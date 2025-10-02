package com.scritch.app.admin

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.functions.functions
import com.scritch.app.jam.data.SubmissionDto
import com.scritch.app.userprofile.UserProfileDto

class AdminRepository {
    private val firestore = Firebase.firestore
    private val functions = Firebase.functions

    /**
     * Check if the current user is an admin by looking up their ID in the admins collection
     */
    suspend fun isUserAdmin(userId: String): Boolean {
        return try {
            val adminDoc = firestore.collection("admins").document(userId).get()
            adminDoc.exists
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get the moderation queue - submissions requiring manual review (direct Firestore)
     */
    suspend fun getModerationQueue(limit: Int = 50): ModerationQueueResponse {
        return try {
            // Get all pending submissions across all jams using collection group query
            val pendingSubmissions = firestore.collectionGroup("submissions")
                .where { "status" equalTo "pending" }
                .orderBy("createdAt", Direction.DESCENDING)
                .limit(limit)
                .get()
            
            val queue = pendingSubmissions.documents.mapNotNull { doc ->
                val submission = doc.data<SubmissionDto>()
                val jamId = doc.reference.parent.parent?.id
                val userId = submission.userId
                val imageUrl = submission.imageUrl
                
                // Skip if mandatory fields are null
                if (jamId == null || userId == null || imageUrl == null) {
                    return@mapNotNull null
                }
                
                // Get user profile for display name
                val userProfile = try {
                    firestore.collection("user_profiles").document(userId).get()
                        .data<UserProfileDto>()
                } catch (e: Exception) {
                    null
                }
                
                ModerationQueueItem(
                    jamId = jamId,
                    userId = userId,
                    submissionId = doc.id,
                    imageUrl = imageUrl,
                    nickname = userProfile?.nickname ?: "Unknown",
                    confirmedReports = 0, // We can add this later if needed
                    effectiveReports = 0, // We can add this later if needed  
                    createdAt = submission.createdAt?.seconds?.times(1000) ?: 0L,
                )
            }
            
            ModerationQueueResponse(
                success = true,
                queue = queue,
                total = queue.size,
            )
        } catch (e: Exception) {
            ModerationQueueResponse(
                success = false,
                queue = emptyList(),
                total = 0,
            )
        }
    }

    /**
     * Manually moderate a submission (approve/reject) - direct Firestore update
     */
    suspend fun moderateSubmission(
        jamId: String,
        userId: String,
        status: String,
        reason: String
    ): ModerateSubmissionResponse {
        return try {
            // Validate status
            if (!listOf("approved", "rejected", "pending").contains(status)) {
                return ModerateSubmissionResponse(
                    success = false,
                    message = "Invalid status. Must be approved, rejected, or pending",
                    jamId = jamId,
                    userId = userId,
                    newStatus = status,
                )
            }
            
            val currentUser = Firebase.auth.currentUser
            if (currentUser == null) {
                return ModerateSubmissionResponse(
                    success = false,
                    message = "User not authenticated",
                    jamId = jamId,
                    userId = userId,
                    newStatus = status,
                )
            }
            
            // Update submission status directly in Firestore
            val submissionRef = firestore.collection("weekly_jam")
                .document(jamId)
                .collection("submissions")
                .document(userId)
            
            val submission = submissionRef.get()
            if (!submission.exists) {
                return ModerateSubmissionResponse(
                    success = false,
                    message = "Submission not found",
                    jamId = jamId,
                    userId = userId,
                    newStatus = status,
                )
            }
            
            // Update with moderation info
            submissionRef.update(
                mapOf(
                    "status" to status,
                    "moderatedBy" to currentUser.uid,
                    "moderatedAt" to FieldValue.serverTimestamp,
                    "moderationReason" to reason,
                    "autoModerated" to false,
                )
            )
            
            ModerateSubmissionResponse(
                success = true,
                message = "Submission status updated to $status",
                jamId = jamId,
                userId = userId,
                newStatus = status,
            )
        } catch (e: Exception) {
            ModerateSubmissionResponse(
                success = false,
                message = "Failed to moderate submission: ${e.message}",
                jamId = jamId,
                userId = userId,
                newStatus = status,
            )
        }
    }

    /**
     * Recalculates jam statistics using Firebase Cloud Functions
     */
    suspend fun recalculateJamStats(jamId: String): RecalculateStatsResponse {
        return try {
            val functionReference = functions.httpsCallable("recalculateJamStats")
            val result = functionReference(data = mapOf("jamId" to jamId))

            val data = result.data<RecalculateStatsResponse>()
            data.copy(message = "Stats recalculated successfully")
        } catch (e: Exception) {
            RecalculateStatsResponse(
                success = false,
                message = "Error: ${e.message}",
                jamId = jamId
            )
        }
    }

    /**
     * Batch generates thumbnails for submissions that don't have them using Firebase Cloud Functions
     */
    suspend fun batchGenerateThumbnails(): BatchThumbnailGenerationResponse {
        return try {
            val functionReference = functions.httpsCallable("batchGenerateThumbnails")
            val result = functionReference(data = emptyMap<String, Any>())

            val data = result.data<BatchThumbnailGenerationResponse>()
            data.copy(message = "Thumbnail batch processing completed successfully")
        } catch (e: Exception) {
            BatchThumbnailGenerationResponse(
                success = false,
                message = "Error: ${e.message}",
                processedCount = 0,
                errorCount = 0
            )
        }
    }
}