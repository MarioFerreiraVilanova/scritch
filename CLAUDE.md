# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Development Commands

### Building
- `./gradlew composeApp:build` - Build the entire project
- `./gradlew composeApp:assembleDebug` - Build debug Android APK
- `./gradlew composeApp:assembleRelease` - Build release Android APK  
- `./gradlew composeApp:clean` - Clean build artifacts

### iOS Build Notes
- **Kotlin Native Cache Workaround**: Due to cache corruption issues with `androidx.navigation` library, `kotlin.native.cacheKind=none` is set in `gradle.properties` to ensure iOS builds work correctly in both command line and Xcode
- **Bundle ID Warning**: iOS builds show bundle ID inference warnings - can be ignored or resolved by specifying explicit bundle ID

### Testing
- `./gradlew composeApp:test` - Run all unit tests
- `./gradlew composeApp:testDebugUnitTest` - Run debug unit tests
- `./gradlew composeApp:iosX64Test` - Run iOS simulator tests (x64)
- `./gradlew composeApp:iosSimulatorArm64Test` - Run iOS simulator tests (ARM64)
- `./gradlew composeApp:connectedAndroidTest` - Run Android instrumentation tests (requires device)

### Installation
- `./gradlew composeApp:installDebug` - Install debug APK on connected Android device
- `./gradlew composeApp:uninstallDebug` - Uninstall debug APK

## Project Architecture

### Multiplatform Structure
- **Android**: Primary target with full Firebase integration and Google Services
- **iOS**: Native iOS app using SwiftUI for the shell and Kotlin Multiplatform for shared logic
- **Web/Desktop**: Currently commented out but infrastructure exists

### Core Architecture Patterns
- **MVVM**: ViewModels handle business logic, UI observes state via StateFlow
- **Koin Dependency Injection**: All repositories, ViewModels, and use cases are injected via Koin modules
- **Firebase Backend**: Authentication, Firestore database, Analytics, Crashlytics, and Storage
- **Navigation**: Type-safe navigation using Compose Navigation with sealed class routes
- **Repository Pattern**: Data layer abstraction with repositories for each domain

