# Database Context: Firebase Integration

## Services Overview
- **Authentication**: User management with Firebase Auth
- **Firestore**: NoSQL database for all app data
- **Storage**: Image uploads for user submissions
- **Analytics**: Usage tracking and user behavior
- **Crashlytics**: Error reporting and crash analytics

## Firestore Collections

### `weekly_jam` - Weekly Art Challenges
```
weekly_jam/{jamId}           # e.g., "2025_33"
├── id: String              # Document ID
├── constraint: String?     # Creative constraint
├── medium: String?         # Art medium
├── support: String?        # Art support/surface  
├── topic: String?          # Art topic/theme
├── startDate: Timestamp    # Challenge start
└── endDate: Timestamp      # Challenge end

├── submissions/{userId}    # Subcollection
    ├── userId: String
    ├── storagePath: String # "weekly_jam/2025_33/user123.jpg"
    ├── imageUrl: String    # Firebase Storage download URL
    ├── caption: String?
    ├── createdAt: Timestamp
    └── status: String      # "pending", "approved", "rejected"
```

### `categories` - Art Categories with Localization
```
categories/{categoryType}    # "medium", "support", "topic", "constraint"
├── options/{optionId}      # Main collection (English)
│   ├── id: String
│   ├── name: String?
│   ├── description: String?
│   ├── tips: Map<String, String>?
│   ├── prompt: String?
│   └── frequency: Int?
├── options-es/{optionId}   # Spanish localization
├── options-fr/{optionId}   # French localization  
└── options-en/{optionId}   # English (explicit)
```

### `user_data` - Private User Preferences
```
user_data/{userId}
├── disabledTopicIds: List<String>?
├── disabledMediumIds: List<String>?  
├── disabledSupportIds: List<String>?
├── disabledConstraintIds: List<String>?
└── unImposedCategories: Map<String, Boolean>?
```
**Security**: Private - user can only read/write their own document

### `user_profiles` - Public User Information  
```
user_profiles/{userId}
├── userId: String
├── nickname: String        # e.g., "Picasso47", "VanGogh203"
└── createdAt: Timestamp
```
**Security**: Public read access, user can only write their own profile

### `admins` - Admin Privileges
```
admins/{userId}
└── who: String            # Real name (dummy field to prevent empty doc)
```

### `user_reports` - Moderation System
```
user_reports/{userId}
├── reports: Array<Report>
│   ├── reportedBy: String
│   ├── reason: String
│   ├── timestamp: Timestamp
│   └── jamId: String?
├── effectiveReports: Number    # Calculated with time decay
├── approvedSubmissions: Number # Rehabilitation counter
└── lastCalculated: Timestamp
```

## Firebase Storage Structure
```
weekly_jam/{jamId}/{userId}.jpg    # User submission images
```

## Authentication Flow
- **Sign-in Methods**: Email/password, Google OAuth
- **User Creation**: Auto-generates artist-based nickname in `user_profiles`
- **Admin Check**: Verify if user exists in `admins` collection
- **Security Rules**: Firestore rules enforce data access patterns

## Localization Strategy
- **Translation Script**: `scripts/translate-options.js` uses Google Translate API
- **Automated Workflow**: Translates category options from English to ES/FR
- **Firestore Integration**: Uses Firebase Admin SDK for batch operations

## Development Tools
- **Firebase CLI**: Project configuration and deployment
- **Local Emulators**: Test Firestore, Functions, and Storage locally
- **Security Rules Testing**: Validate access patterns before deployment