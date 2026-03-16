package com.maks.island.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maks.island.domain.models.IslandBackgroundStyle
import com.maks.island.domain.models.IslandSettings
import com.maks.island.domain.models.IslandVisualState

@Composable
fun IslandComposable(
    state: IslandVisualState,
    settings: IslandSettings,
    modifier: Modifier = Modifier,
    isPreview: Boolean = false,
    onLongPress: () -> Unit = {},
) {
    val compact = state is IslandVisualState.Idle
    val targetWidth = when (state) {
        IslandVisualState.Idle -> settings.width.dp * settings.compactScale
        is IslandVisualState.Expanded -> (settings.width * 1.45f * settings.expandedScale).dp
        else -> (settings.width * 1.25f).dp
    }
    val targetHeight = if (state is IslandVisualState.Expanded) (settings.height * 2.5f).dp else settings.height.dp

    val width by animateDpAsState(targetWidth, spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 400f * settings.animationSpeed))
    val height by animateDpAsState(targetHeight, spring(dampingRatio = 0.78f, stiffness = 380f * settings.animationSpeed))
    val glow by animateFloatAsState(if (compact) settings.glowIntensity * 0.45f else settings.glowIntensity)
    val pulse = if (!compact && settings.persistentPulse) {
        rememberInfiniteTransition(label = "pulse").animateFloat(
            initialValue = 0.78f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(1300, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "p"
        ).value
    } else 1f

    val islandShape = RoundedCornerShape(settings.cornerRadius.dp)

    Box(modifier.fillMaxWidth().padding(top = if (isPreview) 8.dp else settings.topOffset.dp), contentAlignment = Alignment.TopCenter) {
        Box(
            Modifier
                .width(width)
                .height(height)
                .shadow((18f * settings.shadowIntensity + 6f).dp, islandShape)
                .background(backgroundBrush(settings, state, pulse), islandShape)
                .border(1.dp, Color.White.copy(alpha = 0.08f + glow * 0.15f), islandShape)
                .pointerInput(Unit) { detectTapGestures(onLongPress = { onLongPress() }) }
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            AnimatedContent(targetState = state, label = "island") { current ->
                when (current) {
                    IslandVisualState.Idle -> IdleContent(settings)
                    is IslandVisualState.Notification -> NotificationContent(current.item.appName, current.item.title, current.item.body, settings)
                    is IslandVisualState.Media -> NotificationContent("Now Playing", current.nowPlaying.title, current.nowPlaying.artist, settings, Icons.Default.MusicNote)
                    is IslandVisualState.Charging -> NotificationContent("Battery", "Charging ${current.batteryPct}%", if (current.isFull) "Fully charged" else "Fast charging", settings, Icons.Default.BatteryChargingFull)
                    is IslandVisualState.Timer -> NotificationContent("Timer", formatTimer(current.timerState.remainingSeconds), if (current.timerState.isPaused) "Paused" else "Active", settings, Icons.Default.Timer)
                    is IslandVisualState.Call -> NotificationContent("Incoming call", current.callState.caller, "Tap to view call controls", settings, Icons.Default.Call, urgent = true)
                    is IslandVisualState.Expanded -> ExpandedContent(current.base, settings)
                }
            }
            AnimatedVisibility(!compact, Modifier.align(Alignment.BottomEnd)) {
                Text("Hold for controls", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun IdleContent(settings: IslandSettings) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        val shimmer = rememberInfiniteTransition(label = "idle").animateFloat(0.4f, 0.95f, infiniteRepeatable(tween(1500), RepeatMode.Reverse), label = "s")
        Box(Modifier.size(settings.iconSize.dp * 0.45f).background(MaterialTheme.colorScheme.primary.copy(alpha = shimmer.value), CircleShape))
        Spacer(Modifier.width(10.dp))
        Text("Maks Island", color = Color.White.copy(alpha = 0.92f), fontSize = settings.titleTextSize.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun NotificationContent(
    title: String,
    headline: String,
    subtitle: String,
    settings: IslandSettings,
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.Notifications,
    urgent: Boolean = false,
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            Modifier
                .size(settings.iconSize.dp + 8.dp)
                .background((if (urgent) Color(0xFFFF4D4D) else Color.White).copy(alpha = 0.14f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = if (urgent) Color(0xFFFFD0D0) else Color.White.copy(alpha = 0.95f), modifier = Modifier.size(settings.iconSize.dp))
        }
        Box(Modifier.weight(1f)) {
            Crossfade(targetState = headline, label = "headline") {
                ColumnText(title, it, subtitle, settings, urgent)
            }
        }
    }
}

@Composable
private fun ColumnText(title: String, headline: String, subtitle: String, settings: IslandSettings, urgent: Boolean) {
    androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, color = Color.White.copy(alpha = 0.7f), fontSize = settings.subtitleTextSize.sp)
        Text(headline, color = if (urgent) Color(0xFFFFF1F1) else Color.White, fontSize = settings.titleTextSize.sp, maxLines = 1, fontWeight = FontWeight.SemiBold)
        Text(subtitle, color = Color.White.copy(alpha = 0.66f), fontSize = settings.subtitleTextSize.sp, maxLines = 1)
    }
}

@Composable
private fun ExpandedContent(base: IslandVisualState, settings: IslandSettings) {
    androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Maks Island Controls", color = Color.White, fontWeight = FontWeight.Bold, fontSize = (settings.titleTextSize + 1).sp)
        when (base) {
            is IslandVisualState.Media -> NotificationContent("Playback", base.nowPlaying.title, base.nowPlaying.artist, settings, Icons.Default.MusicNote)
            is IslandVisualState.Call -> NotificationContent("Urgent", base.callState.caller, "Swipe to dismiss", settings, Icons.Default.Call, urgent = true)
            else -> NotificationContent("Activity", "Expanded panel", "Quick controls ready", settings)
        }
    }
}

private fun backgroundBrush(settings: IslandSettings, state: IslandVisualState, pulse: Float): Brush {
    val base = when (settings.backgroundStyle) {
        IslandBackgroundStyle.Default -> listOf(Color(0xFF0F1116), Color(0xFF141923))
        IslandBackgroundStyle.Glass -> listOf(Color(0xFF202A3B).copy(alpha = 0.88f), Color(0xFF161D2A).copy(alpha = 0.8f))
        IslandBackgroundStyle.Solid -> listOf(Color(0xFF111111), Color(0xFF111111))
        IslandBackgroundStyle.SoftContrast -> listOf(Color(0xFF1C1A2A), Color(0xFF0C111E))
        IslandBackgroundStyle.PureBlack -> listOf(Color.Black, Color.Black)
    }
    val accent = when (state) {
        is IslandVisualState.Call -> Color(0x99FF4D4D)
        is IslandVisualState.Media -> Color(0x9989A9FF)
        is IslandVisualState.Charging -> Color(0x9974F7B0)
        is IslandVisualState.Timer -> Color(0x99FFCB66)
        is IslandVisualState.Notification -> Color(0x99A2B6FF)
        else -> Color.Transparent
    }
    return Brush.linearGradient(listOf(base.first.copy(alpha = settings.transparency * pulse), base.last.copy(alpha = settings.transparency), accent))
}

private fun formatTimer(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%02d:%02d".format(m, s)
}
