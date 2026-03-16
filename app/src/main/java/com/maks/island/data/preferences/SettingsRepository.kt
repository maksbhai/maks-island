package com.maks.island.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.maks.island.domain.models.BlurStyle
import com.maks.island.domain.models.IslandBackgroundStyle
import com.maks.island.domain.models.IslandSettings
import com.maks.island.domain.models.NotificationStylePreset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "maks_island_settings")

class SettingsRepository(private val context: Context) {
    private object Keys {
        val onboardingSeen = booleanPreferencesKey("onboarding_seen")
        val enabled = booleanPreferencesKey("enabled")
        val width = floatPreferencesKey("width")
        val height = floatPreferencesKey("height")
        val compactScale = floatPreferencesKey("compact_scale")
        val expandedScale = floatPreferencesKey("expanded_scale")
        val radius = floatPreferencesKey("radius")
        val topOffset = floatPreferencesKey("top_offset")
        val horizontalOffset = floatPreferencesKey("horizontal_offset")
        val safeMargin = floatPreferencesKey("safe_margin")
        val transparency = floatPreferencesKey("transparency")
        val iconSize = floatPreferencesKey("icon_size")
        val titleTextSize = floatPreferencesKey("title_text_size")
        val subtitleTextSize = floatPreferencesKey("subtitle_text_size")
        val shadowIntensity = floatPreferencesKey("shadow_intensity")
        val glowIntensity = floatPreferencesKey("glow_intensity")
        val useMaterialYou = booleanPreferencesKey("material_you")
        val darkMode = booleanPreferencesKey("dark_mode")
        val pureBlack = booleanPreferencesKey("pure_black")
        val backgroundStyle = stringPreferencesKey("background_style")
        val blurStyle = stringPreferencesKey("blur_style")
        val animSpeed = floatPreferencesKey("anim_speed")
        val springiness = floatPreferencesKey("springiness")
        val bounce = booleanPreferencesKey("bounce")
        val softFade = booleanPreferencesKey("soft_fade")
        val persistentPulse = booleanPreferencesKey("persistent_pulse")
        val mediaEq = booleanPreferencesKey("media_eq")
        val haptics = booleanPreferencesKey("haptics")
        val startOnBoot = booleanPreferencesKey("start_on_boot")
        val draggable = booleanPreferencesKey("draggable")
        val snapToCamera = booleanPreferencesKey("snap_to_camera")
        val autoCollapse = intPreferencesKey("auto_collapse")
        val longPress = booleanPreferencesKey("long_press")
        val keepPersistent = booleanPreferencesKey("keep_persistent")
        val compactOnly = booleanPreferencesKey("compact_only")
        val swipeDismiss = booleanPreferencesKey("swipe_dismiss")
        val gamingMode = booleanPreferencesKey("gaming_mode")
        val lockScreenBehavior = booleanPreferencesKey("lock_screen_behavior")
        val showPreview = booleanPreferencesKey("show_preview")
        val hideSensitive = booleanPreferencesKey("hide_sensitive")
        val appIconOnly = booleanPreferencesKey("app_icon_only")
        val priority = booleanPreferencesKey("priority")
        val grouping = booleanPreferencesKey("grouping")
        val silentAppBehavior = booleanPreferencesKey("silent_apps")
        val cooldownSec = intPreferencesKey("cooldown")
        val notificationPreset = stringPreferencesKey("notif_preset")
        val hideOnLockScreen = booleanPreferencesKey("hide_on_lock")
        val privateMessagingMode = booleanPreferencesKey("private_messaging")
        val obscureOtp = booleanPreferencesKey("obscure_otp")
        val appNameOnly = booleanPreferencesKey("app_name_only")
        val requireExpansion = booleanPreferencesKey("require_expand")
        val debugBounds = booleanPreferencesKey("debug_bounds")
        val islandX = floatPreferencesKey("island_x")
        val islandY = floatPreferencesKey("island_y")
        val appAllowList = stringSetPreferencesKey("app_allow_list")
    }

