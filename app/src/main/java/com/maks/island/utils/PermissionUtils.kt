package com.maks.island.utils

import android.content.Context
import android.os.PowerManager
import android.provider.Settings

object PermissionUtils {
    fun hasOverlayPermission(context: Context): Boolean = Settings.canDrawOverlays(context)

    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }
}
