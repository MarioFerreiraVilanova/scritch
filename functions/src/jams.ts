import { onDocumentWritten } from "firebase-functions/v2/firestore";
import { onCall } from "firebase-functions/v2/https";
import * as admin from "firebase-admin";

const db = admin.firestore();

/**
 * Updates submission count and participants list when submissions are created/updated/deleted
 * Uses actual document count to avoid race conditions
 */
export const onSubmissionWrite = onDocumentWritten(
  "weekly_jam/{jamId}/submissions/{userId}",
  async (event) => {
    const jamId = event.params.jamId;
    const userId = event.params.userId;

    try {
      const jamRef = db.collection("weekly_jam").doc(jamId);

      // Get current jam data
      const jamDoc = await jamRef.get();
      if (!jamDoc.exists) {
        console.error(`Jam document ${jamId} does not exist`);
        return;
      }

      // Query actual submissions to get accurate count and participants
      const submissionsSnapshot = await jamRef
        .collection("submissions")
        .get();

      const participants: string[] = [];
      submissionsSnapshot.forEach(doc => {
        const submission = doc.data();
        if (submission.userId && !participants.includes(submission.userId)) {
          participants.push(submission.userId);
        }
      });

      const submissionCount = submissionsSnapshot.size;

      // Update the jam document with accurate data
      await jamRef.update({
        participants,
        submissionCount
      });

      console.log(`Updated jam ${jamId}: ${submissionCount} submissions, ${participants.length} participants (triggered by ${userId})`);

    } catch (error) {
      console.error(`Error updating jam ${jamId} submission stats:`, error);
    }
  });

/**
 * Recalculates submission count and participants for a jam (for data repair/migration)
 */
export const recalculateJamStats = onCall(async (request) => {
  // Verify admin access
  if (!request.auth) {
    throw new Error("User must be authenticated");
  }

  const adminDoc = await db.collection("admins").doc(request.auth.uid).get();
  if (!adminDoc.exists) {
    throw new Error("Admin access required");
  }

  const { jamId } = request.data;
  if (!jamId) {
    throw new Error("jamId is required");
  }

  try {
    // Get all submissions for this jam
    const submissionsSnapshot = await db
      .collection("weekly_jam")
      .doc(jamId)
      .collection("submissions")
      .get();

    const participants: string[] = [];
    const submissionCount = submissionsSnapshot.size;

    submissionsSnapshot.forEach(doc => {
      const submission = doc.data();
      if (submission.userId && !participants.includes(submission.userId)) {
        participants.push(submission.userId);
      }
    });

    // Update jam document
    await db.collection("weekly_jam").doc(jamId).update({
      participants,
      submissionCount
    });

    return {
      success: true,
      jamId,
      submissionCount,
      participantCount: participants.length
    };

  } catch (error) {
    console.error(`Error recalculating stats for jam ${jamId}:`, error);
    throw new Error("Failed to recalculate jam stats");
  }
});