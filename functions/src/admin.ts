// Admin functions have been moved to direct Firestore operations in the client app
// This file is kept for reference but all functions are now unused

// Functions that were moved to client-side AdminRepository:
// - pardonUser: Direct Firestore batch update of user_reports
// - moderateSubmissionManually: Direct Firestore update of submission status  
// - getModerationQueue: Direct Firestore collection group query for pending submissions

// These can be implemented as admin panel features using direct Firestore operations