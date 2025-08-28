# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Development Commands

### Building
- `./gradlew composeApp:build` - Build the entire project
- `./gradlew composeApp:assembleDebug` - Build debug Android APK
- `./gradlew composeApp:assembleRelease` - Build release Android APK  
- `./gradlew composeApp:clean` - Clean build artifacts

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