package com.maks.island.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maks.island.domain.models.PreviewScenario
import com.maks.island.ui.components.ClickRow
import com.maks.island.ui.components.EnumChips
import com.maks.island.ui.components.IslandComposable
import com.maks.island.ui.components.PremiumSlider
import com.maks.island.ui.components.PremiumSwitch
import com.maks.island.ui.components.SectionCard
import com.maks.island.viewmodel.IslandViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(viewModel: IslandViewModel, onBack: () -> Unit, onAppFilters: () -> Unit) {
    val s by viewModel.settings.collectAsStateWithLifecycle()
    val liveState by viewModel.islandState.collectAsStateWithLifecycle()

    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Island Studio") }) }) { p ->
        Column(
            Modifier.fillMaxSize().padding(p).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SectionCard("Live Preview", "Changes are reflected instantly.") {
                IslandComposable(state = liveState, settings = s, isPreview = true, onLongPress = { viewModel.triggerPreviewScenario(PreviewScenario.UrgentCall) })
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PreviewScenario.entries.forEach { scenario ->
                        androidx.compose.material3.AssistChip(
                            onClick = { viewModel.triggerPreviewScenario(scenario) },
                            label = { Text(scenario.name) },
                        )
                    }
                }
            }

            SectionCard("Appearance") {
                PremiumSlider("Island width", s.width, 170f..340f) { viewModel.setFloat("width", it) }
                PremiumSlider("Island height", s.height, 38f..76f) { viewModel.setFloat("height", it) }
                PremiumSlider("Compact scale", s.compactScale, 0.7f..1.2f) { viewModel.setFloat("compact_scale", it) }
                PremiumSlider("Expanded scale", s.expandedScale, 0.9f..1.6f) { viewModel.setFloat("expanded_scale", it) }
                PremiumSlider("Corner radius", s.cornerRadius, 16f..40f) { viewModel.setFloat("radius", it) }
                PremiumSlider("Transparency", s.transparency, 0.5f..1f) { viewModel.setFloat("transparency", it) }
                PremiumSlider("Icon size", s.iconSize, 14f..30f) { viewModel.setFloat("icon", it) }
                PremiumSlider("Title text", s.titleTextSize, 12f..20f) { viewModel.setFloat("title_text", it) }
                PremiumSlider("Subtitle text", s.subtitleTextSize, 10f..18f) { viewModel.setFloat("subtitle_text", it) }
                PremiumSlider("Shadow intensity", s.shadowIntensity, 0f..1f) { viewModel.setFloat("shadow", it) }
                PremiumSlider("Glow intensity", s.glowIntensity, 0f..1f) { viewModel.setFloat("glow", it) }
                EnumChips(listOf("Default", "Glass", "Solid", "SoftContrast", "PureBlack"), s.backgroundStyle.name) { viewModel.setEnum("background_style", it) }
                EnumChips(listOf("Off", "Soft", "Frosted"), s.blurStyle.name) { viewModel.setEnum("blur_style", it) }
            }

            SectionCard("Layout & Position") {
                PremiumSlider("Top offset", s.topOffset, 8f..80f) { viewModel.setFloat("top_offset", it) }
                PremiumSlider("Horizontal offset", s.horizontalOffset, -80f..80f) { viewModel.setFloat("horizontal_offset", it) }
                PremiumSlider("Safe margin", s.safeMargin, 0f..32f) { viewModel.setFloat("safe_margin", it) }
                PremiumSwitch("Snap to camera", checked = s.snapToCamera) { viewModel.setBool("snap_to_camera", it) }
                PremiumSwitch("Free drag", checked = s.draggable) { viewModel.setBool("draggable", it) }
                ClickRow("Reset island position", "Reset") { viewModel.resetIslandPosition() }
            }

            SectionCard("Animation") {
                PremiumSlider("Animation speed", s.animationSpeed, 0.6f..1.6f) { viewModel.setFloat("anim", it) }
                PremiumSlider("Spring intensity", s.springiness, 0.3f..1f) { viewModel.setFloat("spring", it) }
                PremiumSwitch("Bounce on arrival", checked = s.bounceOnNotification) { viewModel.setBool("bounce", it) }
                PremiumSwitch("Soft fade mode", checked = s.softFadeMode) { viewModel.setBool("soft_fade", it) }
                PremiumSwitch("Persistent pulse", checked = s.persistentPulse) { viewModel.setBool("persistent_pulse", it) }
                PremiumSwitch("Media equalizer animation", checked = s.mediaEqualizerAnimation) { viewModel.setBool("media_eq", it) }
                PremiumSwitch("Haptic feedback", checked = s.hapticFeedback) { viewModel.setBool("haptics", it) }
            }

            SectionCard("Behavior") {
                PremiumSwitch("Enable island", checked = s.enabled) { viewModel.setBool("enabled", it) }
                PremiumSwitch("Start on boot", checked = s.startOnBoot) { viewModel.setBool("start_on_boot", it) }
                PremiumSwitch("Keep persistent island", checked = s.keepPersistentVisible) { viewModel.setBool("keep_persistent", it) }
                PremiumSwitch("Compact-only mode", checked = s.compactOnlyMode) { viewModel.setBool("compact_only", it) }
                PremiumSwitch("Expanded panel on long press", checked = s.longPressExpand) { viewModel.setBool("long_press", it) }
                PremiumSwitch("Swipe to dismiss", checked = s.swipeToDismiss) { viewModel.setBool("swipe_dismiss", it) }
                PremiumSwitch("Gaming mode", checked = s.gamingMode) { viewModel.setBool("gaming_mode", it) }
                PremiumSlider("Auto collapse (ms)", s.autoCollapseMillis.toFloat(), 1500f..8000f) { viewModel.setInt("collapse", it.toInt()) }
                ClickRow("Pause island for 1 hour", "Soon") {}
            }

            SectionCard("Notifications") {
                PremiumSwitch("Show preview text", checked = s.showPreview) { viewModel.setBool("preview", it) }
                PremiumSwitch("Hide sensitive content", checked = s.hideSensitive) { viewModel.setBool("hide_sensitive", it) }
                PremiumSwitch("App icon only mode", checked = s.appIconOnly) { viewModel.setBool("app_icon_only", it) }
                PremiumSwitch("Priority mode", checked = s.priorityNotifications) { viewModel.setBool("priority", it) }
                PremiumSwitch("Group repeated alerts", checked = s.grouping) { viewModel.setBool("grouping", it) }
                PremiumSwitch("Silent app behavior", checked = s.silentAppBehavior) { viewModel.setBool("silent_apps", it) }
                PremiumSlider("Alert cooldown (sec)", s.notificationCooldownSec.toFloat(), 1f..10f) { viewModel.setInt("cooldown", it.toInt()) }
                EnumChips(listOf("Minimal", "Balanced", "Detailed"), s.notificationPreset.name) { viewModel.setEnum("notification_preset", it) }
                ClickRow("Per-app controls", "Open") { onAppFilters() }
            }

            SectionCard("Privacy") {
                PremiumSwitch("Hide content on lock screen", checked = s.hideOnLockScreen) { viewModel.setBool("hide_on_lock", it) }
                PremiumSwitch("Private mode for messaging", checked = s.privateMessagingMode) { viewModel.setBool("private_messaging", it) }
                PremiumSwitch("Obscure OTP / codes", checked = s.obscureOtp) { viewModel.setBool("obscure_otp", it) }
                PremiumSwitch("App-name only mode", checked = s.appNameOnly) { viewModel.setBool("app_name_only", it) }
                PremiumSwitch("Require expansion for details", checked = s.requireExpansionForDetails) { viewModel.setBool("require_expand", it) }
            }

            SectionCard("Advanced") {
                PremiumSwitch("Debug bounds", checked = s.debugBounds) { viewModel.setBool("debug_bounds", it) }
                ClickRow("Battery optimization guide", "Open") {}
                ClickRow("Restart service", "Run") { viewModel.onAppLaunch() }
                ClickRow("Run all preview states", "Test") {
                    viewModel.showTestIslandNow()
                    PreviewScenario.entries.forEach(viewModel::triggerPreviewScenario)
                }
                ClickRow("Reset all settings", "Placeholder") {}
                ClickRow("Export/import settings", "Soon") {}
                ClickRow("Experimental features", "Placeholder") {}
            }

            SectionCard("About & Support") {
                Text("Maks Island by Maks")
                Text("Version 1.0.0 • Pixel-first dynamic island utility")
                ClickRow("Back", "Return") { onBack() }
            }
        }
    }
}
