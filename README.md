# Maks Island

Maks Island is a premium-styled Android utility app inspired by Dynamic Island interactions, tuned for Google Pixel center punch-hole layouts.

## Stack
- Kotlin + Jetpack Compose + Material 3
- MVVM with `StateFlow`
- DataStore preferences
- `NotificationListenerService`
- Foreground overlay service (`TYPE_APPLICATION_OVERLAY`)

## Premium highlights in this build
- Upgraded island visual system with richer styling per state (idle, notification, media, charging, timer, urgent call).
- Refined spring/tween animation choreography for pill expansion, content transitions, and subtle persistent pulse behavior.
- Elite Settings redesign with section cards and deep controls across:
  - Appearance
  - Layout & Position
  - Animation
  - Behavior
  - Notifications
  - Privacy
  - Advanced
- Real-time live preview in Settings with instant scenario switching (idle/chat/media/charging/timer/urgent call).
- Improved Home control-center style layout with hero preview, quick scenarios, and demo action set.
- Polished per-app filter screen with search, row cards, status copy, and clean loading/empty states.

## Build in Android Studio
1. Open the project in Android Studio Iguana+.
2. Ensure Android SDK 34 is installed.
3. Sync Gradle.
4. Run on Pixel device/emulator (API 29+).

## Permissions setup
- Overlay permission (`Draw over other apps`)
- Notification access (`NotificationListenerService`)
- Optional battery optimization exemption for service reliability

## Notes about platform limits
Android does not allow third-party apps to universally replace system heads-up behavior. Maks Island mirrors and enhances notification presentation in an overlay where permissions and OEM behavior permit.
