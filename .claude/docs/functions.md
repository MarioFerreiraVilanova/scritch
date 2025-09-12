# Backend Context: Firebase Cloud Functions

## Project Structure
```
functions/
├── src/
│   ├── index.ts          # Main entry point (exports all functions)
│   ├── moderation.ts     # Auto-moderation and community reporting
│   ├── admin.ts          # Admin tools and manual overrides
│   ├── jams.ts           # Jam management (placeholder)
│   ├── notifications.ts  # Push notifications (placeholder)
│   └── analytics.ts      # Custom analytics (placeholder)
├── package.json          # Dependencies and build scripts
└── tsconfig.json         # TypeScript configuration
```

## Development Commands
- `cd functions && npm install` - Install dependencies
- `npm run build` - Build TypeScript functions
- `npm run serve` - Run local Firebase emulators
- `../deploy-functions.sh` - Deploy to Firebase (with error checking)
- `firebase functions:log` - View execution logs

## Current Functions

### Moderation System (`moderation.ts`)
- **`moderateSubmission`**: Auto-moderates new submissions based on user report history
- **`reportUser`**: Handles community reporting with automatic enforcement
- **`getUserModerationStatus`**: Get user's moderation status (admin only)

### Admin Tools (`admin.ts`)  
- **`pardonUser`**: Clear user's report history for rehabilitation
- **`moderateSubmissionManually`**: Manual submission status override
- **`getModerationQueue`**: Get submissions requiring manual review

## Moderation Logic
**Trust-First Approach**: Auto-approve all users by default, only flag problematic users
- **3+ Reports**: Automatically reject new submissions
- **Time Decay**: Reports lose weight after 6 months
- **Rehabilitation**: 10 approved submissions = -1 effective report
- **Admin Override**: Full manual control for edge cases

## Database Collections Used
- `user_reports`: Community reports and moderation tracking
- `user_profiles`: User data for moderation calculations  
- `weekly_jam/{jamId}/submissions`: Submission documents (auto-moderated)
- `admins`: Admin privilege verification

## Security & Authentication
- Admin functions verify user exists in `admins` collection
- User authentication via `context.auth` in callable functions
- Firebase Admin SDK provides full database privileges
- Firestore security rules work alongside function-based moderation

## Adding New Functions
1. Choose appropriate module file or create new one
2. Add function with proper error handling and authentication
3. Export from module (auto-exported via `index.ts`)
4. Test locally with `npm run serve`
5. Deploy with `./deploy-functions.sh`
6. Update function documentation

## Error Handling Patterns
- Always return structured error responses
- Log errors with context for debugging
- Validate input parameters thoroughly
- Handle Firebase Auth and Firestore errors gracefully