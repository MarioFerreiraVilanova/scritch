import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

admin.initializeApp();

const db = admin.firestore();

// Trust level constants
const TRUST_LEVELS = {
  NEW_USER: 0,
  TRUSTED: 100,
  MODERATOR: 500,
  ADMIN: 1000,
};

// Trust score calculation
async function getUserTrustScore(userId: string): Promise<number> {
  try {
    // Get user profile
    const userProfileDoc = await db.collection("user_profiles").doc(userId).get();
    if (!userProfileDoc.exists) {
      return TRUST_LEVELS.NEW_USER;
    }

    const userProfile = userProfileDoc.data()!;
    const accountAge = Date.now() - userProfile.createdAt.toMillis();
    const ageInDays = Math.floor(accountAge / (1000 * 60 * 60 * 24));

    // Base score
    let trustScore = 50;

    // Account age bonus (max 30 points)
    trustScore += Math.min(ageInDays, 30);

    // Count approved submissions across all jams
    const approvedSubmissions = await db.collectionGroup("submissions")
      .where("userId", "==", userId)
      .where("status", "==", "approved")
      .get();

    // +10 points per approved submission
    trustScore += approvedSubmissions.size * 10;

    // Count reports against user (negative score)
    const reportsAgainstUser = await db.collection("user_reports")
      .where("reportedUserId", "==", userId)
      .where("status", "==", "confirmed")
      .get();

    // -20 points per confirmed report
    trustScore -= reportsAgainstUser.size * 20;

    // Check if user is admin
    const adminDoc = await db.collection("admins").doc(userId).get();
    if (adminDoc.exists) {
      trustScore = TRUST_LEVELS.ADMIN;
    }

    return Math.max(0, trustScore);
  } catch (error) {
    console.error("Error calculating trust score:", error);
    return TRUST_LEVELS.NEW_USER;
  }
}

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
      // Determine initial status based on trust score
      const initialStatus = await determineInitialStatus(userId);

      // Update the submission with the determined status
      await snap.ref.update({
        status: initialStatus,
        autoModerated: true,
        moderatedAt: admin.firestore.FieldValue.serverTimestamp(),
        trustScore: await getUserTrustScore(userId),
      });

      console.log(`Submission ${snap.id} set to status: ${initialStatus}`);

      // If auto-approved, log for monitoring
      if (initialStatus === "approved") {
        console.log(`Auto-approved submission from trusted user ${userId}`);
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

// Function to get user trust score (for admin dashboard)
export const getUserTrust = functions.https.onCall(async (data, context) => {
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

// Admin function to pardon a user (reset their moderation status)
export const pardonUser = functions.https.onCall(async (data, context) => {
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