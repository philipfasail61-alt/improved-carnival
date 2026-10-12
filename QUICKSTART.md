# Quick Start Guide - Anime Generator Android App

## Project Overview
This Android application generates anime characters using the DALL-E 2 API. It's built with modern Android architecture (MVVM, Jetpack Compose) and includes content moderation for TV-14 rating compliance.

**Status**: MVP Foundation Complete ✅

## What's Implemented (Phase 1)

### ✅ Foundation
- [x] Gradle project configuration (Android 9+ support)
- [x] Material3 Compose theme with dark/light mode support
- [x] App core with Hilt dependency injection
- [x] Android Manifest with required permissions

### ✅ Database & Storage
- [x] Room database with Image and GenerationHistory entities
- [x] Data access objects (DAOs) for queries
- [x] Encrypted SharedPreferences for API key storage
- [x] Database initialization and migration strategy

### ✅ Networking & APIs
- [x] Retrofit + OkHttp configuration
- [x] DALL-E 2 API service interface
- [x] Moderation API models
- [x] Network error handling with retry logic

### ✅ Security & Moderation
- [x] On-device content filter (keyword/pattern matching)
- [x] TV-14 rating enforcement
- [x] Secure API key management
- [x] Exception handling with custom error types

### ✅ Architecture
- [x] Repository pattern (Generation, Gallery, Auth)
- [x] ViewModels (Generation, Gallery, Auth)
- [x] State management with StateFlow
- [x] Coroutines + Flow for async operations

### ✅ UI/Screens
- [x] HomeScreen - Image generation interface
- [x] GalleryScreen - Image grid with favorites
- [x] SettingsScreen - API key configuration
- [x] Navigation setup with Compose NavHost
- [x] Loading states and error handling
- [x] Material3 components and theming

### ✅ Testing Foundation
- [x] Unit test framework setup (JUnit, Mockito)
- [x] Sample ContentFilter tests
- [x] Test utilities and Android Test support

## What's Pending (Phase 2+)

### Phase 2: Features
- [ ] Server-side content verification API
- [ ] Cloud authentication & sync
- [ ] Image download & local caching
- [ ] Social media sharing (Instagram, Twitter, etc.)
- [ ] Image gallery cloud sync
- [ ] Advanced search/filtering

### Phase 3: Polish
- [ ] Comprehensive integration tests
- [ ] Edge case handling (no internet, quota exceeded, etc.)
- [ ] Analytics integration
- [ ] Release build configuration
- [ ] Performance optimization
- [ ] Accessibility improvements

## Getting Started

### 1. Open in Android Studio
```bash
cd anime-gen-android
open -a "Android Studio" .
```

### 2. Build the Project
```bash
./gradlew build
```

### 3. Run on Device/Emulator
```bash
# Connect device or open emulator
./gradlew installDebug
```

### 4. Configure API Key
1. Open the app
2. Navigate to Settings
3. Paste your DALL-E 2 API key (get from https://platform.openai.com/api-keys)
4. Tap "Save API Key"

### 5. Generate Your First Image
1. Return to Home screen
2. Enter a prompt (e.g., "Anime girl with blue eyes and long silver hair")
3. Tap "Generate Image"
4. Wait for the image to be generated

## Project Structure Quick Reference

```
app/src/main/kotlin/com/animegen/app/

├── ui/screens/              → Main screens (Home, Gallery, Settings)
├── data/
│   ├── db/                 → Room database entities & DAOs
│   ├── remote/             → DALL-E API client
│   └── repository/         → Business logic
├── viewmodel/              → Screen state management
├── di/                     → Hilt dependency injection
└── util/                   → Utilities (ContentFilter, KeyManager, etc.)
```

## Key Concepts

### MVVM Architecture
- **Model**: Entities, APIs, Database
- **ViewModel**: State management, business logic
- **View**: Compose UI screens

### Dependency Injection (Hilt)
```kotlin
@Inject
constructor(private val repository: GenerationRepository)
```

### State Management (StateFlow)
```kotlin
val uiState: StateFlow<GenerationUiState> = _uiState.asStateFlow()
```

### Content Moderation
```
User Input → On-Device Filter → DALL-E API → Moderation API → Save/Display
```

## Common Tasks

### Adding a New Screen
1. Create screen composable in `ui/screens/`
2. Create ViewModel in `viewmodel/`
3. Add route to `MainActivity.kt` NavHost
4. Inject repositories as needed

### Adding a Database Table
1. Create entity in `data/db/Entities.kt`
2. Create DAO in `data/db/Daos.kt`
3. Add to `AppDatabase` abstract class
4. Update version number

### Adding API Endpoints
1. Add method to `DalleService` interface
2. Create models in `data/remote/DalleModels.kt`
3. Use in repository with error handling

## Important Configurations

### minSdk / targetSdk
- **minSdk**: 28 (Android 9)
- **targetSdk**: 34 (Android 14)
- Supports API 28+ devices

### Kotlin Version
- Kotlin 1.9.10
- Java 17 compatibility

### Compose Version
- Compose BOM: 2023.10.01
- Material3 components

## Debugging Tips

1. **Enable Network Logging**
   - Check `NetworkModule.kt` HttpLoggingInterceptor level

2. **View Database**
   - Android Studio → Database Inspector → Select app → Browse tables

3. **Check API Key Storage**
   - Device Settings → Apps → Anime Generator → Permissions & data

4. **Logcat Filter**
   ```bash
   adb logcat | grep "AnimGen\|DalleService\|GenerationVM"
   ```

## Next Steps to Complete MVP

1. **Implement Image Download**
   - Add HTTP image download in GenerationRepository
   - Save to app cache directory
   - Handle storage permissions (Android 11+)

2. **Implement Sharing**
   - Create ShareManager utility
   - Set up FileProvider for content sharing
   - Add share intents for social platforms

3. **Server-Side Moderation**
   - Implement backend verification endpoint
   - Call API before saving images
   - Handle moderation rejections

4. **Cloud Sync (Optional)**
   - Design cloud auth flow
   - Implement sync repository
   - Add background sync service

5. **Testing**
   - Write more unit tests (repositories, viewmodels)
   - Add instrumented tests (UI)
   - Integration tests with mock APIs

6. **Polish**
   - Add skeleton loading screens
   - Improve error messages
   - Add rate limiting UI
   - Implement retry logic for failed generations

## Useful Links

- [DALL-E 2 API Docs](https://platform.openai.com/docs/guides/images)
- [Jetpack Compose Docs](https://developer.android.com/jetpack/compose)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Material Design 3](https://m3.material.io/)

## Troubleshooting Build Issues

```bash
# Clean build
./gradlew clean build

# Clear Gradle cache
rm -rf ~/.gradle/caches

# Update dependencies
./gradlew --refresh-dependencies build

# Check for conflicts
./gradlew dependencies
```

## Branch & Version Info

- **Current Branch**: `philipfasail61-alt-improved-carnival`
- **Base Branch**: `master`
- **App Version**: 1.0.0 (will increment with releases)
- **Min SDK**: 28
- **Target SDK**: 34

## Support & Questions

For implementation questions or bugs:
1. Check existing TODO comments in code
2. Review comments in updated todos SQL
3. Refer to inline documentation in classes
4. Check Logcat for error messages

---

**Last Updated**: October 2026
**Status**: Foundation phase complete, ready for Phase 2 features
