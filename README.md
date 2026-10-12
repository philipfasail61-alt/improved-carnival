# Anime Generator Android App

A TV-14 rated anime image generator app for Android 9+ (API 28+) using Kotlin, Jetpack Compose, and DALL-E 2 API.

## Features

### Core Features
- 🎨 **AI Image Generation**: Generate anime characters using DALL-E 2 API
- 🖼️ **Local Gallery**: Save and manage generated images locally
- 🔒 **Secure API Key Storage**: Encrypted storage for DALL-E 2 API keys
- 📤 **Share Functionality**: Share generated images to social media
- 🛡️ **Content Moderation**: Two-layer defense (on-device + server verification)
- 🎭 **TV-14 Rating**: All content moderated for ages 14+

### Authentication
- **Local Offline Mode**: Use your own DALL-E 2 API key locally
- **Optional Cloud Sync**: (Future) Sync gallery across devices with optional account

### Architecture
- **MVVM Pattern**: Clean separation of concerns
- **Jetpack Compose**: Modern, reactive UI framework
- **Room Database**: Local persistence with type-safe queries
- **Hilt DI**: Dependency injection for testability
- **Retrofit + OkHttp**: RESTful API client with interceptors
- **Coroutines + Flow**: Reactive, async operations

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── kotlin/com/animegen/app/
│   │   │   ├── MainActivity.kt                    # Main entry point
│   │   │   ├── AnimeGeneratorApp.kt               # Application class with Hilt
│   │   │   ├── ui/
│   │   │   │   ├── screens/
│   │   │   │   │   ├── HomeScreen.kt              # Main generation screen
│   │   │   │   │   ├── GalleryScreen.kt           # Gallery grid view
│   │   │   │   │   └── SettingsScreen.kt          # API key configuration
│   │   │   │   ├── components/                    # Reusable UI components
│   │   │   │   └── theme/
│   │   │   │       ├── Theme.kt                   # Material3 theme with dark/light mode
│   │   │   │       └── Type.kt                    # Typography system
│   │   │   ├── data/
│   │   │   │   ├── db/
│   │   │   │   │   ├── AppDatabase.kt             # Room database class
│   │   │   │   │   ├── Entities.kt                # Image and History entities
│   │   │   │   │   └── Daos.kt                    # Data access objects
│   │   │   │   ├── remote/
│   │   │   │   │   ├── DalleService.kt            # Retrofit API interface
│   │   │   │   │   └── DalleModels.kt             # Request/response models
│   │   │   │   └── repository/
│   │   │   │       ├── GenerationRepository.kt    # Image generation logic
│   │   │   │       ├── GalleryRepository.kt       # Gallery management
│   │   │   │       └── AuthRepository.kt          # API key management
│   │   │   ├── viewmodel/
│   │   │   │   ├── GenerationViewModel.kt         # Generation screen state
│   │   │   │   ├── GalleryViewModel.kt            # Gallery screen state
│   │   │   │   └── AuthViewModel.kt               # Settings screen state
│   │   │   ├── util/
│   │   │   │   ├── ContentFilter.kt               # On-device moderation
│   │   │   │   ├── KeyManager.kt                  # Secure key storage
│   │   │   │   └── Result.kt                      # Result wrapper & exceptions
│   │   │   └── di/
│   │   │       ├── AppModule.kt                   # Database & SharedPrefs DI
│   │   │       ├── NetworkModule.kt               # Retrofit & OkHttp DI
│   │   │       └── RemoteModule.kt                # API service DI
│   │   ├── res/
│   │   │   ├── values/
│   │   │   │   ├── strings.xml                    # String resources
│   │   │   │   ├── colors.xml                     # Color palette
│   │   │   │   └── themes.xml                     # Theme definitions
│   │   │   ├── xml/
│   │   │   │   └── file_paths.xml                 # FileProvider paths
│   │   │   └── drawable/                          # Vector drawables
│   │   └── AndroidManifest.xml                    # App manifest (API 28+)
│   ├── test/                                      # Unit tests
│   └── androidTest/                               # Instrumented tests
├── build.gradle.kts                               # App module configuration
└── proguard-rules.pro                             # ProGuard/R8 rules

