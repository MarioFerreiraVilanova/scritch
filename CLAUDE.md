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