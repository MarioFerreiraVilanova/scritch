# Scritch Cloud Functions

This directory contains Firebase Cloud Functions for automated moderation and trust-based approval system.

## Features

- **Trust-based moderation**: Auto-approve submissions from trusted users
- **Community reporting**: Users can report inappropriate content
- **Admin tools**: Functions for admin dashboard integration

## Setup

### Prerequisites

1. Firebase CLI installed: `npm install -g firebase-tools`
2. Node.js 18+ installed
3. Firebase project with Firestore enabled

### Configuration

1. **Update project ID** in `.firebaserc`:
   ```json
   {
     "projects": {
       "default": "your-actual-project-id"
     }
   }
   ```

2. **Login to Firebase** (run this locally):
   ```bash
   firebase login
   ```

3. **Install dependencies**:
   ```bash
   cd functions
   npm install
   ```

### Deployment

1. **Build the functions**:
   ```bash
   npm run build
   ```

2. **Deploy to Firebase**:
   ```bash
   firebase deploy --only functions
   ```

## Function Details

### `moderateSubmission`

**Trigger**: New document created in `weekly_jam/{jamId}/submissions/{userId}`

**Behavior**:
- **Auto-approves all users by default** (trust-first approach)
- Only requires manual review if user has 2+ confirmed reports
- Admin users are always auto-approved
- Graceful fallback to auto-approve on any errors

### `reportUser`

**Trigger**: HTTPS callable function

**Parameters**:
```json
{
  "reportedUserId": "string",
  "submissionId": "string", 
  "reason": "string"
}
```

**Behavior**:
- Creates report in `user_reports` collection
- Auto-rejects submission if 3+ reports received
- Marks reports as "confirmed" when threshold reached
- Users with 2+ confirmed reports require manual review for future submissions

### `getUserTrust`

**Trigger**: HTTPS callable function (Admin only)

**Parameters**:
```json
{
  "userId": "string"
}
```

**Returns**: User trust score and level for admin dashboard

## Moderation Strategy: "Trust First, Verify Later"

**Default Behavior**: Auto-approve all submissions

**Manual Review Triggered By**: 
- Users with 2+ effective reports against them
- Admin override (admins can always manually set status)

**Community Moderation**:
- 3+ reports on same submission → Auto-reject + mark reports as confirmed
- Confirmed reports count towards user's moderation threshold

## Credibility Recovery System

**Time Decay**: Only reports from last 6 months count for moderation decisions

**Good Behavior Credits**: Every 10 approved submissions = -1 effective report

**Admin Pardon**: Admins can clear a user's report history

**Example Recovery**:
- User gets 3 confirmed reports → Manual review required
- After 4 months + 20 approved submissions → Back to auto-approval
- Old reports beyond 6 months → Automatically ignored

## Database Schema

### Collections Used

- `user_profiles` - User profile data with createdAt
- `weekly_jam/{jamId}/submissions` - Submission documents  
- `user_reports` - User report documents
- `admins` - Admin user IDs

### New Fields Added to Submissions

- `autoModerated: boolean` - Whether auto-moderated
- `trustScore: number` - User's trust score at time of submission
- `moderatedAt: Timestamp` - When moderation occurred

## Security

- All functions validate user authentication
- Admin-only functions check admin collection
- Users cannot report themselves
- Rate limiting handled by client-side implementation

## Monitoring

Check Firebase Console → Functions for:
- Function execution logs
- Error tracking
- Performance metrics

## Testing

Use Firebase Emulators for local testing:
```bash
npm run serve
```

This starts local emulators for testing before deployment.