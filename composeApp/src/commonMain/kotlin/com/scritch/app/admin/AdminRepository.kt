package com.scritch.app.admin

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.functions.functions

class AdminRepository {
    private val firestore = Firebase.firestore
    private val functions = Firebase.functions

    /**
     * Check if the current user is an admin by looking up their ID in the admins collection
     */
    suspend fun isUserAdmin(userId: String): Boolean {
        return try {
            println("AdminRepository: Checking admin status for user ID: $userId")
            val adminDoc = firestore.collection("admins").document(userId).get()
            val exists = adminDoc.exists
            println("AdminRepository: User $userId admin status: $exists")
            exists
        } catch (e: Exception) {
            println("AdminRepository: Error checking admin status: ${e.message}")
            false
        }
    }

    /**
     * Get the moderation queue - submissions requiring manual review
     */
    suspend fun getModerationQueue(limit: Int = 50): ModerationQueueResponse {
        return try {
            println("AdminRepository: Calling getModerationQueue with limit: $limit")
            
            // Ensure user is authenticated
            val currentUser = Firebase.auth.currentUser
            println("AdminRepository: Current user: ${currentUser?.uid}")
            if (currentUser == null) {
                println("AdminRepository: No authenticated user")
                return ModerationQueueResponse(
                    success = false,
                    queue = emptyList(),
                    total = 0,
                )
            }
            
            // For now, let's just try the regular call
            val getModerationQueueFunction = functions.httpsCallable("getModerationQueue")
            val request = mapOf("limit" to limit)
            val result = getModerationQueueFunction(request)
            
            // Parse the result - you'll need to adapt this based on your Firebase Functions response
            val data = result.data<Map<String, Any>>()
            println("AdminRepository: Firebase function returned: $data")
            val success = data["success"] as? Boolean ?: false
            val queueData = data["queue"] as? List<Map<String, Any>> ?: emptyList()
            val total = (data["total"] as? Number)?.toInt() ?: 0
            println("AdminRepository: Parsed - success: $success, queue size: ${queueData.size}, total: $total")
            
            val queue = queueData.map { item ->
                ModerationQueueItem(
                    jamId = item["jamId"] as? String ?: "",
                    userId = item["userId"] as? String ?: "",
                    submissionId = item["submissionId"] as? String ?: "",
                    imageUrl = item["imageUrl"] as? String ?: "",
                    nickname = item["nickname"] as? String ?: "Unknown",
                    confirmedReports = (item["confirmedReports"] as? Number)?.toInt() ?: 0,
                    effectiveReports = (item["effectiveReports"] as? Number)?.toInt() ?: 0,
                    createdAt = (item["createdAt"] as? Number)?.toLong() ?: 0L,
                )
            }
            
            ModerationQueueResponse(
                success = success,
                queue = queue,
                total = total,
            )
        } catch (e: Exception) {
            println("AdminRepository: Error calling getModerationQueue: ${e.message}")
            e.printStackTrace()
            ModerationQueueResponse(
                success = false,
                queue = emptyList(),
                total = 0,
            )
        }
    }

    /**
     * Manually moderate a submission (approve/reject)
     */
    suspend fun moderateSubmission(
        jamId: String,
        userId: String,
        status: String,
        reason: String
    ): ModerateSubmissionResponse {
        return try {
            // Ensure user is authenticated
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
            
            val moderateFunction = functions.httpsCallable("moderateSubmissionManually")
            val request = mapOf(
                "jamId" to jamId,
                "userId" to userId,
                "status" to status,
                "reason" to reason
            )
            val result = moderateFunction(request)
            
            val data = result.data<Map<String, Any>>()
            ModerateSubmissionResponse(
                success = data["success"] as? Boolean ?: false,
                message = data["message"] as? String ?: "",
                jamId = data["jamId"] as? String ?: jamId,
                userId = data["userId"] as? String ?: userId,
                newStatus = data["newStatus"] as? String ?: status,
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
}