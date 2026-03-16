package com.maks.island.utils

import android.content.Context
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils

object PermissionUtils {
    fun hasOverlayPermission(context: Context): Boolean = Settings.canDrawOverlays(context)

    fun hasNotificationListenerAccess(context: Context): Boolean {
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners",
        ) ?: return false
        return enabledListeners.split(":").any { component ->
            !TextUtils.isEmpty(component) && component.contains(context.packageName)
        }
    }

    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }
}
