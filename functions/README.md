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
       "default": "scritch-2daff"
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

## Function Organization

Functions are organized by feature in separate TypeScript modules:

### `moderation.ts` - Content Moderation
- **`moderateSubmission`**: Auto-moderation trigger for new submissions
- **`reportUser`**: Community reporting system  
- **`getUserModerationStatus`**: Get user moderation status (admin only)

### `admin.ts` - Admin Tools
- **`pardonUser`**: Clear user's report history (admin only)
- **`moderateSubmissionManually`**: Manual moderation override (admin only)
- **`getModerationQueue`**: Get pending submissions for review (admin only)

### Placeholder Modules (Ready for Future Development)
- **`jams.ts`**: Jam creation, scheduling, statistics
- **`notifications.ts`**: Push notifications, email digests
- **`analytics.ts`**: Custom analytics and reporting

## Function Details

### Moderation Functions

#### `moderateSubmission`
**Trigger**: New document created in `weekly_jam/{jamId}/submissions/{userId}`
**Module**: `moderation.ts`

**Behavior**:
- Auto-approves all users by default (trust-first approach)
- Only requires manual review if user has 2+ effective reports
- Admin users are always auto-approved
- Graceful fallback to auto-approve on any errors

#### `reportUser`
**Trigger**: HTTPS callable function
**Module**: `moderation.ts`

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

#### `getUserModerationStatus`
**Trigger**: HTTPS callable function (Admin only)
**Module**: `moderation.ts`

**Parameters**:
```json
{
  "userId": "string"
}
```

**Returns**: User effective reports and moderation status

### Admin Functions

#### `pardonUser`
**Trigger**: HTTPS callable function (Admin only)
**Module**: `admin.ts`

**Parameters**:
```json
{
  "userId": "string",
  "reason": "string"
}
```

**Behavior**: Clears all confirmed reports for a user

#### `moderateSubmissionManually`
**Trigger**: HTTPS callable function (Admin only)
**Module**: `admin.ts`

**Parameters**:
```json
{
  "jamId": "string",
  "userId": "string", 
  "status": "approved|rejected|pending",
  "reason": "string"
}
```

**Behavior**: Manually override submission status

#### `getModerationQueue`
**Trigger**: HTTPS callable function (Admin only)  
**Module**: `admin.ts`

**Parameters**:
```json
{
  "limit": "number" (optional, default: 50)
}
```

**Returns**: Array of submissions requiring manual review

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