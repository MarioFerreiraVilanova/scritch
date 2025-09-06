import * as functions from "firebase-functions/v1";
import * as admin from "firebase-admin";

const db = admin.firestore();

// Admin function to pardon a user (reset their moderation status)
export const pardonUser = functions.https.onCall(async (data: any, context: any) => {
  // Verify admin access
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "User must be authenticated");
  }

  const adminDoc = await db.collection("admins").doc(context.auth.uid).get();
  if (!adminDoc.exists) {
    throw new functions.https.HttpsError("permission-denied", "Admin access required");
  }

  const { userId, reason } = data;

  try {
    // Mark all confirmed reports against this user as "pardoned"
    const confirmedReports = await db.collection("user_reports")
      .where("reportedUserId", "==", userId)
      .where("status", "==", "confirmed")
      .get();

    const batch = db.batch();
    confirmedReports.docs.forEach(doc => {
      batch.update(doc.ref, { 
        status: "pardoned",
        pardonedBy: context.auth!.uid,
        pardonedAt: admin.firestore.FieldValue.serverTimestamp(),
        pardonReason: reason || "Admin pardon",
      });
    });
    await batch.commit();

    console.log(`Admin ${context.auth.uid} pardoned user ${userId} (${confirmedReports.size} reports cleared)`);

    return {
      success: true,
      message: `Pardoned user ${userId} - cleared ${confirmedReports.size} reports`,
      reportsCleared: confirmedReports.size,
    };

  } catch (error) {
    console.error("Error pardoning user:", error);
    throw new functions.https.HttpsError("internal", "Failed to pardon user");
  }
});

// Admin function to manually moderate a submission
export const moderateSubmissionManually = functions.https.onCall(async (data: any, context: any) => {
  // Verify admin access
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "User must be authenticated");
  }

  const adminDoc = await db.collection("admins").doc(context.auth.uid).get();
  if (!adminDoc.exists) {
    throw new functions.https.HttpsError("permission-denied", "Admin access required");
  }

  const { jamId, userId, status, reason } = data;

  // Validate status
  if (!["approved", "rejected", "pending"].includes(status)) {
    throw new functions.https.HttpsError("invalid-argument", "Invalid status. Must be approved, rejected, or pending");
  }

  try {
    const submissionRef = db.doc(`weekly_jam/${jamId}/submissions/${userId}`);
    const submission = await submissionRef.get();

    if (!submission.exists) {
      throw new functions.https.HttpsError("not-found", "Submission not found");
    }

    // Update submission status
    await submissionRef.update({
      status,
      moderatedBy: context.auth.uid,
      moderatedAt: admin.firestore.FieldValue.serverTimestamp(),
      moderationReason: reason || "Admin override",
      autoModerated: false,
    });

    console.log(`Admin ${context.auth.uid} manually set submission ${jamId}/${userId} to ${status}`);

    return {
      success: true,
      message: `Submission status updated to ${status}`,
      jamId,
      userId,
      newStatus: status,
    };

  } catch (error) {
    console.error("Error in manual moderation:", error);
    throw new functions.https.HttpsError("internal", "Failed to moderate submission");
  }
});

// Admin function to get moderation queue (submissions requiring review)
export const getModerationQueue = functions.https.onCall(async (data: any, context: any) => {
  // Verify admin access
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "User must be authenticated");
  }

  const adminDoc = await db.collection("admins").doc(context.auth.uid).get();
  if (!adminDoc.exists) {
    throw new functions.https.HttpsError("permission-denied", "Admin access required");
  }

  const { limit = 50 } = data;

  try {
    // Get all pending submissions across all jams
    const pendingSubmissions = await db.collectionGroup("submissions")
      .where("status", "==", "pending")
      .orderBy("createdAt", "desc")
      .limit(limit)
      .get();

    const queue = await Promise.all(
      pendingSubmissions.docs.map(async (doc) => {
        const submission = doc.data();
        const submissionId = doc.id;
        const jamId = doc.ref.parent.parent!.id;

        // Get user profile for display
        const userProfile = await db.collection("user_profiles").doc(submission.userId).get();
        const nickname = userProfile.exists ? userProfile.data()?.nickname : "Unknown";

        // Get recent report count
        const recentReports = await db.collection("user_reports")
          .where("reportedUserId", "==", submission.userId)
          .where("status", "==", "confirmed")
          .limit(10)
          .get();

        return {
          jamId,
          userId: submission.userId,
          submissionId,
          imageUrl: submission.imageUrl,
          createdAt: submission.createdAt,
          nickname,
          confirmedReports: recentReports.size,
          autoModerated: submission.autoModerated || false,
          effectiveReports: submission.effectiveReports || 0,
        };
      })
    );

    return {
      success: true,
      queue,
      total: queue.length,
    };

  } catch (error) {
    console.error("Error getting moderation queue:", error);
    throw new functions.https.HttpsError("internal", "Failed to get moderation queue");
  }
});