settings.gradle.kts                                # Project configuration
build.gradle.kts                                   # Root Gradle file
```

## Setup & Installation

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 17 or later
- Android NDK (optional)
- DALL-E 2 API key from https://platform.openai.com/api-keys

### Build Steps

1. **Clone and Open**
   ```bash
   git clone <repo-url>
   cd anime-gen-android
   open -a "Android Studio" .
   ```

2. **Configure Gradle**
   - Gradle 8.1+ will auto-download
   - JDK 17 is used for compilation

3. **Build & Run**
   ```bash
   # Build
   ./gradlew build

   # Run on connected device/emulator
   ./gradlew installDebug
   ```

4. **Configure API Key**
   - Open app and navigate to Settings
   - Paste your DALL-E 2 API key (starts with `sk-`)
   - Key is encrypted and stored locally

## Key Technologies

| Component | Library | Version |
|-----------|---------|---------|
| UI Framework | Jetpack Compose | 2023.10.01 |
| Architecture | MVVM + Repository Pattern | - |
| Database | Room | 2.6.1 |
| DI Framework | Hilt | 2.48 |
| Networking | Retrofit + OkHttp | 2.10.0 / 4.11.0 |
| Security | AndroidX Security Crypto | 1.1.0-alpha06 |
| Async | Coroutines + Flow | 1.7.3 |
| Image Loading | Coil | 2.5.0 |

## API Integration

### DALL-E 2 Endpoints Used

1. **Image Generation** (`POST /images/generations`)
   - Creates anime images from prompts
   - Rate limited: ~3-60 images per minute
   - Costs tokens based on image size

2. **Moderation** (`POST /moderations`)
   - Server-side content validation
   - Checks prompt appropriateness
   - Enforces TV-14 rating

### Authentication

```kotlin
// API calls require Authorization header
val authHeader = "Bearer sk-<your-api-key>"
dalleService.generateImage(request, authHeader)
```

## Content Moderation

### On-Device Filtering (Layer 1)
- Keyword/pattern matching for blocked terms
- TV-14 rating enforcement (no sexual content with minors)
- Real-time validation before API submission

### Server-Side Verification (Layer 2)
- OpenAI Moderation API integration
- Image-level content verification
- Prevents non-compliant images from saving

## Data Storage

### Local Database (Room)
```kotlin
// Images table
ImageEntity: id, prompt, imagePath, timestamp, tags, isFavorite, cloudSynced, cloudId

// Generation history table
GenerationHistoryEntity: id, prompt, status, createdAt, errorMessage, imageId
```

### Secure Storage
```kotlin
// EncryptedSharedPreferences (Android Security Crypto)
- DALLE_API_KEY (encrypted)
- LOCAL_AUTH_TOKEN
- CLOUD_AUTH_TOKEN (future)
```

## Error Handling

The app implements comprehensive error handling:

```kotlin
sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception, val message: String) : Result<T>()
    data object Loading : Result<T>()
}

sealed class AnimeGenException(message: String) : Exception(message)
- ApiKeyNotValidException
- ContentModerationException
- DalleApiException
- NetworkException
- DatabaseException
- AuthenticationException
```

## Supported Android Versions

- **Minimum**: Android 9 (API 28)
- **Target**: Android 14 (API 34)
- **Compilation**: SDK 34

### Permissions
- `INTERNET` - API communication
- `READ_EXTERNAL_STORAGE` - Legacy image access
- `WRITE_EXTERNAL_STORAGE` - Legacy image save
- `READ_MEDIA_IMAGES` - Android 13+ image access

## Development Roadmap

### Phase 1: Foundation ✅
- [x] Project setup & Gradle configuration
- [x] Android Manifest & core resources
- [x] Application class & Hilt DI
- [x] Room database schema
- [x] Secure key storage
- [x] DALL-E 2 client integration
- [x] On-device content moderation
- [x] UI screens (Compose)
- [x] ViewModels with state management

### Phase 2: Features
- [ ] Cloud sync for authenticated users
- [ ] Server-side moderation API
- [ ] Image download & local storage
- [ ] Social media sharing (Intent-based)
- [ ] Advanced search & filtering
- [ ] Batch generation & queue system

### Phase 3: Polish
- [ ] Error handling edge cases
- [ ] Loading state UI improvements
- [ ] Offline mode enhancements
- [ ] Analytics integration
- [ ] Release build signing
- [ ] Testing (unit + instrumented)

## Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

### Manual Testing Checklist
- [ ] API key save/delete
- [ ] Image generation with valid prompt
- [ ] Content moderation rejection
- [ ] Gallery load and display
- [ ] Image favorite toggle
- [ ] Image deletion
- [ ] App theme switching
- [ ] Network error handling

## Building for Release

1. **Configure Signing** (`local.properties` or Gradle)
   ```properties
   RELEASE_STORE_FILE=path/to/keystore.jks
   RELEASE_STORE_PASSWORD=***
   RELEASE_KEY_ALIAS=release-key
   RELEASE_KEY_PASSWORD=***
   ```

2. **Build Release APK**
   ```bash
   ./gradlew assembleRelease
   ```

3. **Generate App Bundle**
   ```bash
   ./gradlew bundleRelease
   ```

## Debugging

### Enable Network Logging
```kotlin
// In NetworkModule, set HttpLoggingInterceptor level to BODY
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}
```

### View Database Content
- Use Android Studio Database Inspector
- Right-click app → Profilers → Database Inspector

### Logcat Filters
```bash
adb logcat | grep AnimGen
```

## Troubleshooting

### "API key not configured"
- Go to Settings
- Paste valid DALL-E 2 API key (starts with `sk-`)
- Restart app if needed

### "Content moderation failed"
- Prompt contains blocked keywords or patterns
- Avoid violence, sexual content, drug references
- Try a different prompt

### "Network error"
- Check internet connection
- Verify API key is valid
- Check DALL-E 2 API status

### Build Errors
- Clear Gradle cache: `./gradlew clean`
- Invalidate IDE cache: File → Invalidate Caches → Invalidate and Restart

## License

[Your License Here]

## Contributing

[Contribution guidelines]

## Support

For issues or questions, please file a GitHub issue or contact the development team.