    val settings: Flow<IslandSettings> = context.dataStore.data.map(::mapSettings)
    val onboardingSeen: Flow<Boolean> = context.dataStore.data.map { it[Keys.onboardingSeen] ?: false }
    val appAllowList: Flow<Set<String>> = context.dataStore.data.map { it[Keys.appAllowList] ?: emptySet() }

    suspend fun setOnboardingSeen() = context.dataStore.edit { it[Keys.onboardingSeen] = true }

    suspend fun updateFloat(key: String, value: Float) = context.dataStore.edit {
        when (key) {
            "width" -> it[Keys.width] = value
            "height" -> it[Keys.height] = value
            "compact_scale" -> it[Keys.compactScale] = value
            "expanded_scale" -> it[Keys.expandedScale] = value
            "radius" -> it[Keys.radius] = value
            "top_offset" -> it[Keys.topOffset] = value
            "horizontal_offset" -> it[Keys.horizontalOffset] = value
            "safe_margin" -> it[Keys.safeMargin] = value
            "transparency" -> it[Keys.transparency] = value
            "icon" -> it[Keys.iconSize] = value
            "title_text" -> it[Keys.titleTextSize] = value
            "subtitle_text" -> it[Keys.subtitleTextSize] = value
            "shadow" -> it[Keys.shadowIntensity] = value
            "glow" -> it[Keys.glowIntensity] = value
            "anim" -> it[Keys.animSpeed] = value
            "spring" -> it[Keys.springiness] = value
            "x" -> it[Keys.islandX] = value
            "y" -> it[Keys.islandY] = value
        }
    }

    suspend fun updateInt(key: String, value: Int) = context.dataStore.edit {
        when (key) {
            "collapse" -> it[Keys.autoCollapse] = value
            "cooldown" -> it[Keys.cooldownSec] = value
        }
    }

    suspend fun updateBool(key: String, value: Boolean) = context.dataStore.edit {
        when (key) {
            "enabled" -> it[Keys.enabled] = value
            "material_you" -> it[Keys.useMaterialYou] = value
            "dark" -> it[Keys.darkMode] = value
            "pure_black" -> it[Keys.pureBlack] = value
            "bounce" -> it[Keys.bounce] = value
            "soft_fade" -> it[Keys.softFade] = value
            "persistent_pulse" -> it[Keys.persistentPulse] = value
            "media_eq" -> it[Keys.mediaEq] = value
            "haptics" -> it[Keys.haptics] = value
            "start_on_boot" -> it[Keys.startOnBoot] = value
            "draggable" -> it[Keys.draggable] = value
            "snap_to_camera" -> it[Keys.snapToCamera] = value
            "long_press" -> it[Keys.longPress] = value
            "keep_persistent" -> it[Keys.keepPersistent] = value
            "compact_only" -> it[Keys.compactOnly] = value
            "swipe_dismiss" -> it[Keys.swipeDismiss] = value
            "gaming_mode" -> it[Keys.gamingMode] = value
            "lock_screen_behavior" -> it[Keys.lockScreenBehavior] = value
            "preview" -> it[Keys.showPreview] = value
            "hide_sensitive" -> it[Keys.hideSensitive] = value
            "app_icon_only" -> it[Keys.appIconOnly] = value
            "priority" -> it[Keys.priority] = value
            "grouping" -> it[Keys.grouping] = value
            "silent_apps" -> it[Keys.silentAppBehavior] = value
            "hide_on_lock" -> it[Keys.hideOnLockScreen] = value
            "private_messaging" -> it[Keys.privateMessagingMode] = value
            "obscure_otp" -> it[Keys.obscureOtp] = value
            "app_name_only" -> it[Keys.appNameOnly] = value
            "require_expand" -> it[Keys.requireExpansion] = value
            "debug_bounds" -> it[Keys.debugBounds] = value
        }
    }

    suspend fun updateEnum(key: String, value: String) = context.dataStore.edit {
        when (key) {
            "background_style" -> it[Keys.backgroundStyle] = value
            "blur_style" -> it[Keys.blurStyle] = value
            "notification_preset" -> it[Keys.notificationPreset] = value
        }
    }

    suspend fun setAppAllowed(packageName: String, allowed: Boolean) = context.dataStore.edit {
        val mutable = (it[Keys.appAllowList] ?: emptySet()).toMutableSet()
        if (allowed) mutable.add(packageName) else mutable.remove(packageName)
        it[Keys.appAllowList] = mutable
    }

