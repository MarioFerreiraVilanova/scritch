package com.scritch.app.reporting

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.FieldValue

class ReportRepository {
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    suspend fun submitReport(
        reportedUserId: String,
        submissionId: String,
        jamId: String,
        reason: String
    ): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.failure(Exception("User not authenticated"))
            
            // Prevent users from reporting themselves
            if (currentUser.uid == reportedUserId) {
                return Result.failure(Exception("Cannot report your own submission"))
            }

            // Check if user has already reported this submission
            val existingReport = firestore.collection("user_reports")
                .where { 
                    "reporterId" equalTo currentUser.uid
                    "submissionId" equalTo submissionId
                }
                .get()

            if (existingReport.documents.isNotEmpty()) {
                return Result.failure(Exception("You have already reported this submission"))
            }

            // Create the report
            val report = UserReport(
                reporterId = currentUser.uid,
                reportedUserId = reportedUserId,
                submissionId = submissionId,
                jamId = jamId,
                reason = reason
            )

            firestore.collection("user_reports").add(report)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}