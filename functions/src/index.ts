import * as admin from "firebase-admin";

// Initialize Firebase Admin SDK
admin.initializeApp();

// Export all functions from organized modules
export * from "./moderation";
// admin functions moved to client-side operations

// Placeholder exports for future modules
// Uncomment as you add functions to these modules:
// export * from "./jams";
// export * from "./notifications"; 
// export * from "./analytics";