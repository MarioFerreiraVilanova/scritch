# Development Context: Build System & DevOps

## Build System
**Gradle 8.13** with **Kotlin Multiplatform** and **Compose Multiplatform**

### Key Configuration
- **JVM Target**: 17 for Android compatibility
- **Version Catalog**: Dependencies managed in `libs.versions.toml`  
- **Memory Settings**: `-Xmx2048M` for build performance
- **Kotlin Native Cache**: Disabled (`cacheKind=none`) due to navigation library corruption

### Essential Build Commands
```bash
./gradlew composeApp:build              # Build entire project
./gradlew composeApp:assembleDebug      # Android APK (debug)
./gradlew composeApp:assembleRelease    # Android APK (release)
./gradlew composeApp:clean              # Clean build artifacts
./gradlew composeApp:installDebug       # Install on device
```

### Testing Commands
```bash
./gradlew composeApp:test                      # All unit tests
./gradlew composeApp:testDebugUnitTest         # Debug unit tests
./gradlew composeApp:iosSimulatorArm64Test     # iOS ARM64 tests
./gradlew composeApp:connectedAndroidTest      # Instrumentation tests
```

## Platform-Specific Notes

### iOS Development
- **Target Limitation**: Only ARM64 targets active (x64 disabled for Firebase KMP compatibility)
- **Xcode Integration**: Native iOS shell in `iosApp/` directory
- **Bundle ID**: Warnings about inference can be safely ignored
- **Team Configuration**: Set in `iosApp/Configuration/Config.xcconfig`

### Android Development
- **API Level**: Minimum SDK and target SDK defined in build configuration
- **Google Services**: Full Firebase integration with `google-services.json`
- **Signing**: Debug and release signing configurations

## Deployment

### Firebase Functions
```bash
cd functions
npm install                    # Install dependencies
npm run build                 # Build TypeScript
npm run serve                 # Local emulators
../deploy-functions.sh        # Deploy to Firebase
firebase functions:log        # View logs
```

### Mobile App Deployment
- **Android**: APK generation via Gradle, Play Store deployment
- **iOS**: Xcode build and App Store deployment

## Development Tools

### Code Quality
- **Firebase Functions**: ESLint with TypeScript rules
- **Kotlin**: Official code style (`kotlin.code.style=official`)
- **No Linting**: Currently no automated Kotlin linting (Detekt/KtLint)

### Internationalization
- **Translation Automation**: `scripts/translate-options.js`
- **Google Cloud Translation**: API integration for category options
- **Supported Languages**: English, Spanish, French

## Troubleshooting

### Common Issues
- **iOS Build Failures**: Clear Kotlin Native cache or check Firebase KMP compatibility
- **Memory Issues**: Increase Gradle heap size in `gradle.properties`
- **Navigation Library**: Cache corruption requires `cacheKind=none` setting
- **Bundle ID Warnings**: iOS build warnings can be safely ignored

### Performance Optimization
- **Gradle Daemon**: Enabled for faster builds
- **Parallel Execution**: Multi-module project benefits from parallel builds
- **Build Cache**: Local and remote caching configured

### Debugging
- **Android**: Standard Android debugging with Logcat
- **iOS**: Xcode debugging tools for native shell
- **Shared Code**: Platform-specific debugging strategies for Kotlin MP