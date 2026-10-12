# Cloud Synchronization

## Overview
The cloud synchronization system allows users to optionally back up their generated anime images to the cloud and sync across devices. This is completely optional - users can use the app fully offline with just a local DALL-E API key.

## Architecture

### Components

#### SyncScheduler (`sync/CloudSyncWorker.kt`)
Manages background sync scheduling using Android WorkManager:
- **Periodic sync**: Runs every 15 minutes when battery is not critically low
- **Immediate sync**: One-time manual sync triggered by user or app initialization
- **Constraints**: Requires minimum battery level, no device idle requirement

#### CloudSyncViewModel (`viewmodel/CloudSyncViewModel.kt`)
Manages UI state for sync operations:
- **Sync Status**: Current sync state (syncing, idle, error)
- **Last Sync Time**: Timestamp of last successful sync
- **Image Counts**: Tracks synced and pending images
- **Auto-sync Toggle**: User control for periodic background sync
- **Error Handling**: User-friendly error messages

#### CloudSyncRepository
(Previously implemented in Phase 1)
Coordinates sync operations:
- `syncImages()`: Upload pending images to cloud
- `getSyncStatus()`: Retrieve current sync state
- Handles conflict resolution
- Manages sync metadata

#### SyncPanel (`ui/components/SyncPanel.kt`)
Composable UI for sync controls in Settings:
- Sync status display with progress indicator
- Last sync time
- Synced/pending image counts
- Auto-sync toggle switch
- Sync action buttons (manual sync, force sync)
- Error message display

### Data Flow

```
User Action (SettingsScreen)
         ↓
    SyncPanel (UI)
         ↓
CloudSyncViewModel (State)
         ↓
SyncScheduler (Scheduling)
         ↓
CloudSyncWorker (Background)
         ↓
CloudSyncRepository (Logic)
         ↓
CloudService (API)
```

## Usage

### Manual Sync
```kotlin
// In Settings, user taps "Sync Now"
viewModel.performSync()

// ViewMode updates UI state to show loading
// CloudSyncRepository performs sync
// UI updates with result
```

### Background Sync
```kotlin
// User enables auto-sync in Settings
viewModel.toggleAutoSync(enabled = true)

// SyncScheduler starts periodic WorkManager task
// Runs every 15 minutes in background
// WorkManager respects device state (battery, connectivity)
```

### Force Sync (Highest Priority)
```kotlin
// Developer or crash recovery
viewModel.forceImmediateSync()

// Bypasses normal scheduling
// Executes immediately
// Useful for critical sync scenarios
```

## Configuration

### WorkManager Constraints
Currently configured in `SyncScheduler.schedulePeriodicSync()`:
```kotlin
.setConstraints(
    Constraints.Builder()
        .setRequiresBatteryNotLow(true)  // Don't sync if <15% battery
        .build()
)
```

### Sync Interval
Default: 15 minutes
- Can be adjusted in `SyncScheduler` companion object
- Consider Play Store policies before changing

### Retry Policy
- Failed syncs automatically retry via WorkManager
- ExistingPeriodicWorkPolicy.KEEP prevents duplicate scheduling

## Integration Points

### Dependency Injection
```kotlin
// In SyncModule
@Provides
@Singleton
fun provideSyncScheduler(
    @ApplicationContext context: Context
): SyncScheduler = SyncScheduler(context)
```

### In SettingsScreen
```kotlin
// Add to existing settings UI
SyncPanel()  // Includes all sync controls
```

## Future Enhancements

### Phase 2
- [ ] Selective sync (choose which images to sync)
- [ ] Bandwidth optimization (WiFi-only sync option)
- [ ] Conflict resolution UI for duplicate images
- [ ] Sync statistics and analytics
- [ ] Export sync logs for debugging

### Phase 3
- [ ] Delta sync (only upload changed images)
- [ ] Compression before upload
- [ ] Resume sync on interruption
- [ ] End-to-end encryption for sensitive content
- [ ] Sync across multiple user devices

## Testing

### Unit Tests
```bash
./gradlew test
```

Includes:
- `CloudSyncViewModelTest`: ViewModel state and methods
- Auto-sync toggle verification
- Error message clearing
- Initial status loading

### Integration Tests
```bash
./gradlew connectedAndroidTest
```

Tests cloud sync integration with database and network layers.

## Troubleshooting

### Sync not running
- Check WorkManager status: `adb shell dumpsys jobscheduler | grep cloud_sync`
- Verify auto-sync is enabled in Settings
- Check battery level (must be above 15%)

### Sync failures
- Check network connectivity
- Verify API credentials
- Review error message in Settings UI
- Check Logcat for detailed error logs

### WorkManager constraints not met
- Device must have sufficient battery
- No network connectivity: will retry when available
- Device is fully offline: manual sync will also fail

## Dependencies
- `androidx.work:work-runtime-ktx:2.8.1`
- Existing: Hilt, Retrofit, Coroutines, Room

## Known Limitations
- Maximum image size per sync: TBD (defined by cloud backend)
- Sync timeout: 30 seconds (configurable)
- Concurrent sync: Only one sync job runs at a time
- Device offline: Sync queued until online

## API Reference

### CloudSyncViewModel
```kotlin
fun performSync()                    // Manual sync
fun toggleAutoSync(enabled: Boolean) // Enable/disable periodic sync
fun forceImmediateSync()            // High-priority immediate sync
fun clearError()                     // Clear error message
fun refreshSyncStatus()              // Refresh UI state
```

### SyncScheduler
```kotlin
fun schedulePeriodicSync()          // Start background sync
fun cancelPeriodicSync()            // Stop background sync
fun forceImmediateSync()            // One-time immediate sync
fun isSyncScheduled(): Boolean      // Check if sync is active
```

## References
- [Android WorkManager Documentation](https://developer.android.com/reference/androidx/work/WorkManager)
- [Jetpack Compose State Management](https://developer.android.com/jetpack/compose/state)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
