# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
**Scritch** is a Kotlin Multiplatform art community app where users participate in weekly creative challenges called "jams". Users submit artwork based on randomized prompts (medium, support, topic, constraint) and engage with a moderated community.

## Key Build Commands
- `./gradlew composeApp:build` - Build entire project
- `./gradlew composeApp:assembleDebug` - Build Android APK  
- `./gradlew composeApp:test` - Run tests
- `cd functions && npm run build` - Build Cloud Functions

## Project Structure
```
├── composeApp/             # Kotlin Multiplatform app
│   ├── src/commonMain/     # Shared logic (UI, ViewModels, repos)
│   ├── src/androidMain/    # Android-specific code
│   └── src/iosMain/        # iOS-specific code
├── functions/              # Firebase Cloud Functions (TypeScript)
├── iosApp/                # iOS native shell (SwiftUI)
└── .claude/docs/          # Modular context documentation
```

## Loading Specialized Context
Use these slash commands to load domain-specific context:
- `/prime-app` - Load frontend context (Kotlin MP, Compose, MVVM, navigation)  
- `/prime-functions` - Load backend context (Cloud Functions, moderation, APIs)
- `/prime-firebase` - Load database context (Firestore schema, auth, storage)
- `/prime-dev` - Load development context (Gradle, testing, deployment)