### Key Modules
- **app/**: Core app setup, main entry point, and theming
- **auth/**: Firebase Authentication integration
- **jam/**: Core feature for photo submissions and community content
- **categories/**: User preference/option management system
- **userdata/**: User profile and settings data management
- **di/**: Dependency injection modules using Koin
- **navigation/**: Type-safe navigation system with authenticated/unauthenticated flows

### Firebase Integration
The app heavily relies on Firebase services:
- Authentication for user management
- Firestore for data persistence
- Storage for image uploads
- Analytics for usage tracking
- Crashlytics for error reporting
- **Cloud Functions for server-side logic** (see Cloud Functions section below)

### Platform-Specific Code
- **commonMain/**: Shared business logic, UI, and data layer
- **androidMain/**: Android-specific implementations (file handling, platform services)
- **iosMain/**: iOS-specific implementations
- Platform abstraction via expect/actual declarations

### Key Files to Understand
- `app/App.kt`: Main app entry point with theming
- `navigation/AppNavGraph.kt`: Navigation structure and flow
- `di/AppModule.kt`: Dependency injection configuration
- `build.gradle.kts`: Multiplatform build configuration with all dependencies

## Development Preferences

### Internationalization (i18n)
- **ALL text in the app must be localized** - never use hardcoded strings in UI components
- **Supported languages**: 
  - English (default/fallback)
  - French (France) 
  - Spanish (Spain)
- **String key organization**: Follow the existing organizational structure in string resource files when adding new keys
- **Before adding new strings**: Always check existing localization files to understand the current key naming patterns and organization

### Firestore Database Structure

#### Collections Overview
- **weekly_jam**: Weekly art challenges/prompts
- **categories**: Art categories (medium, support, topic, constraint) with localized options
- **user_data**: Private user preferences and disabled options
- **user_profiles**: Public user data (nicknames, display information)
- **admins**: Admin users with elevated privileges

#### Collection: `weekly_jam`
**Document Structure** (JamDto):
```
weekly_jam/{jamId}
├── id: String (document ID, e.g., "2025_33")
├── constraint: String?
├── medium: String?
├── support: String?
├── topic: String?
├── startDate: Timestamp
└── endDate: Timestamp
```

**Subcollection**: `submissions`
```
weekly_jam/{jamId}/submissions/{userId}
├── userId: String
├── storagePath: String (e.g., "weekly_jam/2025_33/user123.jpg")
├── imageUrl: String (Firebase Storage download URL)
├── caption: String?
├── createdAt: Timestamp
└── status: String ("pending", "approved", "rejected")
```

#### Collection: `categories`
**Document Structure**: Four category types (`medium`, `support`, `topic`, `constraint`)
```
categories/{categoryType}
└── options/
    └── {optionId} (OptionDto)
        ├── id: String
        ├── name: String?
        ├── description: String?
        ├── tips: Map<String, String>? (TipMap)
        ├── prompt: String?
        └── frequency: Int?
```

**Localized Options**: Each category also has localized subcollections:
```
categories/{categoryType}
├── options-en/
├── options-es/
└── options-fr/
```

#### Collection: `user_data`
**Document Structure** (UserDataDto):
```
user_data/{userId}
├── disabledTopicIds: List<String>?
├── disabledMediumIds: List<String>?
├── disabledSupportIds: List<String>?
├── disabledConstraintIds: List<String>?
└── unImposedCategories: Map<String, Boolean>? (categorySettings)
```

**Security**: Private data - only the user who owns the document can read/write

#### Collection: `user_profiles`
**Document Structure** (UserProfileDto):
```
user_profiles/{userId}
├── userId: String
├── nickname: String (e.g., "Picasso47", "VanGogh203")
└── createdAt: Timestamp
```

**Security**: Public read access (needed for jam submissions), user can only write their own profile

**Usage**:
- Stores publicly visible user information like artist-based nicknames
- Automatically generated on first access with format: {ArtistName}{Number}
- Separate from private user_data for proper security isolation
- Used in jam submissions for user identification

#### Collection: `admins`
**Document Structure**: Admin user records
```
admins/{userId}
└── who: String (real name, dummy field to prevent empty document)
```

**Usage**:
- Check if a user ID exists in this collection to determine admin privileges
- Used for admin panel access (creating jams, moderating submissions)
- Simplified Firestore security rule setup (easier than role-based fields)

#### Storage Structure
- **Path**: `weekly_jam/{jamId}/{userId}.jpg`
- **Usage**: User submission images for weekly jams

## Firebase Cloud Functions

The project includes a complete Firebase Cloud Functions setup for server-side logic, located in the `functions/` directory.

### Development Commands

#### Cloud Functions
- `cd functions && npm install` - Install function dependencies
- `cd functions && npm run build` - Build TypeScript functions
- `cd functions && npm run serve` - Run local emulators for testing
- `./deploy-functions.sh` - Deploy functions to Firebase (requires Firebase CLI login)
- `firebase functions:log` - View function execution logs

### Project Structure
```
functions/
├── src/
│   ├── index.ts          # Main entry point (imports/exports all functions)
│   ├── moderation.ts     # Auto-moderation and community reporting
│   ├── admin.ts          # Admin tools and manual overrides
│   ├── jams.ts           # Jam management (placeholder for future)
│   ├── notifications.ts  # Push notifications (placeholder for future)
│   └── analytics.ts      # Custom analytics (placeholder for future)
├── package.json          # Node.js dependencies and scripts  
├── tsconfig.json         # TypeScript configuration
├── .eslintrc.js          # Code linting rules
├── README.md             # Detailed function documentation
└── .gitignore            # Ignore node_modules, build artifacts
```

### Current Functions (Organized by Module)

#### Moderation Functions (`moderation.ts`)
- **`moderateSubmission`**: Auto-moderates new submissions based on user report history
- **`reportUser`**: Handles community reporting with automatic enforcement (3+ reports = auto-reject)
- **`getUserModerationStatus`**: Get user's effective reports and moderation status (admin only)

#### Admin Functions (`admin.ts`)
- **`pardonUser`**: Clear user's report history for rehabilitation (admin only)
- **`moderateSubmissionManually`**: Manual submission status override (admin only)
- **`getModerationQueue`**: Get submissions requiring manual review (admin only)

### Moderation System Features

**Trust-First Approach**: Auto-approve all users by default, only flag problematic users
**Community Reporting**: 3+ community reports automatically reject submissions
**Credibility Recovery**: Time decay (6 months) + good behavior credits (10 approvals = -1 effective report)
**Admin Tools**: Full override capabilities and user rehabilitation system

### Adding New Functions

When adding new Cloud Functions:
1. **Choose appropriate module** or create new one (e.g., `jams.ts`, `notifications.ts`)
2. **Add function to module file** with proper error handling and authentication
3. **Export from module** (functions auto-exported via `index.ts`)
4. **Update `functions/README.md`** with function documentation
5. **Test locally** with `npm run serve` 
6. **Deploy** with `./deploy-functions.sh`
7. **Update this CLAUDE.md** if the function adds new architectural patterns

### Database Collections Used by Functions
- `user_reports`: Community reports and moderation tracking
- `user_profiles`: User data for moderation calculations
- `weekly_jam/{jamId}/submissions`: Submission documents (auto-moderated)
- `admins`: Admin privilege verification

### Security & Authentication
- All admin functions verify user exists in `admins` collection
- Functions use Firebase Admin SDK with full database privileges
- User authentication verified via `context.auth` in callable functions
- Firestore security rules work alongside function-based moderation