    private fun mapSettings(p: Preferences): IslandSettings = IslandSettings(
        enabled = p[Keys.enabled] ?: true,
        width = p[Keys.width] ?: 228f,
        height = p[Keys.height] ?: 46f,
        compactScale = p[Keys.compactScale] ?: 1f,
        expandedScale = p[Keys.expandedScale] ?: 1f,
        cornerRadius = p[Keys.radius] ?: 28f,
        topOffset = p[Keys.topOffset] ?: 30f,
        horizontalOffset = p[Keys.horizontalOffset] ?: 0f,
        safeMargin = p[Keys.safeMargin] ?: 8f,
        transparency = p[Keys.transparency] ?: 0.92f,
        iconSize = p[Keys.iconSize] ?: 20f,
        titleTextSize = p[Keys.titleTextSize] ?: 14f,
        subtitleTextSize = p[Keys.subtitleTextSize] ?: 12f,
        shadowIntensity = p[Keys.shadowIntensity] ?: 0.45f,
        glowIntensity = p[Keys.glowIntensity] ?: 0.2f,
        useMaterialYou = p[Keys.useMaterialYou] ?: true,
        darkMode = p[Keys.darkMode] ?: false,
        pureBlackMode = p[Keys.pureBlack] ?: false,
        backgroundStyle = p[Keys.backgroundStyle]?.let { runCatching { IslandBackgroundStyle.valueOf(it) }.getOrNull() }
            ?: IslandBackgroundStyle.Default,
        blurStyle = p[Keys.blurStyle]?.let { runCatching { BlurStyle.valueOf(it) }.getOrNull() } ?: BlurStyle.Soft,
        animationSpeed = p[Keys.animSpeed] ?: 1f,
        springiness = p[Keys.springiness] ?: 0.75f,
        bounceOnNotification = p[Keys.bounce] ?: true,
        softFadeMode = p[Keys.softFade] ?: true,
        persistentPulse = p[Keys.persistentPulse] ?: true,
        mediaEqualizerAnimation = p[Keys.mediaEq] ?: true,
        hapticFeedback = p[Keys.haptics] ?: true,
        startOnBoot = p[Keys.startOnBoot] ?: false,
        draggable = p[Keys.draggable] ?: true,
        snapToCamera = p[Keys.snapToCamera] ?: true,
        autoCollapseMillis = p[Keys.autoCollapse] ?: 3500,
        longPressExpand = p[Keys.longPress] ?: true,
        keepPersistentVisible = p[Keys.keepPersistent] ?: true,
        compactOnlyMode = p[Keys.compactOnly] ?: false,
        swipeToDismiss = p[Keys.swipeDismiss] ?: true,
        gamingMode = p[Keys.gamingMode] ?: false,
        lockScreenBehavior = p[Keys.lockScreenBehavior] ?: false,
        showPreview = p[Keys.showPreview] ?: true,
        hideSensitive = p[Keys.hideSensitive] ?: false,
        appIconOnly = p[Keys.appIconOnly] ?: false,
        priorityNotifications = p[Keys.priority] ?: true,
        grouping = p[Keys.grouping] ?: true,
        silentAppBehavior = p[Keys.silentAppBehavior] ?: true,
        notificationCooldownSec = p[Keys.cooldownSec] ?: 3,
        notificationPreset = p[Keys.notificationPreset]?.let { runCatching { NotificationStylePreset.valueOf(it) }.getOrNull() }
            ?: NotificationStylePreset.Balanced,
        hideOnLockScreen = p[Keys.hideOnLockScreen] ?: false,
        privateMessagingMode = p[Keys.privateMessagingMode] ?: false,
        obscureOtp = p[Keys.obscureOtp] ?: true,
        appNameOnly = p[Keys.appNameOnly] ?: false,
        requireExpansionForDetails = p[Keys.requireExpansion] ?: false,
        debugBounds = p[Keys.debugBounds] ?: false,
        islandX = p[Keys.islandX] ?: 0f,
        islandY = p[Keys.islandY] ?: 0f,
    )
}
