import { onObjectFinalized } from "firebase-functions/v2/storage";
import { onCall } from "firebase-functions/v2/https";
import { onDocumentDeleted } from "firebase-functions/v2/firestore";
import * as admin from "firebase-admin";
import * as sharp from "sharp";

const storage = admin.storage();
const firestore = admin.firestore();

/**
 * Cloud Function triggered when images are uploaded to Firebase Storage.
 * Automatically generates thumbnails for new submission images.
 */
export const generateThumbnailOnUpload = onObjectFinalized(async (event) => {
  const object = event.data;
  const filePath = object.name;

  // Only process images in weekly_jam folder that are not already thumbnails
  if (!filePath ||
      !filePath.startsWith('weekly_jam/') ||
      filePath.includes('/thumbnails/') ||
      !filePath.endsWith('.jpg')) {
    console.log('Skipping file:', filePath);
    return null;
  }

  try {
    await generateThumbnail(filePath);
    console.log('Thumbnail generated successfully for:', filePath);
  } catch (error) {
    console.error('Error generating thumbnail for', filePath, ':', error);
  }
});

/**
 * HTTP callable function to batch process existing images that don't have thumbnails.
 * Can be called manually to process legacy submissions.
 */
export const batchGenerateThumbnails = onCall(async (request) => {
  // Verify the user is authenticated and has admin privileges
  if (!request.auth) {
    throw new Error('User must be authenticated');
  }

  try {
    // Check if user is an admin
    const adminDoc = await firestore.collection('admins').doc(request.auth.uid).get();
    if (!adminDoc.exists) {
      throw new Error('User must be an admin');
    }

    console.log('Starting batch thumbnail generation...');
    let processedCount = 0;
    let errorCount = 0;

    // Query all weekly jams
    const jamsSnapshot = await firestore.collection('weekly_jam').get();

    for (const jamDoc of jamsSnapshot.docs) {
      const jamId = jamDoc.id;

      // Get ALL submissions in this jam, then filter for those without thumbnails
      const submissionsSnapshot = await firestore
        .collection('weekly_jam')
        .doc(jamId)
        .collection('submissions')
        .get();

      // Filter submissions that need thumbnails (missing thumbnailUrl field or null/empty value)
      const submissionsNeedingThumbnails = submissionsSnapshot.docs.filter(doc => {
        const data = doc.data();
        return !data.thumbnailUrl; // Missing field, null, undefined, or empty string
      });

      console.log(`Processing ${submissionsNeedingThumbnails.length} of ${submissionsSnapshot.size} submissions in jam ${jamId}`);

      for (const submissionDoc of submissionsNeedingThumbnails) {
        const submission = submissionDoc.data();
        const userId = submission.userId;
        const originalPath = `weekly_jam/${jamId}/${userId}.jpg`;

        try {
          // Check if original file exists
          const [exists] = await storage.bucket().file(originalPath).exists();
          if (!exists) {
            console.log(`Original file not found: ${originalPath}`);
            continue;
          }

          await generateThumbnail(originalPath);
          processedCount++;
          console.log(`✓ Generated thumbnail for ${originalPath}`);
        } catch (error) {
          errorCount++;
          console.error(`✗ Error processing ${originalPath}:`, error);
        }
      }
    }

    const result = {
      processedCount,
      errorCount,
      message: `Batch processing completed. Generated ${processedCount} thumbnails with ${errorCount} errors.`
    };

    console.log(result.message);
    return result;
  } catch (error) {
    console.error('Batch thumbnail generation failed:', error);
    throw new Error('Batch processing failed');
  }
});

/**
 * Core function to generate a thumbnail from an original image.
 * Updates the corresponding Firestore submission document with the thumbnail URL.
 */
