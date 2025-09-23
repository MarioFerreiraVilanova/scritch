import { onDocumentWritten } from "firebase-functions/v2/firestore";
import { onCall } from "firebase-functions/v2/https";
import * as admin from "firebase-admin";

const db = admin.firestore();

/**
 * Updates submission count and participants list when submissions are created/updated/deleted
 */
export const onSubmissionWrite = onDocumentWritten(
  "weekly_jam/{jamId}/submissions/{userId}",
  async (event) => {
    const change = event.data;
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

      const jamData = jamDoc.data();
      const currentParticipants: string[] = jamData?.participants || [];
      const currentSubmissionCount: number = jamData?.submissionCount || 0;

      let newParticipants = [...currentParticipants];
      let newSubmissionCount = currentSubmissionCount;

      // Handle the change
      if (!change.before.exists && change.after.exists) {
        // Document created - add participant and increment count
        if (!newParticipants.includes(userId)) {
          newParticipants.push(userId);
        }
        newSubmissionCount++;
        console.log(`Added submission for user ${userId} in jam ${jamId}`);

      } else if (change.before.exists && !change.after.exists) {
        // Document deleted - remove participant and decrement count
        newParticipants = newParticipants.filter(id => id !== userId);
        newSubmissionCount = Math.max(0, newSubmissionCount - 1);
        console.log(`Removed submission for user ${userId} in jam ${jamId}`);

      } else if (change.before.exists && change.after.exists) {
        // Document updated - ensure participant is in list but don't change count
        if (!newParticipants.includes(userId)) {
          newParticipants.push(userId);
        }
        console.log(`Updated submission for user ${userId} in jam ${jamId}`);
      }

      // Update the jam document
      await jamRef.update({
        participants: newParticipants,
        submissionCount: newSubmissionCount
      });

      console.log(`Updated jam ${jamId}: ${newSubmissionCount} submissions, ${newParticipants.length} participants`);

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