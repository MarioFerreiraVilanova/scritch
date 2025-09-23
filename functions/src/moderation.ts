import { onDocumentCreated } from "firebase-functions/v2/firestore";
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
export const moderateSubmission = onDocumentCreated(
  "weekly_jam/{jamId}/submissions/{userId}",
  async (event) => {
    const snap = event.data;
    const userId = event.params.userId;

    console.log(`New submission from user ${userId}`);

    try {
      // Determine initial status based on user history
      const initialStatus = await determineInitialStatus(userId);

      // Update the submission with the determined status
      await snap!.ref.update({
        status: initialStatus,
        autoModerated: true,
        moderatedAt: admin.firestore.FieldValue.serverTimestamp(),
        effectiveReports: await getEffectiveReportCount(userId),
      });

      console.log(`Submission ${snap!.id} set to status: ${initialStatus}`);

      // If auto-approved, log for monitoring
      if (initialStatus === "approved") {
        console.log(`Auto-approved submission from user ${userId}`);
      }

    } catch (error: any) {
      console.error("Error in moderation:", error);
      
      // Fallback to pending on error
      await snap!.ref.update({
        status: "pending",
        moderationError: error.message,
        moderatedAt: admin.firestore.FieldValue.serverTimestamp(),
      });
    }
  });

// reportUser function replaced by client-side report creation + processUserReport trigger
// getUserModerationStatus function can be replaced by direct Firestore queries in admin panel

// Process user reports when they are created (trigger-based)
export const processUserReport = onDocumentCreated(
  "user_reports/{reportId}",
  async (event) => {
    const snap = event.data;
    const report = snap!.data();
    const { submissionId, jamId, reportedUserId } = report;

    console.log(`Processing new report for submission ${submissionId} in jam ${jamId}`);

    try {
      // Count total reports for this specific submission
      const allReports = await db.collection("user_reports")
        .where("submissionId", "==", submissionId)
        .get();

      console.log(`Total reports for submission ${submissionId}: ${allReports.size}`);

      // If 3+ reports → Auto-reject submission and confirm all reports
      if (allReports.size >= 3) {
        console.log(`Threshold reached! Auto-rejecting submission ${submissionId}`);

        // Update submission status to "rejected"
        await db.collection("weekly_jam")
          .doc(jamId)
          .collection("submissions")
          .doc(submissionId)
          .update({
            status: "rejected",
            moderatedAt: admin.firestore.FieldValue.serverTimestamp(),
            autoModerated: true,
            moderationReason: `Auto-rejected due to ${allReports.size} community reports`
          });

        // Mark all reports for this submission as "confirmed"
        const batch = db.batch();
        allReports.docs.forEach(doc => {
          batch.update(doc.ref, { status: "confirmed" });
        });
        await batch.commit();

        console.log(`Confirmed ${allReports.size} reports for submission ${submissionId}`);

        // Log the action for monitoring
        console.log(`User ${reportedUserId} submission auto-rejected due to community reports`);
      } else {
        console.log(`Report recorded. ${allReports.size}/3 reports needed for auto-rejection`);
      }

    } catch (error: any) {
      console.error("Error processing user report:", error);
      // Don't throw - we want the report to be saved even if processing fails
    }
  });