async function generateThumbnail(originalPath: string): Promise<void> {
  // Parse the path to extract jamId and userId
  const pathParts = originalPath.split('/');
  if (pathParts.length !== 3 || pathParts[0] !== 'weekly_jam') {
    throw new Error(`Invalid path format: ${originalPath}`);
  }

  const jamId = pathParts[1];
  const fileName = pathParts[2]; // userId.jpg
  const userId = fileName.replace('.jpg', '');

  const thumbnailPath = `weekly_jam/${jamId}/thumbnails/${fileName}`;

  // Check if thumbnail already exists
  const [thumbnailExists] = await storage.bucket().file(thumbnailPath).exists();
  if (thumbnailExists) {
    console.log(`Thumbnail already exists: ${thumbnailPath}`);
    return;
  }

  const bucket = storage.bucket();
  const originalFile = bucket.file(originalPath);
  const thumbnailFile = bucket.file(thumbnailPath);

  // Download the original image
  const [originalBuffer] = await originalFile.download();

  // Generate thumbnail using Sharp (400x400, high quality)
  const thumbnailBuffer = await sharp(originalBuffer)
    .resize(400, 400, {
      fit: 'cover',
      position: 'center'
    })
    .jpeg({
      quality: 85,
      progressive: true
    })
    .toBuffer();

  // Upload thumbnail to Storage
  await thumbnailFile.save(thumbnailBuffer, {
    metadata: {
      contentType: 'image/jpeg',
      cacheControl: 'public, max-age=31536000', // 1 year cache
    }
  });

  // Make thumbnail publicly readable
  await thumbnailFile.makePublic();

  // Get the public URL for the thumbnail
  const thumbnailUrl = `https://storage.googleapis.com/${bucket.name}/${thumbnailPath}`;

  // Update the Firestore submission document
  const submissionRef = firestore
    .collection('weekly_jam')
    .doc(jamId)
    .collection('submissions')
    .doc(userId);

  await submissionRef.update({
    thumbnailUrl: thumbnailUrl
  });

  console.log(`Thumbnail created and document updated for ${originalPath}`);
}

/**
 * Cloud Function triggered when submission documents are deleted.
 * Automatically cleans up both the main image and thumbnail from Storage.
 */
export const cleanupSubmissionFiles = onDocumentDeleted(
  "weekly_jam/{jamId}/submissions/{userId}",
  async (event) => {
    const jamId = event.params.jamId;
    const userId = event.params.userId;

    console.log(`Cleaning up files for submission: jam=${jamId}, user=${userId}`);

    const mainImagePath = `weekly_jam/${jamId}/${userId}.jpg`;
    const thumbnailPath = `weekly_jam/${jamId}/thumbnails/${userId}.jpg`;

    const bucket = storage.bucket();
    let mainImageDeleted = false;
    let thumbnailDeleted = false;
    let mainImageError = null;
    let thumbnailError = null;

    // Delete main image
    try {
      await bucket.file(mainImagePath).delete();
      mainImageDeleted = true;
      console.log(`✓ Deleted main image: ${mainImagePath}`);
    } catch (error: any) {
      const msg = error.message?.toLowerCase() || "";
      if (msg.includes("no such object") || msg.includes("not found") || msg.includes("404")) {
        mainImageDeleted = true; // Consider it successful if file doesn't exist
        console.log(`✓ Main image already deleted: ${mainImagePath}`);
      } else {
        mainImageError = error;
        console.error(`✗ Error deleting main image ${mainImagePath}:`, error);
      }
    }

    // Delete thumbnail
    try {
      await bucket.file(thumbnailPath).delete();
      thumbnailDeleted = true;
      console.log(`✓ Deleted thumbnail: ${thumbnailPath}`);
    } catch (error: any) {
      const msg = error.message?.toLowerCase() || "";
      if (msg.includes("no such object") || msg.includes("not found") || msg.includes("404")) {
        thumbnailDeleted = true; // Consider it successful if file doesn't exist
        console.log(`✓ Thumbnail already deleted: ${thumbnailPath}`);
      } else {
        thumbnailError = error;
        console.error(`✗ Error deleting thumbnail ${thumbnailPath}:`, error);
      }
    }

    // Log final status
    if (mainImageDeleted && thumbnailDeleted) {
      console.log(`✅ Successfully cleaned up all files for submission: jam=${jamId}, user=${userId}`);
    } else {
      console.warn(`⚠️ Partial cleanup for submission: jam=${jamId}, user=${userId}. Main: ${mainImageDeleted}, Thumbnail: ${thumbnailDeleted}`);

      // Don't throw errors - we want the submission deletion to succeed even if file cleanup has issues
      if (mainImageError) console.error("Main image error:", mainImageError);
      if (thumbnailError) console.error("Thumbnail error:", thumbnailError);
    }
  }
);