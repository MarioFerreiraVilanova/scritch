# Frontend Context: Kotlin Multiplatform App

## Architecture Overview
- **MVVM Pattern**: ViewModels handle business logic, UI observes StateFlow
- **Compose Multiplatform**: Shared UI across Android/iOS with platform-specific shells
- **Koin Dependency Injection**: All ViewModels, repositories, and use cases injected
- **Type-safe Navigation**: Sealed class routes with Compose Navigation

## Key Modules & Structure
```
composeApp/src/commonMain/kotlin/
├── app/                    # Main entry point, theming, App.kt
├── auth/                   # Firebase Authentication integration
├── jam/                    # Core feature: photo submissions, community
├── categories/             # User preferences, category management
├── userdata/               # User profile and settings data
├── navigation/             # Route definitions, AppNavGraph.kt
└── di/                     # Dependency injection modules (Koin)
```

## Navigation System
- **Routes**: Sealed classes in `navigation/` for type safety
- **Authentication Flow**: Separate nav graphs for authenticated/unauthenticated states
- **Deep Linking**: Supported for jam submissions and user profiles

## State Management
- **StateFlow**: ViewModels expose UI state via StateFlow
- **Repository Pattern**: Data layer abstraction with clean interfaces
- **Error Handling**: Standardized error states and user feedback

## UI Components
- **Design System**: Consistent theming with Material Design principles
- **Responsive Design**: Adapts to different screen sizes and orientations
- **Accessibility**: Screen reader support and touch target sizing

## Platform-Specific Code
- **expect/actual**: Platform abstractions for file handling, permissions
- **androidMain/**: Android-specific implementations
- **iosMain/**: iOS-specific implementations
- **Platform Services**: Camera, gallery, file picker integrations

## Key Files to Understand
- `app/App.kt`: Main application entry point with theming setup
- `navigation/AppNavGraph.kt`: Complete navigation structure
- `di/AppModule.kt`: Dependency injection configuration  
- `jam/presentation/JamViewModel.kt`: Core feature ViewModel example

## Development Patterns
- **Localization Required**: ALL UI text must be localized. PROACTIVELY use the localization-text-creator agent for localization tasks
- **No Hardcoded Strings**: Use string resources consistently
- **StateFlow Observation**: UI components observe ViewModel state
- **Koin Injection**: Use constructor injection for dependencies