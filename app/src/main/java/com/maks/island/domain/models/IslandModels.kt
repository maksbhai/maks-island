package com.maks.island.domain.models

import androidx.compose.ui.graphics.Color

enum class IslandBackgroundStyle { Default, Glass, Solid, SoftContrast, PureBlack }
enum class BlurStyle { Off, Soft, Frosted }
enum class NotificationStylePreset { Minimal, Balanced, Detailed }
enum class PreviewScenario { Idle, Notification, Media, Charging, Timer, UrgentCall }

data class IslandSettings(
    val enabled: Boolean = true,
    val width: Float = 228f,
    val height: Float = 46f,
    val compactScale: Float = 1f,
    val expandedScale: Float = 1f,
    val cornerRadius: Float = 28f,
    val topOffset: Float = 30f,
    val horizontalOffset: Float = 0f,
    val safeMargin: Float = 8f,
    val transparency: Float = 0.92f,
    val iconSize: Float = 20f,
    val titleTextSize: Float = 14f,
    val subtitleTextSize: Float = 12f,
    val shadowIntensity: Float = 0.45f,
    val glowIntensity: Float = 0.2f,
    val useMaterialYou: Boolean = true,
    val darkMode: Boolean = false,
    val pureBlackMode: Boolean = false,
    val backgroundStyle: IslandBackgroundStyle = IslandBackgroundStyle.Default,
    val blurStyle: BlurStyle = BlurStyle.Soft,
    val animationSpeed: Float = 1f,
    val springiness: Float = 0.75f,
    val bounceOnNotification: Boolean = true,
    val softFadeMode: Boolean = true,
    val persistentPulse: Boolean = true,
    val mediaEqualizerAnimation: Boolean = true,
    val hapticFeedback: Boolean = true,
    val startOnBoot: Boolean = false,
    val draggable: Boolean = true,
    val snapToCamera: Boolean = true,
    val autoCollapseMillis: Int = 3500,
    val longPressExpand: Boolean = true,
    val keepPersistentVisible: Boolean = true,
    val compactOnlyMode: Boolean = false,
    val swipeToDismiss: Boolean = true,
    val gamingMode: Boolean = false,
    val lockScreenBehavior: Boolean = false,
    val showPreview: Boolean = true,
    val hideSensitive: Boolean = false,
    val appIconOnly: Boolean = false,
    val priorityNotifications: Boolean = true,
    val grouping: Boolean = true,
    val silentAppBehavior: Boolean = true,
    val notificationCooldownSec: Int = 3,
    val notificationPreset: NotificationStylePreset = NotificationStylePreset.Balanced,
    val hideOnLockScreen: Boolean = false,
    val privateMessagingMode: Boolean = false,
    val obscureOtp: Boolean = true,
    val appNameOnly: Boolean = false,
    val requireExpansionForDetails: Boolean = false,
    val debugBounds: Boolean = false,
    val islandX: Float = 0f,
    val islandY: Float = 0f,
)

sealed interface IslandVisualState {
    data object Idle : IslandVisualState
    data class Notification(val item: NotificationItem) : IslandVisualState
    data class Media(val nowPlaying: MediaState) : IslandVisualState
    data class Charging(val batteryPct: Int, val isFull: Boolean) : IslandVisualState
    data class Timer(val timerState: TimerState) : IslandVisualState
    data class Call(val callState: CallState) : IslandVisualState
    data class Expanded(val base: IslandVisualState) : IslandVisualState
}

data class NotificationItem(
    val packageName: String,
    val appName: String,
    val title: String,
    val body: String,
    val postedAt: Long = System.currentTimeMillis(),
)

data class MediaState(
    val title: String,
    val artist: String,
    val isPlaying: Boolean,
    val progress: Float,
    val colorHint: Color = Color(0xFF95A8FF),
)

data class TimerState(
    val totalSeconds: Int,
    val remainingSeconds: Int,
    val isPaused: Boolean,
)

data class CallState(
    val caller: String,
    val durationSec: Int,
    val isIncoming: Boolean,
)
