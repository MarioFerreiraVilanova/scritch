import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

const db = admin.firestore();

// Calculate effective reports considering time decay and good behavior
async function getEffectiveReportCount(userId: string): Promise<number> {
  try {
    // Only count reports from last 6 months (time decay)
    const sixMonthsAgo = new Date();
    sixMonthsAgo.setMonth(sixMonthsAgo.getMonth() - 6);

    const recentReports = await db.collection("user_reports")
      .where("reportedUserId", "==", userId)
      .where("status", "==", "confirmed")
      .where("createdAt", ">=", admin.firestore.Timestamp.fromDate(sixMonthsAgo))
      .get();

    let effectiveReports = recentReports.size;

    // Good behavior credit: every 10 approved submissions = -1 effective report
    const approvedSubmissions = await db.collectionGroup("submissions")
      .where("userId", "==", userId)
      .where("status", "==", "approved")
      .where("moderatedAt", ">=", admin.firestore.Timestamp.fromDate(sixMonthsAgo))
      .get();

    const goodBehaviorCredit = Math.floor(approvedSubmissions.size / 10);
    effectiveReports = Math.max(0, effectiveReports - goodBehaviorCredit);

    console.log(`User ${userId}: ${recentReports.size} recent reports, ${approvedSubmissions.size} approved submissions, ${goodBehaviorCredit} credits = ${effectiveReports} effective reports`);
    
    return effectiveReports;
  } catch (error) {
    console.error("Error calculating effective reports:", error);
    return 0; // Default to clean record on error
  }
}

// Determine initial moderation status
async function determineInitialStatus(userId: string): Promise<string> {
  try {
    // Check if user is admin (always trusted)
    const adminDoc = await db.collection("admins").doc(userId).get();
    if (adminDoc.exists) {
      console.log(`User ${userId} is admin - auto-approved`);
      return "approved";
    }

    // Calculate effective report count with time decay and good behavior credits
    const effectiveReports = await getEffectiveReportCount(userId);

    // Auto-approve unless user has 2+ effective reports
    if (effectiveReports >= 2) {
      console.log(`User ${userId} requires manual review due to ${effectiveReports} effective reports`);
      return "pending";  // Manual review for users with multiple effective reports
    } else {
      console.log(`User ${userId} auto-approved (${effectiveReports} effective reports)`);
      return "approved"; // Auto-approve users with clean/improving record
    }

  } catch (error) {
    console.error("Error determining status:", error);
    // Default to auto-approve on error (fail open for better UX)
    return "approved";
  }
}

// Main moderation function - triggers when a new submission is created
export const moderateSubmission = functions.firestore
  .document("weekly_jam/{jamId}/submissions/{userId}")
  .onCreate(async (snap, context) => {
    const submission = snap.data();
    const userId = context.params.userId;

    console.log(`New submission from user ${userId}`);

    try {
      // Determine initial status based on user history
      const initialStatus = await determineInitialStatus(userId);

      // Update the submission with the determined status
      await snap.ref.update({
        status: initialStatus,
        autoModerated: true,
        moderatedAt: admin.firestore.FieldValue.serverTimestamp(),
        effectiveReports: await getEffectiveReportCount(userId),
      });

      console.log(`Submission ${snap.id} set to status: ${initialStatus}`);

      // If auto-approved, log for monitoring
      if (initialStatus === "approved") {
        console.log(`Auto-approved submission from user ${userId}`);
      }

    } catch (error) {
      console.error("Error in moderation:", error);
      
      // Fallback to pending on error
      await snap.ref.update({
        status: "pending",
        moderationError: error.message,
        moderatedAt: admin.firestore.FieldValue.serverTimestamp(),
      });
    }
  });

// Function to handle user reports
export const reportUser = functions.https.onCall(async (data, context) => {
  // Verify user is authenticated
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "User must be authenticated");
  }

  const { reportedUserId, submissionId, reason } = data;
  const reporterId = context.auth.uid;

  // Prevent self-reporting
  if (reporterId === reportedUserId) {
    throw new functions.https.HttpsError("invalid-argument", "Cannot report yourself");
  }

  try {
    // Create report document
    await db.collection("user_reports").add({
      reporterId,
      reportedUserId,
      submissionId,
      reason,
      status: "pending",
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    // Count reports for this submission
    const submissionReports = await db.collection("user_reports")
      .where("submissionId", "==", submissionId)
      .where("status", "in", ["pending", "confirmed"])
      .get();

    // Auto-confirm report if 3+ people report the same submission
    if (submissionReports.size >= 3) {
      // Mark all reports for this submission as confirmed
      const batch = db.batch();
      submissionReports.docs.forEach(doc => {
        batch.update(doc.ref, { status: "confirmed" });
      });
      await batch.commit();

      // Hide the submission
      const submissionRef = db.doc(`weekly_jam/${submissionId.split("/")[0]}/submissions/${reportedUserId}`);
      await submissionRef.update({
        status: "rejected",
        rejectionReason: "multiple_community_reports",
        rejectedAt: admin.firestore.FieldValue.serverTimestamp(),
      });

      console.log(`Auto-rejected submission ${submissionId} due to ${submissionReports.size} reports`);
      
      // Check if user now needs manual review for future submissions
      const totalConfirmedReports = await db.collection("user_reports")
        .where("reportedUserId", "==", reportedUserId)
        .where("status", "==", "confirmed")
        .get();

      if (totalConfirmedReports.size >= 2) {
        console.log(`User ${reportedUserId} now requires manual review for future submissions`);
      }
    }

    return { success: true, message: "Report submitted successfully" };

  } catch (error) {
    console.error("Error submitting report:", error);
    throw new functions.https.HttpsError("internal", "Failed to submit report");
  }
});

// Function to get user moderation status (for admin dashboard)
export const getUserModerationStatus = functions.https.onCall(async (data, context) => {
  // Verify admin access
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "User must be authenticated");
  }

  const adminDoc = await db.collection("admins").doc(context.auth.uid).get();
  if (!adminDoc.exists) {
    throw new functions.https.HttpsError("permission-denied", "Admin access required");
  }

  const { userId } = data;
  const effectiveReports = await getEffectiveReportCount(userId);

  return {
    userId,
    effectiveReports,
    needsReview: effectiveReports >= 2,
    status: effectiveReports >= 2 ? "manual_review" : "auto_approved",
  };
});